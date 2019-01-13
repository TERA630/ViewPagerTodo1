package com.example.yoshi.viewpagertodo1

import android.os.AsyncTask
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class UploadTask (
        private val dbxClient: DbxClientV2,
        private val file: File) : AsyncTask<Void, Void, Boolean>() {
    var error: Exception? = null

    override fun doInBackground(vararg params: Void?): Boolean {
        try {
            // Upload to Dropbox
            val inputStream = FileInputStream(file)
            dbxClient.files().uploadBuilder("/" + file.name) //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(inputStream)
            Log.d("Upload Status", "Success")
            return true
        } catch (e: DbxException) {
            e.printStackTrace()
            error = e
        } catch (e: IOException) {
            e.printStackTrace()
            error = e
        }
        return false
    }
}

class DownloadTask(
        private val mClientV2: DbxClientV2) : AsyncTask<Void, Void, Boolean>() {
    var error: Exception? = null

    override fun doInBackground(vararg params: Void?): Boolean {
        val result = mClientV2.files().search("", TODO_TEXT_FILE)
        if (result == null) return false
        else {
            mClientV2.files().download(TODO_TEXT_FILE)
            return true
        }
    }
}


fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requestConfig = DbxRequestConfig.newBuilder("Name/Version")
            .build()
    val client = DbxClientV2(requestConfig, accessToken)
    return client
}
