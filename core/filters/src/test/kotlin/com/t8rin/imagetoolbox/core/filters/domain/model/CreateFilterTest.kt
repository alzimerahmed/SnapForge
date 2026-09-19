/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 SnapForge contributors
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

package com.t8rin.imagetoolbox.core.filters.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateFilterTest {

    private interface IntFilter : Filter<Int>

    @Test
    fun createFilterReturnsDeclaredType() {
        val filter = createFilter<Int, IntFilter>(5)
        assertTrue(filter is IntFilter)
        assertEquals(5, filter.value)
    }

    @Test
    fun createFilterValueIsPassedThrough() {
        val filter = createFilter<Int, IntFilter>(42)
        assertEquals(42, filter.value)
    }

    @Test
    fun createFilterIsVisibleByDefault() {
        val filter = createFilter<Int, IntFilter>(1)
        assertTrue(filter.isVisible)
    }

    @Test
    fun createFilterHasNoErrorByDefault() {
        val filter = createFilter<Int, IntFilter>(1)
        assertEquals(null, filter.error)
    }
}
