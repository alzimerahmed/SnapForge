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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterValueWrapperTest {

    @Test
    fun wrapWrapsValue() {
        val wrapper = 5.wrap()
        assertEquals(5, wrapper.wrapped)
    }

    @Test
    fun wrapperEqualityIsByValue() {
        assertEquals("a".wrap(), "a".wrap())
    }

    @Test
    fun wrapperOfDifferentValuesAreNotEqual() {
        assertTrue("a".wrap() != "b".wrap())
    }

    @Test
    fun wrapperCopyChangesWrappedValue() {
        val copy = 1.wrap().copy(wrapped = 2)
        assertEquals(2, copy.wrapped)
    }

    @Test
    fun nullIsNotWrappedValue() {
        val wrapper = "x".wrap()
        assertTrue(wrapper.wrapped != null)
        assertFalse(wrapper.wrapped is Unit)
    }
}
