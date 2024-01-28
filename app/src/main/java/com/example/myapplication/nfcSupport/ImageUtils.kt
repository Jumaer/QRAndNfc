package com.example.myapplication.nfcSupport

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    /**
     * this function is using for convert image bitmap to uri
     * @param[context] context of view
     * @param[bitmap] image bitmap
     * @return[Uri]
     */
    fun getUri(context: Context, bitmap: Bitmap?): Uri? {
        if (bitmap == null) return null
        var uri: Uri? = null
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + System.currentTimeMillis() + ".png")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            uri = FileProvider.getUriForFile(context, "com.example.myapplication" + ".provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }


    /**
     * this function is using convert image uri to bitmap
     * @param[context] context of view
     * @param[uri] Uri that contains image
     * @return[Bitmap] image
     */
    fun getBitmap(
        context: Context,
        uri: Uri
    ): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)).copy(Bitmap.Config.RGBA_F16, true)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri).copy(Bitmap.Config.RGBA_F16, true)
        }
    }
}