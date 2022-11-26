package com.catalin.photoedit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catalin.photoedit.ui.theme.PhotoEditTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoEditTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SelectImage()
                }
            }
        }
    }
}

@Composable
fun SelectImage() {
    val ctx = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null)}
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            imageUri.value = uri
            launchHandlingActivity(ctx, uri)
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {success ->
            if (success && imageUri.value != null)
                launchHandlingActivity(ctx, imageUri.value!!)

        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Import an image", fontSize = 25.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .width(120.dp)
                    .height(120.dp)
                    .clip(CircleShape),
                onClick = { imagePicker.launch("image/*") }
            ) {
                Text(text = "Picker")
            }
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .width(120.dp)
                    .height(120.dp)
                    .clip(CircleShape),
                onClick = {
                    val uri = ComposeFileProvider.getImageUri(ctx)
                    imageUri.value = uri
                    cameraLauncher.launch(uri)
                }
            ) {
                Text(text = "Camera")
            }
        }
    }
}

fun launchHandlingActivity(ctx: Context, uri: Uri) {
    val intent = Intent(ctx, EditActivity::class.java)
    intent.putExtra("imageUri", uri.toString())
    ctx.startActivity(intent)
}





