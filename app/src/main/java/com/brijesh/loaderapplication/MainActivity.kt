package com.brijesh.loaderapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    LoaderScreen()
                }
            }
        }
    }
}

enum class LoaderState {
    Idle, TranslatingUp, Rotating, TranslatingDown
}

@Composable
fun LoaderScreen() {
    var state by remember { mutableStateOf(LoaderState.Idle) }
    val coroutineScope = rememberCoroutineScope()

    if (state == LoaderState.Idle) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        state = LoaderState.TranslatingUp
                        delay(1000)
                        state = LoaderState.Rotating
                        delay(10000)
                        state = LoaderState.TranslatingDown
                        delay(1000)
                        state = LoaderState.Idle
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Start Loader",
                    color = Color(0xFFD81B60), // colorAccent
                    fontSize = 22.sp
                )
            }
        }
    } else {
        LoaderAnimation(state)
    }
}

@Composable
fun LoaderAnimation(state: LoaderState) {
    val transition = updateTransition(targetState = state, label = "LoaderTransition")

    // The dimensions of the ovals from original XML: 100dp x 120dp.
    // Wait, let's just make it simpler by using the Animatable logic or Transition.

    // translate_up_1: from 0,0 to 38%p, 40%p
    // translate_up_2: from 80%p, 0 to 38%p, 40%p
    // translate_up_3: from 38%p, 90%p to 38%p, 40%p
    // Since it goes from these starting positions to the center, we'll map them to screen dimensions.

    // Instead of using complex percentages, let's define 0..1 progress.
    val progressUp by transition.animateFloat(
        transitionSpec = {
            if (targetState == LoaderState.TranslatingUp) {
                tween(durationMillis = 1000)
            } else {
                snap()
            }
        },
        label = "progressUp"
    ) {
        if (it == LoaderState.TranslatingUp) 1f else if (it == LoaderState.Rotating || it == LoaderState.TranslatingDown) 1f else 0f
    }

    val progressDown by transition.animateFloat(
        transitionSpec = {
            if (targetState == LoaderState.TranslatingDown) {
                tween(durationMillis = 1000)
            } else {
                snap()
            }
        },
        label = "progressDown"
    ) {
        if (it == LoaderState.TranslatingDown) 1f else 0f
    }

    // Rotation is infinite during the rotating state.
    val infiniteTransition = rememberInfiniteTransition()
    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation1"
    )
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation2"
    )
    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation3"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth.value
        val h = maxHeight.value

        // Target center
        val targetX = w * 0.38f
        val targetY = h * 0.40f

        // Initial positions for up translation
        val init1X = 0f
        val init1Y = 0f

        val init2X = w * 0.80f
        val init2Y = 0f

        val init3X = w * 0.38f
        val init3Y = h * 0.90f

        // Target positions for down translation
        val down1X = 0f
        val down1Y = 0f

        val down2X = w * 0.80f
        val down2Y = 0f

        val down3X = w * 0.38f
        val down3Y = h * 0.90f

        // Current positions
        val x1 = if (state == LoaderState.TranslatingDown) {
            targetX + (down1X - targetX) * progressDown
        } else {
            init1X + (targetX - init1X) * progressUp
        }
        val y1 = if (state == LoaderState.TranslatingDown) {
            targetY + (down1Y - targetY) * progressDown
        } else {
            init1Y + (targetY - init1Y) * progressUp
        }

        val x2 = if (state == LoaderState.TranslatingDown) {
            targetX + (down2X - targetX) * progressDown
        } else {
            init2X + (targetX - init2X) * progressUp
        }
        val y2 = if (state == LoaderState.TranslatingDown) {
            targetY + (down2Y - targetY) * progressDown
        } else {
            init2Y + (targetY - init2Y) * progressUp
        }

        val x3 = if (state == LoaderState.TranslatingDown) {
            targetX + (down3X - targetX) * progressDown
        } else {
            init3X + (targetX - init3X) * progressUp
        }
        val y3 = if (state == LoaderState.TranslatingDown) {
            targetY + (down3Y - targetY) * progressDown
        } else {
            init3Y + (targetY - init3Y) * progressUp
        }

        val rot1 = if (state == LoaderState.Rotating || state == LoaderState.TranslatingDown) rotation1 else 0f
        val rot2 = if (state == LoaderState.Rotating || state == LoaderState.TranslatingDown) rotation2 else 0f
        val rot3 = if (state == LoaderState.Rotating || state == LoaderState.TranslatingDown) rotation3 else 0f

        // We draw the ovals
        // color 1: oval_blue -> #BC2A8D
        // color 2: oval_green -> #ffdc80
        // color 3: oval_red -> #00BCD4
        // Dimensions: 100dp x 120dp
        Box(
            modifier = Modifier
                .offset(x = x1.dp, y = y1.dp)
                .size(100.dp, 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                withTransform({
                    rotate(rot1, pivot = center)
                }) {
                    drawOval(color = Color(0xFFBC2A8D))
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = x2.dp, y = y2.dp)
                .size(100.dp, 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                withTransform({
                    rotate(rot2, pivot = center)
                }) {
                    drawOval(color = Color(0xFFFFDC80))
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = x3.dp, y = y3.dp)
                .size(100.dp, 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                withTransform({
                    rotate(rot3, pivot = center)
                }) {
                    drawOval(color = Color(0xFF00BCD4))
                }
            }
        }
    }
}
