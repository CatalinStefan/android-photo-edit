package com.catalin.photoedit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.catalin.photoedit.ui.theme.PhotoEditTheme

enum class TOOL(name: String) {
    BRIGHTNESS("Brightness"),
    SATURATION("Saturation")
}

data class ToolButton(
    val tool: TOOL,
    val onClick: () -> Unit,
    val icon: Int = 0
)

class EditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoEditTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    var uriString: String? = null

                    uriString = intent.extras?.getString("imageUri")
                    if (uriString == null)
                        uriString = intent.extras?.get(Intent.EXTRA_STREAM).toString()
                    val uri = Uri.parse(uriString)

                    val currentTool = remember { mutableStateOf<TOOL?>(null) }
                    val brightness = remember { mutableStateOf(0f) }
                    val saturation = remember { mutableStateOf(1f) }

                    val tools = listOf(
                        ToolButton(
                            TOOL.BRIGHTNESS,
                            { currentTool.value = TOOL.BRIGHTNESS },
                            R.drawable.ic_bright
                        ),
                        ToolButton(
                            TOOL.SATURATION,
                            { currentTool.value = TOOL.SATURATION },
                            R.drawable.ic_saturation
                        )
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        UtilHeader(tools = tools, currentTool = currentTool)
                        EditImage(uri = uri, brightness.value, saturation.value)

                        when (currentTool.value) {
                            TOOL.BRIGHTNESS -> ToolBrightness(brightness)
                            TOOL.SATURATION -> ToolSaturation(saturation)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditImage(uri: Uri, brightness: Float, saturation: Float) {
    val painter = rememberAsyncImagePainter(uri)

    val matrixFilter = ColorMatrix()
    matrixFilter.setToSaturation(saturation)
    matrixFilter[0, 4] = brightness
    matrixFilter[1, 4] = brightness
    matrixFilter[2, 4] = brightness

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.colorMatrix(matrixFilter)
        )
    }
}

@Composable
fun UtilHeader(tools: List<ToolButton>, currentTool: MutableState<TOOL?>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(tools) {
            val icon = painterResource(id = it.icon)
            IconButton(
                onClick = it.onClick,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (currentTool.value == it.tool)
                            MaterialTheme.colors.secondary
                        else
                            Color.White
                    )
                    .padding(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = icon, contentDescription = null)
                    Text(text = it.tool.name, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ToolBrightness(brightness: MutableState<Float>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Brightness")
        Slider(
            valueRange = -255f..255f,
            value = brightness.value,
            onValueChange = { brightness.value = it }
        )
    }
}

@Composable
fun ToolSaturation(saturation: MutableState<Float>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = "Saturation")
        Slider(
            valueRange = 0f..5f,
            value = saturation.value,
            onValueChange = { saturation.value = it }
        )
    }
}







