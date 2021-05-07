Release version: [![](https://jitpack.io/v/aakarshrestha/compose-vertical-slider.svg)](https://jitpack.io/#aakarshrestha/compose-vertical-slider)

# compose-vertical-slider

A simple verticle slider that is created in Jetpack Compose. ComposeVerticalSlider allows users to make selections from the range of values by dragging the slider in vertical axis.

The min value that is allowed to choose is 0 and max value is 100.

# Download
Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add it in your app build.gradle:
```
dependencies {
    implementation "com.github.aakarshrestha:compose-vertical-slider:[version]"
}
```

**Compose Vertical Slider demo** 

![vertical-slider](https://user-images.githubusercontent.com/15058925/117324948-486a0a80-ae5e-11eb-97e2-02ae5c48ca00.gif)


# Usage

```
@Composable
fun ComposeVerticalSlider(
    trackColor: Color = Color.LightGray,
    progressTrackColor: Color = Color.Green,
    onProgressChanged: (Int) -> Unit,
    onStopTrackingTouch: (Int) -> Unit
)
```
@param trackColor that can be set to a desired color.
@param progressTrackColor that can be set to a desired color.
@param onProgressChanged lambda that is invoked when the slider value changes when [MotionEvent.ACTION_MOVE] is triggered.
@param onStopTrackingTouch lambda that is invoked when the slider value changes when [MotionEvent.ACTION_UP] is triggered.

**Implementation:** Check out the app to see how it works.

```
@Composable
fun VerticalSlider(value: (Int) -> Unit) {
    ComposeVerticalSlider(
        onProgressChanged = {
            value(it)
        },
        onStopTrackingTouch = {
            value(it)
        }
    )
}
```

# License

```
Copyright 2021 Aakar Shrestha

Permission is hereby granted, free of charge, 
to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to permit persons
to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

```
