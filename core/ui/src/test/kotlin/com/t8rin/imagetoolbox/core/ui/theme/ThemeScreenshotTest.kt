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

package com.t8rin.imagetoolbox.core.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [34],
    qualifiers = RobolectricDeviceQualifiers.Pixel7
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ThemeScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun capturesLightAndDarkThemeScreenshots() {
        // Record mode: run `recordRoborazziFossDebug` (CI) to generate goldens,
        // then `verifyRoborazziFossDebug` gates regressions.
        listOf(
            NightMode.Light to "src/test/snapshots/light/theme_light.png",
            NightMode.Dark to "src/test/snapshots/dark/theme_dark.png"
        ).forEach { (nightMode, path) ->
            composeRule.setContent {
                val settingsState = SettingsState.Default.copy(nightMode = nightMode).toUiState()

                CompositionLocalProvider(LocalSettingsState provides settingsState) {
                    ImageToolboxTheme {
                        Surface {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("SnapForge theme sample")
                                Button(onClick = {}) { Text("Primary action") }
                            }
                        }
                    }
                }
            }
            composeRule.onRoot().captureRoboImage(path)
        }
    }
}
