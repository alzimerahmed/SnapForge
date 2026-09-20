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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Error
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItem
import com.t8rin.imagetoolbox.feature.batch.domain.model.BatchItemStatus
import com.t8rin.imagetoolbox.feature.batch.presentation.screenLogic.BatchDecision

@Composable
fun BatchFailureDialog(
    item: BatchItem,
    onDecision: (BatchDecision) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDecision(BatchDecision.Cancel) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Error,
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.batch_error_dialog_title))
        },
        text = {
            Text(
                text = item.error?.let { "${stringResource(R.string.batch_error_dialog_message)}\n\n$it" }
                    ?: stringResource(R.string.batch_error_dialog_message)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onDecision(BatchDecision.Retry) }
            ) {
                Text(text = stringResource(R.string.batch_retry))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDecision(BatchDecision.Skip) }
            ) {
                Text(text = stringResource(R.string.skip))
            }
        }
    )
}
