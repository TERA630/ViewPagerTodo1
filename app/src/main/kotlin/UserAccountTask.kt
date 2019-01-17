package com.example.yoshi.viewpagertodo1

import android.os.AsyncTask
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import com.dropbox.core.v2.users.FullAccount
import java.io.FileInputStream
import java.io.IOException

class UserAccountTask(private val mClientV2: DbxClientV2,
                      private val delegate: TaskDelegate) : AsyncTask<Void, Void, FullAccount?>() {


    interface TaskDelegate {
        fun onAccountReceived(account: FullAccount)
        fun onError(error: Exception?)
    }

    var error: Exception? = null
    override fun doInBackground(vararg params: Void?): FullAccount? {
        try {
            return mClientV2.users().currentAccount
        } catch (e: DbxException) {
            e.printStackTrace()
            error = e
        }
        return null
    }

    override fun onPostExecute(account: FullAccount?) {
        super.onPostExecute(account)

        if (account != null && error == null) {
            delegate.onAccountReceived(account)
        } else {
            delegate.onError(error)
        }
    }
}

class UploadTask (
        private val dbxClient: DbxClientV2,
        private val fileInputStream: FileInputStream,
        private val delegate: TaskDelegate) : AsyncTask<Void, Void, Void?>() {
    var error: Exception? = null

    interface TaskDelegate {
        fun onSuccessUpLoad()
        fun onError(error: Exception?)
    }

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            // Upload to Dropbox
            dbxClient.files().uploadBuilder("/$TODO_TEXT_FILE") //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(fileInputStream)
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

    override fun onPostExecute(result: Void?) {
        if (error == null) {
            delegate.onSuccessUpLoad()
        } else {
            delegate.onError(error)
        }
    }
}

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
