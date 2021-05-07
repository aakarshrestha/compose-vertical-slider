package com.aakarshrestha.composeverticalslider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.aakarshrestha.composeverticalslider.ui.theme.SampleAppComposeVerticalSliderTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            SampleAppComposeVerticalSliderTheme {
                Surface(color = MaterialTheme.colors.background) {

                    var sliderProgressValue by rememberSaveable { mutableStateOf(34) }

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center

                    ) {
                        Column {

                            Text("$sliderProgressValue", textAlign = TextAlign.Center, fontSize = 50.sp)

                            Spacer(modifier = Modifier.padding(10.dp))

                            Card(
                                modifier = Modifier
                                    .wrapContentSize(),
                                shape = RoundedCornerShape(30.dp),
                                elevation = 5.dp
                            ) {
                                VerticalSlider(
                                    progressValue = sliderProgressValue
                                ) {
                                    sliderProgressValue = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VerticalSlider(progressValue: Int? = null, value: (Int) -> Unit) {

        val state = rememberComposeVerticalSliderState()

        ComposeVerticalSlider(
            state = state,
            progressValueSet = progressValue,
            onProgressChanged = {
                value(it)
            },
            onStopTrackingTouch = {
                value(it)
            }
        )
    }
}