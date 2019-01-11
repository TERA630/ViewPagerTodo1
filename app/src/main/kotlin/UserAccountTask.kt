package com.example.yoshi.viewpagertodo1

import android.os.AsyncTask
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class UploadTask (
        private val dbxClient: DbxClientV2,
        private val file: File) : AsyncTask<Void, Void, Void>() {
    private var error:Exception? = null

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            // Upload to Dropbox
            val inputStream = FileInputStream(file)
            dbxClient.files().uploadBuilder("/" + file.name) //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(inputStream)
            Log.d("Upload Status", "Success")
        } catch (e: DbxException) {
            e.printStackTrace()
            error = e
        } catch (e: IOException) {
            e.printStackTrace()
            error = e
        }
        return null
    }
}

class DownloadTask {
    fun
            File file = new File(dstFilePath)
    OutputStream os = new FileOutputStream(file)
    mClient.files().download(metadata.getPathLower()).download(os)


}


fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requester = OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient())
    val requestConfig = DbxRequestConfig.newBuilder("viewPagerTodo")
            .withHttpRequestor(requester)
            .build()
    return DbxClientV2(requestConfig, accessToken)
}
