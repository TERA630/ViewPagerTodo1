package com.example.yoshi.viewpagertodo1

import android.os.AsyncTask
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

class DownloadTask(
        private val mClientV2: DbxClientV2,
        private val delegate: TaskDelegate) : AsyncTask<Void, Void, Void>() {
    var error: Exception? = null

    interface TaskDelegate {
        fun onSuccessUpLoad()
        fun onError(error: Exception?)
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val result = mClientV2.files().search("", TODO_TEXT_FILE)
        if (result == null) return null
        else {
            mClientV2.files().download(TODO_TEXT_FILE)
        }
        return null
    }
    override fun onPostExecute(result: Void?) {
        if (error == null) {
            delegate.onSuccessUpLoad()
        } else {
            delegate.onError(error)
        }
    }

}


fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requestConfig = DbxRequestConfig.newBuilder("Name/Version")
            .build()
    return DbxClientV2(requestConfig, accessToken)
}
