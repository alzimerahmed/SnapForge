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

package com.t8rin.imagetoolbox.feature.batch.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CheckCircle
import com.t8rin.imagetoolbox.core.resources.icons.Error
import com.t8rin.imagetoolbox.core.resources.icons.MultipleImageEdit
import com.t8rin.imagetoolbox.core.resources.icons.Refresh
import com.t8rin.imagetoolbox.core.resources.icons.Schedule
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageCounter
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItem
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItemStatus
import com.t8rin.imagetoolbox.feature.batch.presentation.screenLogic.BatchComponent
import com.t8rin.imagetoolbox.feature.batch.presentation.screenLogic.BatchDecision

@Composable
fun BatchContent(
    component: BatchComponent
) {
    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.updateUris(uris)
    }

    val pickImage = imagePicker::pickImage

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val saveBatch: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.runBatch(it)
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.batch_tool),
                input = null,
                isLoading = false,
                size = null
            )
        },
        onGoBack = {
            if (component.uris?.isNotEmpty() == true) showExitDialog = true
            else component.onGoBack()
        },
        actions = {},
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        imagePreview = {
            BatchItemsList(items = component.items)
        },
        controls = {
            ImageCounter(
                imageCount = component.uris?.size?.takeIf { it > 1 },
                onRepick = pickImage
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResizeImageField(
                imageInfo = ImageInfo(
                    width = component.targetWidth,
                    height = component.targetHeight
                ),
                originalSize = null,
                onWidthChange = component::setTargetWidth,
                onHeightChange = component::setTargetHeight,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ImageFormatSelector(
                value = component.targetFormat,
                quality = Quality.Base(component.qualityValue),
                onValueChange = component::setTargetFormat,
                onAutoClick = { component.setTargetFormat(null) },
                autoText = stringResource(R.string.batch_format_keep_original),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            QualitySelector(
                imageFormat = component.targetFormat ?: ImageFormat.Default,
                quality = Quality.Base(component.qualityValue),
                onQualityChange = { quality ->
                    component.setQualityValue(quality.qualityValue)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = { component.setKeepExif(!component.keepExif) },
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.batch_keep_exif),
                subtitle = stringResource(R.string.batch_keep_exif_sub),
                shape = ShapeDefaults.extraLarge,
                startIcon = Icons.Outlined.MultipleImageEdit
            )
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable { mutableStateOf(false) }
            var showOneTimeImagePickingDialog by rememberSaveable { mutableStateOf(false) }
            BottomButtonsBlock(
                isNoData = component.uris.isNullOrEmpty(),
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    saveBatch(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBatch
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImage)
        },
        canShowScreenData = !component.uris.isNullOrEmpty()
    )

    BatchOverlays(
        component = component,
        showExitDialog = showExitDialog,
        onDismissExitDialog = { showExitDialog = false }
    )
}

@Composable
private fun BatchOverlays(
    component: BatchComponent,
    showExitDialog: Boolean,
    onDismissExitDialog: () -> Unit
) {
    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.items.count { it.status != BatchItemStatus.Done },
        onCancelLoading = component::cancelSaving
    )

    component.failedItem?.let { failed ->
        BatchFailureDialog(
            item = failed,
            onDecision = component::resolveFailure
        )
    }

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = onDismissExitDialog,
        visible = showExitDialog
    )
}

@Composable
private fun BatchItemsList(
    items: List<BatchItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            count = items.size,
            key = { items[it].uri }
        ) { index ->
            val item = items[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BatchStatusIcon(status = item.status)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.uri.substringAfterLast('/'),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun BatchStatusIcon(
    status: BatchItemStatus
) {
    val (icon, tint, description) = when (status) {
        BatchItemStatus.Pending -> Triple(
            Icons.Outlined.Schedule,
            MaterialTheme.colorScheme.outline,
            R.string.batch_status_pending
        )
        BatchItemStatus.Running -> Triple(
            Icons.Rounded.Refresh,
            MaterialTheme.colorScheme.primary,
            R.string.batch_status_running
        )
        BatchItemStatus.Done -> Triple(
            Icons.Outlined.CheckCircle,
            MaterialTheme.colorScheme.primary,
            R.string.batch_status_done
        )
        BatchItemStatus.Failed -> Triple(
            Icons.Rounded.Error,
            MaterialTheme.colorScheme.error,
            R.string.batch_status_failed
        )
    }
    Icon(
        imageVector = icon,
        contentDescription = stringResource(description),
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}
