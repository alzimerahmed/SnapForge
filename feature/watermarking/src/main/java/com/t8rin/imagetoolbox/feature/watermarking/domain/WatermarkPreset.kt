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

package com.t8rin.imagetoolbox.feature.watermarking.domain

import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode

data class WatermarkPreset(
    val name: String,
    val positionX: Float,
    val positionY: Float,
    val rotation: Int,
    val alpha: Float,
    val isRepeated: Boolean,
    val overlayModeValue: Int,
    val text: String,
    val textSize: Float,
    val colorArgb: Long,
    val backgroundColorArgb: Long
)

fun WatermarkParams.toWatermarkPreset(
    name: String
): WatermarkPreset? {
    val type = watermarkingType as? WatermarkingType.Text ?: return null
    return WatermarkPreset(
        name = name,
        positionX = positionX,
        positionY = positionY,
        rotation = rotation,
        alpha = alpha,
        isRepeated = isRepeated,
        overlayModeValue = overlayMode.value,
        text = type.text,
        textSize = type.params.size,
        colorArgb = type.params.color.toLong(),
        backgroundColorArgb = type.params.backgroundColor.toLong()
    )
}

fun WatermarkPreset.toWatermarkParams(): WatermarkParams = WatermarkParams(
    positionX = positionX,
    positionY = positionY,
    rotation = rotation,
    alpha = alpha,
    isRepeated = isRepeated,
    overlayMode = BlendingMode.newEntries.getOrNull(overlayModeValue)
        ?: BlendingMode.SrcOver,
    watermarkingType = WatermarkingType.Text(
        params = TextParams(
            color = colorArgb.toInt(),
            size = textSize,
            font = null,
            backgroundColor = backgroundColorArgb.toInt()
        ),
        text = text,
        digitalParams = DigitalParams.Default
    )
)
