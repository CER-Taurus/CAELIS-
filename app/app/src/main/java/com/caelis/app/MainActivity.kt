package com.caelis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Background = Color(0xFF08090B)
private val Panel = Color(0xFF121418)
private val Silver = Color(0xFFD7DCE2)
private val Muted = Color(0xFF777F89)

class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
MaterialTheme(
colorScheme = darkColorScheme(
background = Background,
surface = Panel,
primary = Silver,
onPrimary = Background,
onBackground = Silver,
onSurface = Silver
)
) {
CaelisScreen()
}
}
}
}

@Composable
private fun CaelisScreen() {
var input by remember { mutableStateOf("") }
val messages = remember {
mutableStateListOf(
"CAELIS 0.1 запущен. Ожидаю команду."
)
}
val engine = remember { CaelisEngine() }
val keyboard = LocalSoftwareKeyboardController.current
val focusManager = LocalFocusManager.current

fun sendCommand() {
    val command = input.trim()
    if (command.isNotEmpty()) {
        messages.add("YOU  /  $command")
        messages.add("CAELIS  /  ${engine.process(command)}")
        input = ""
        keyboard?.hide()
        focusManager.clearFocus()
    }
}

Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Background)
        .padding(horizontal = 20.dp, vertical = 18.dp)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "CAELIS",
                color = Silver,
                fontSize = 25.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 5.sp
            )
            Text(
                "PERSONAL INTELLIGENCE",
                color = Muted,
                fontSize = 9.sp,
                letterSpacing = 2.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("SYSTEM  0.1", color = Silver, fontSize = 10.sp)
            Text("LOCAL MODE", color = Muted, fontSize = 9.sp)
        }
    }

    Spacer(Modifier.height(20.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.85f),
        contentAlignment = Alignment.Center
    ) {
        CoreAnimation()
    }

    Surface(
        color = Panel,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "SYSTEM STATUS",
                    color = Muted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                Text("● ONLINE", color = Silver, fontSize = 10.sp)
            }
            Spacer(Modifier.height(12.dp))
            StatusRow("LOCAL ENGINE", "ACTIVE")
            StatusRow("CLOUD INTELLIGENCE", "NOT CONNECTED")
            StatusRow("VOICE INTERFACE", "PLANNED")
        }
    }

    Spacer(Modifier.height(14.dp))

    Text(
        "COMMUNICATION LOG",
        color = Muted,
        fontSize = 10.sp,
        letterSpacing = 2.sp
    )
    Spacer(Modifier.height(7.dp))

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.8f)
    ) {
        items(messages) { message ->
            Text(
                text = message,
                color = if (message.startsWith("YOU")) Muted else Silver,
                fontSize = 12.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(12.dp))
            .padding(horizontal = 13.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Silver,
                fontSize = 14.sp
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = { sendCommand() }
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (input.isEmpty()) {
                        Text(
                            "Введите команду...",
                            color = Muted,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(Modifier.width(10.dp))
        androidx.compose.material3.TextButton(
            onClick = { sendCommand() }
        ) {
            Text("SEND", color = Silver, fontSize = 12.sp)
        }
    }

    Spacer(Modifier.height(6.dp))
    Text(
        "CAELIS CORE  /  STANDBY",
        color = Muted,
        fontSize = 9.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
}

}

@Composable
private fun StatusRow(label: String, value: String) {
Row(
modifier = Modifier
.fillMaxWidth()
.padding(vertical = 4.dp),
horizontalArrangement = Arrangement.SpaceBetween
) {
Text(label, color = Muted, fontSize = 10.sp)
Text(value, color = Silver, fontSize = 10.sp)
}
}

@Composable
private fun CoreAnimation() {
val transition = rememberInfiniteTransition(label = "core")
val rotation by transition.animateFloat(
initialValue = 0f,
targetValue = 360f,
animationSpec = infiniteRepeatable(
animation = tween(18000, easing = FastOutSlowInEasing),
repeatMode = RepeatMode.Restart
),
label = "rotation"
)
val pulse by transition.animateFloat(
initialValue = 0.88f,
targetValue = 1.05f,
animationSpec = infiniteRepeatable(
animation = tween(2200),
repeatMode = RepeatMode.Reverse
),
label = "pulse"
)

Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Canvas(modifier = Modifier.size(190.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.minDimension * 0.34f * pulse

        drawCircle(
            color = Silver.copy(alpha = 0.12f),
            radius = radius * 1.55f,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = Silver.copy(alpha = 0.5f),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            style = Stroke(width = 1.3.dp.toPx())
        )

        for (i in 0 until 8) {
            val angle = Math.toRadians((i * 45 + rotation).toDouble())
            val inner = radius * 0.68f
            val outer = radius * 1.18f
            val x1 = cx + kotlin.math.cos(angle).toFloat() * inner
            val y1 = cy + kotlin.math.sin(angle).toFloat() * inner
            val x2 = cx + kotlin.math.cos(angle).toFloat() * outer
            val y2 = cy + kotlin.math.sin(angle).toFloat() * outer

            drawLine(
                color = Silver.copy(alpha = 0.85f),
                start = androidx.compose.ui.geometry.Offset(x1, y1),
                end = androidx.compose.ui.geometry.Offset(x2, y2),
                strokeWidth = 1.4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        val points = listOf(
            androidx.compose.ui.geometry.Offset(cx, cy - radius * 0.72f),
            androidx.compose.ui.geometry.Offset(cx + radius * 0.62f, cy),
            androidx.compose.ui.geometry.Offset(cx, cy + radius * 0.72f),
            androidx.compose.ui.geometry.Offset(cx - radius * 0.62f, cy)
        )

        for (i in points.indices) {
            drawLine(
                color = Silver,
                start = points[i],
                end = points[(i + 1) % points.size],
                strokeWidth = 1.8.dp.toPx()
            )
        }

        drawCircle(
            color = Silver,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(cx, cy)
        )
    }

    Spacer(Modifier.height(12.dp))
    Text(
        "CORE  /  ACTIVE",
        color = Silver,
        fontSize = 11.sp,
        letterSpacing = 3.sp
    )
    Spacer(Modifier.height(5.dp))
    Text(
        "INTELLIGENCE SYSTEM",
        color = Muted,
        fontSize = 9.sp,
        letterSpacing = 2.sp
    )
}

}
