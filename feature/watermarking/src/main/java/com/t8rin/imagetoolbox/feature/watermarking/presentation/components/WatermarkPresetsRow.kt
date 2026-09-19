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

package com.t8rin.imagetoolbox.feature.watermarking.presentation.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkPreset

@Composable
fun WatermarkPresetsRow(
    presets: List<WatermarkPreset>,
    onApplyPreset: (WatermarkPreset) -> Unit,
    onRemovePreset: (WatermarkPreset) -> Unit,
    onSavePreset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(presets) { preset ->
                Box(
                    modifier = Modifier.pointerInput(preset) {
                        detectTapGestures(
                            onLongPress = { onRemovePreset(preset) }
                        )
                    }
                ) {
                    EnhancedChip(
                        selected = false,
                        onClick = { onApplyPreset(preset) },
                        selectedColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        AutoSizeText(preset.name)
                    }
                }
            }
        }
        EnhancedIconButton(
            onClick = { showSaveDialog = true }
        ) {
            Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
        }
    }

    if (showSaveDialog) {
        var value by rememberSaveable { mutableStateOf("") }
        EnhancedAlertDialog(
            visible = true,
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(stringResource(R.string.save_preset))
            },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.default,
                    value = value,
                    onValueChange = { value = it },
                    label = {
                        Text(stringResource(R.string.name))
                    }
                )
            },
            confirmButton = {
                EnhancedButton(
                    onClick = {
                        if (value.isNotBlank()) {
                            onSavePreset(value.trim())
                        }
                        showSaveDialog = false
                    }
                ) {
                    AutoSizeText(stringResource(R.string.save))
                }
            },
            dismissButton = {
                EnhancedButton(
                    onClick = { showSaveDialog = false },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        )
    }
}
