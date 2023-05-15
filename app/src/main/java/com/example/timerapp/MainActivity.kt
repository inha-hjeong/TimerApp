package com.example.timerapp

import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterOrTimerApp()

        }
    }
}

@Preview
@Composable
fun CounterScreen() {
    var time = remember { mutableStateOf(0L) }
    var isRunning = remember { mutableStateOf(false) }
    val handler = remember { Handler(Looper.getMainLooper()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = formatTime(time.value),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isRunning.value) {
                Button(onClick = { isRunning.value = true }) {
                    Text("Start")
                }
            } else {
                Button(onClick = { isRunning.value = false }) {
                    Text("Stop")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { time.value = 0 }) {
                    Text("Reset")
                }
                LaunchedEffect(isRunning) {
                    while (isRunning.value) {
                        delay(1000)
                        time.value++
                        handler.post { }
                    }
                }
            }
        }
    }
}
@Composable
fun CounterOrTimerApp() {
    var selectedOption = remember { mutableStateOf(Option.COUNTER) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Counter")
            RadioButton(
                selected = selectedOption.value == Option.COUNTER,
                onClick = { selectedOption.value = Option.COUNTER }
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Timer")
            RadioButton(
                selected = selectedOption.value == Option.TIMER,
                onClick = { selectedOption.value = Option.TIMER }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedOption.value == Option.COUNTER) {
            CounterScreen()
        } else {
            TimerScreen()
        }
    }
}

enum class Option {
    COUNTER,
    TIMER
}

@Composable
fun TimerScreen() {
    var time = remember { mutableStateOf(0L) }
    var isRunning = remember { mutableStateOf(false) }
    var isPaused = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatTimeTimer(time.value),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Row {
            Button(
                onClick = {
                    if (!isRunning.value) {
                        time.value = 5 * 60 * 1000 // 5 minutes in milliseconds
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "5m")
            }

            Button(
                onClick = {
                    if (!isRunning.value) {
                        time.value = 10 * 60 * 1000 // 10 minutes in milliseconds
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "10m")
            }
        }

        Row {
            Button(
                onClick = {
                    if (!isRunning.value) {
                        isPaused.value = false
                        isRunning.value = true
                        object : CountDownTimer(time.value, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                time.value = millisUntilFinished
                            }

                            override fun onFinish() {
                                time.value = 0
                                isRunning.value = false
                            }
                        }.start()
                    } else if (isPaused.value) {
                        isPaused.value = false
                        object : CountDownTimer(time.value, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                time.value = millisUntilFinished
                            }

                            override fun onFinish() {
                                time.value = 0
                                isRunning.value = false
                            }
                        }.start()
                    } else {
                        isPaused.value = true
                        cancel()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = if (!isRunning.value) "Start" else if (isPaused.value) "Resume" else "Pause")
            }

            Button(
                onClick = {
                    cancel()
                    time.value = 0
                    isRunning .value= false
                    isPaused.value   = false
                },
                enabled = isRunning.value || isPaused.value,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Reset")
            }
        }
    }
}

fun formatTimeTimer(time: Long): String {
    val minutes = (time / 1000) / 60
    val seconds = (time / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}
private fun formatTime(time: Long): String {
    val hours = time / 3600
    val minutes = (time % 3600) / 60
    val seconds = time % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}