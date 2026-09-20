/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.batch.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItem
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItemStatus
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchRun
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job

enum class BatchDecision {
    Retry, Skip, Cancel
}

class BatchComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _items = mutableStateOf<List<BatchItem>>(emptyList())
    val items by _items

    private val _targetWidth = mutableIntStateOf(0)
    val targetWidth by _targetWidth

    private val _targetHeight = mutableIntStateOf(0)
    val targetHeight by _targetHeight

    private val _targetFormat = mutableStateOf<ImageFormat?>(null)
    val targetFormat by _targetFormat

    private val _qualityValue = mutableIntStateOf(100)
    val qualityValue by _qualityValue

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isSaving = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done = mutableIntStateOf(0)
    val done by _done

    private val _failedItem = mutableStateOf<BatchItem?>(null)
    val failedItem by _failedItem

    private val isAlwaysClearExif: Boolean
        get() = settingsManager.settingsState.value.isAlwaysClearExif

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
        _failedItem.update { null }
    }

    private var failureDecision: CompletableDeferred<BatchDecision>? = null

    fun updateUris(uris: List<Uri>?) {
        if (_isSaving.value) return
        _uris.update { uris }
        _items.update { BatchRun.initialItems(uris.orEmpty().distinct().map(Uri::toString)) }
        _done.value = 0
        _failedItem.update { null }
    }

    fun setTargetWidth(width: Int) {
        _targetWidth.value = width.coerceAtLeast(0)
    }

    fun setTargetHeight(height: Int) {
        _targetHeight.value = height.coerceAtLeast(0)
    }

    fun setTargetFormat(format: ImageFormat?) {
        _targetFormat.update { format }
    }

    fun setQualityValue(quality: Int) {
        _qualityValue.value = quality.coerceIn(1, 100)
    }

    fun setKeepExif(keep: Boolean) {
        _keepExif.update { keep }
    }

    fun runBatch(oneTimeSaveLocationUri: String?) {
        if (_items.value.isEmpty() || _isSaving.value) return
        savingJob = trackProgress {
            _isSaving.update { true }
            _failedItem.update { null }
            var items = BatchRun.initialItems(_items.value.map { it.uri })
            _items.update { items }
            _done.value = 0

            try {
                var index = 0
                while (index < items.size) {
                    val item = items[index]
                    items = BatchRun.markRunning(items, item.uri)
                    _items.update { items }

                    val result = runSuspendCatching {
                        processItem(
                            uri = item.uri,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                            sequenceNumber = index + 1
                        )
                    }.getOrElse { SaveResult.Error.Exception(it) }

                    when (result) {
                        is SaveResult.Success,
                        is SaveResult.Skipped -> {
                            items = BatchRun.markDone(items, item.uri)
                            _items.update { items }
                        }

                        is SaveResult.Error -> {
                            items = BatchRun.markFailed(
                                items = items,
                                uri = item.uri,
                                error = result.throwable.message ?: result.throwable.toString()
                            )
                            _items.update { items }
                            when (awaitFailureDecision(item)) {
                                BatchDecision.Retry -> {
                                    items = BatchRun.retry(items, item.uri)
                                    _items.update { items }
                                    continue
                                }

                                BatchDecision.Skip -> Unit
                                BatchDecision.Cancel -> return@trackProgress
                            }
                        }
                    }

                    _done.value = BatchRun.doneCount(items)
                    updateProgress(
                        done = done,
                        total = BatchRun.totalCount(items)
                    )
                    index++
                }
            } finally {
                _isSaving.update { false }
                _failedItem.update { null }
                failureDecision?.complete(BatchDecision.Cancel)
                failureDecision = null
            }
        }
    }

    fun resolveFailure(decision: BatchDecision) {
        failureDecision?.complete(decision)
        failureDecision = null
    }

    fun cancelSaving() {
        resolveFailure(BatchDecision.Cancel)
        _isSaving.update { false }
        savingJob?.cancel()
        savingJob = null
    }

    private suspend fun awaitFailureDecision(item: BatchItem): BatchDecision {
        val deferred = CompletableDeferred<BatchDecision>()
        failureDecision = deferred
        _failedItem.update { _items.value.firstOrNull { it.uri == item.uri } }
        return deferred.await()
    }

    private suspend fun processItem(
        uri: String,
        oneTimeSaveLocationUri: String?,
        sequenceNumber: Int
    ): SaveResult {
        val imageData = imageGetter.getImage(uri, originalSize = true)
            ?: return SaveResult.Error.Exception(IllegalStateException("Cannot read image: $uri"))
        val bitmap = imageData.image
        val originalInfo = imageData.imageInfo

        val requestedWidth = targetWidth.takeIf { it > 0 }
        val requestedHeight = targetHeight.takeIf { it > 0 }
        val width = requestedWidth ?: requestedHeight?.let {
            (it.toLong() * bitmap.width / bitmap.height).toInt().coerceAtLeast(1)
        } ?: bitmap.width
        val height = requestedHeight ?: requestedWidth?.let {
            (it.toLong() * bitmap.height / bitmap.width).toInt().coerceAtLeast(1)
        } ?: bitmap.height

        val info = originalInfo.copy(
            width = width,
            height = height,
            imageFormat = targetFormat ?: originalInfo.imageFormat,
            quality = if (targetFormat != null) {
                Quality.Base(qualityValue)
            } else {
                originalInfo.quality
            },
            originalUri = uri
        )

        return fileController.save(
            saveTarget = ImageSaveTarget(
                imageInfo = info,
                originalUri = uri,
                sequenceNumber = sequenceNumber,
                metadata = null,
                data = imageCompressor.compressAndTransform(
                    image = bitmap,
                    imageInfo = info
                ),
                canSkipIfLarger = false
            ),
            keepOriginalMetadata = keepExif && !isAlwaysClearExif,
            oneTimeSaveLocationUri = oneTimeSaveLocationUri
        )
    }

    val hasPendingWork: Boolean
        get() = items.any { it.status != BatchItemStatus.Done }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): BatchComponent
    }
}
