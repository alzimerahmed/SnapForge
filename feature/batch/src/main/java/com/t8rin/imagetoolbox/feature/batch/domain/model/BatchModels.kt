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

package com.t8rin.imagetoolbox.feature.batch.domain.model

enum class BatchItemStatus {
    Pending, Running, Done, Failed
}

data class BatchItem(
    val uri: String,
    val status: BatchItemStatus = BatchItemStatus.Pending,
    val error: String? = null
)

object BatchRun {

    fun initialItems(uris: List<String>): List<BatchItem> =
        uris.map { BatchItem(uri = it) }

    fun markRunning(
        items: List<BatchItem>,
        uri: String
    ): List<BatchItem> = items.update(uri) {
        it.copy(status = BatchItemStatus.Running)
    }

    fun markDone(
        items: List<BatchItem>,
        uri: String
    ): List<BatchItem> = items.update(uri) {
        it.copy(status = BatchItemStatus.Done, error = null)
    }

    fun markFailed(
        items: List<BatchItem>,
        uri: String,
        error: String
    ): List<BatchItem> = items.update(uri) {
        it.copy(status = BatchItemStatus.Failed, error = error)
    }

    fun retry(
        items: List<BatchItem>,
        uri: String
    ): List<BatchItem> = items.update(uri) {
        it.copy(status = BatchItemStatus.Pending, error = null)
    }

    fun resetFailed(items: List<BatchItem>): List<BatchItem> = items.map {
        if (it.status == BatchItemStatus.Failed) {
            it.copy(status = BatchItemStatus.Pending, error = null)
        } else {
            it
        }
    }

    fun nextIndex(
        items: List<BatchItem>,
        from: Int
    ): Int = items.indices
        .drop(from + 1)
        .firstOrNull { items[it].status == BatchItemStatus.Pending }
        ?: -1

    fun doneCount(items: List<BatchItem>): Int =
        items.count { it.status == BatchItemStatus.Done }

    fun failedCount(items: List<BatchItem>): Int =
        items.count { it.status == BatchItemStatus.Failed }

    fun totalCount(items: List<BatchItem>): Int = items.size

    private fun List<BatchItem>.update(
        uri: String,
        transform: (BatchItem) -> BatchItem
    ): List<BatchItem> = map {
        if (it.uri == uri) transform(it) else it
    }
}
