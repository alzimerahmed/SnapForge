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

package com.t8rin.imagetoolbox.core.filters.data

import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class SideFadePaintTest {

    private val bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    @Test
    fun paintHasDstInXfermodeForEverySide() {
        FadeSide.entries.forEach { side ->
            val paint = side.getPaint(bitmap, length = 20, strength = 0.5f)
            assertNotNull(paint.xfermode)
        }
    }

    @Test
    fun paintHasLinearGradientShader() {
        val paint = FadeSide.Start.getPaint(bitmap, length = 20, strength = 0.5f)
        assertTrue(paint.shader is LinearGradient)
    }

    @Test
    fun gradientTileModeIsClamp() {
        val paint = FadeSide.Top.getPaint(bitmap, length = 20, strength = 0.5f)
        assertEquals(Shader.TileMode.CLAMP, paint.shader.tileMode)
    }

    @Test
    fun zeroStrengthDoesNotCrash() {
        FadeSide.entries.forEach { side ->
            val paint = side.getPaint(bitmap, length = 20, strength = 0f)
            assertTrue(paint.shader is LinearGradient)
        }
    }
}
