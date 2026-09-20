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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BatchRunTest {

    private val uris = listOf("content://a", "content://b", "content://c")

    @Test
    fun initialItemsAreAllPending() {
        val items = BatchRun.initialItems(uris)

        assertEquals(3, items.size)
        assertTrue(items.all { it.status == BatchItemStatus.Pending })
        assertEquals(uris, items.map { it.uri })
    }

    @Test
    fun markRunningSetsOnlyTargetItem() {
        val items = BatchRun.initialItems(uris)

        val updated = BatchRun.markRunning(items, "content://b")

        assertEquals(BatchItemStatus.Running, updated[1].status)
        assertEquals(BatchItemStatus.Pending, updated[0].status)
        assertEquals(BatchItemStatus.Pending, updated[2].status)
    }

    @Test
    fun markDoneClearsErrorAndSetsDone() {
        val items = BatchRun.markFailed(
            BatchRun.markRunning(BatchRun.initialItems(uris), "content://a"),
            "content://a",
            "boom"
        )

        val updated = BatchRun.markDone(items, "content://a")

        assertEquals(BatchItemStatus.Done, updated[0].status)
        assertEquals(null, updated[0].error)
    }

    @Test
    fun markFailedKeepsErrorMessage() {
        val items = BatchRun.markRunning(BatchRun.initialItems(uris), "content://a")

        val updated = BatchRun.markFailed(items, "content://a", "boom")

        assertEquals(BatchItemStatus.Failed, updated[0].status)
        assertEquals("boom", updated[0].error)
    }

    @Test
    fun retryResetsFailedItemToPending() {
        val items = BatchRun.markFailed(
            BatchRun.markRunning(BatchRun.initialItems(uris), "content://b"),
            "content://b",
            "boom"
        )

        val updated = BatchRun.retry(items, "content://b")

        assertEquals(BatchItemStatus.Pending, updated[1].status)
        assertEquals(null, updated[1].error)
    }

    @Test
    fun nextPendingIndexSkipsTerminalItems() {
        var items = BatchRun.initialItems(uris)
        items = BatchRun.markDone(items, "content://a")
        items = BatchRun.markFailed(items, "content://b", "boom")

        assertEquals(2, BatchRun.nextIndex(items, 0))
        assertEquals(-1, BatchRun.nextIndex(items, 2))
    }

    @Test
    fun doneCountCountsOnlyDone() {
        var items = BatchRun.initialItems(uris)
        items = BatchRun.markDone(items, "content://a")
        items = BatchRun.markFailed(items, "content://b", "boom")

        assertEquals(1, BatchRun.doneCount(items))
        assertEquals(1, BatchRun.failedCount(items))
        assertEquals(3, BatchRun.totalCount(items))
    }

    @Test
    fun resetFailedOnlyAffectsFailedItems() {
        var items = BatchRun.initialItems(uris)
        items = BatchRun.markDone(items, "content://a")
        items = BatchRun.markFailed(items, "content://b", "boom")

        val reset = BatchRun.resetFailed(items)

        assertEquals(BatchItemStatus.Done, reset[0].status)
        assertEquals(BatchItemStatus.Pending, reset[1].status)
        assertEquals(null, reset[1].error)
    }
}
