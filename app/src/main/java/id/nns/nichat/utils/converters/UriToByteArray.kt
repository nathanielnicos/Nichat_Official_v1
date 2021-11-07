package id.nns.nichat.utils.converters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

fun uriToByteArray(baseContext: Context, uri: Uri) : ByteArray {
    val contentResolver = baseContext.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val outputStream = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    return outputStream.toByteArray()
}