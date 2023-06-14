package extrydev.app.yuknaklet.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.nio.file.Files

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(userId: String, genericUserId: String) {
    val name = remember { mutableStateOf(TextFieldValue()) }
    val surName = remember { mutableStateOf(TextFieldValue()) }
    val userPhoto = remember { mutableStateOf("") }
    val s3Client = S3Client.builder()
        .region(Region.EU_WEST_2)
        .build()
    val putRequest = PutObjectRequest.builder()
        .bucket("yuknaklet-bucket")
        .key("publicFolder/userPhotos/$userId/$genericUserId.png")
        .build()
    val context = LocalContext.current
    val outputFile = (context.getExternalFilesDir(null)?.absolutePath ?: "") + "/image.png"
    val file = File(outputFile)
    val bytes: ByteArray = Files.readAllBytes(file.toPath())
    val requestBody = RequestBody.fromBytes(bytes)
    s3Client.putObject(putRequest, requestBody)
    Scaffold(
        topBar = {},
        content = {
            Column(modifier = Modifier
                .padding(it)
                .fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(value = name.value, onValueChange = { value ->
                    name.value = value})
                TextField(value = surName.value, onValueChange = { value ->
                    surName.value = value})
                TextField(value = userPhoto.value, onValueChange = { value ->
                    userPhoto.value = value})
            }
    }, bottomBar = {})
}