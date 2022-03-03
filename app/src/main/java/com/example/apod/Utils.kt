package com.example.apod

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.content.ContextCompat.startActivity

object Utils {
    fun share(context: Context, resources: Resources, podData:
    PodServerResponseData?){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT, podData?.title +
                    if(!podData?.copyright.isNullOrEmpty()) " - " +
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
}