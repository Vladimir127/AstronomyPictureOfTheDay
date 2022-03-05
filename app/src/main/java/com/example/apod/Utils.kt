package com.example.apod

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Environment
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

const val REQUEST_CODE = 42

object Utils {
    fun share(
        context: Context, resources: Resources, podData:
        PodServerResponseData?
    ) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT, podData?.title +
                    if (!podData?.copyright.isNullOrEmpty()) " - " +
                            podData?.copyright else ""
        )
        intent.putExtra(Intent.EXTRA_TEXT, podData?.hdurl)
        startActivity(
            context,
            Intent.createChooser(
                intent,
                resources.getString(R.string.share)
            ),
            null
        )
    }

    fun download(podData: PodServerResponseData?) {
        val imageUrl = URL(podData?.hdurl)
        val title = podData?.title
        val fileName = "$title.jpg"

        Thread {
            val bitmap = BitmapFactory.decodeStream(
                imageUrl.openConnection().getInputStream()
            )

            try {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun setAsWallpaper(context: Context, podData: PodServerResponseData?) {
        val imageUrl = URL(podData?.hdurl)

        Thread {
            val bitmap = BitmapFactory.decodeStream(
                imageUrl.openConnection().getInputStream()
            )

            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                wallpaperManager.setBitmap(bitmap)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }.start()
    }

    fun getFont(context: Context): Typeface{
        val resources = context.resources
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val fontFamily = prefs.getString(resources.getString(R.string.font_key),
            "montserrat")
        val fonts: Array<String> =
            resources.getStringArray(R.array.font_values)
        var index = -1
        for (i in fonts.indices) {
            if (fonts[i] == fontFamily) {
                index = i
                break
            }
        }
        val fontPaths: Array<String> =
            resources.getStringArray(R.array.font_paths)
        if (index > -1) {
            val fontPath = fontPaths[index]
            val font: Typeface = if (index == 0) {
                Typeface.create(fontPath, Typeface.NORMAL)
            } else {
                Typeface.createFromAsset(context.assets, fontPath)
            }

            return font
        }

        return Typeface.create(fontPaths[0], Typeface.NORMAL)
    }
}