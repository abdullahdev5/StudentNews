package com.android.studentnewsadmin.core.domain.common

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun ConvertUriToBitmap(
    uri: Uri?,
    context: Context
): Bitmap? {
    val bitmap = uri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            ImageDecoder.decodeBitmap(source)
        }
    }

    return bitmap
}

//@Composable
//fun ConvertBitmapToUri(bitmap: Bitmap, context: Context): Uri? {
//
//    var uri by remember { mutableStateOf<Uri?>(null) }
//
//    LaunchedEffect(bitmap) {
//        val values = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, "image.png") // Adjust file name as needed
//            put(MediaStore.Images.Media.MIME_TYPE, "image/png") // Adjust MIME type as needed
//            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//        }
//        uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        uri?.let {
//            context.contentResolver.openOutputStream(it)?.use { outputStream ->
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Adjust compression as needed
//            }
//        }
//    }
//
//    return uri
//}