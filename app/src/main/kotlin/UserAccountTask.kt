package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import android.widget.Toast
import com.dropbox.core.v2.files.WriteMode
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class UserAccountTask(
        private val _client: DbxClientV2,
        private val _delegate: TaskDelegate) : AsyncTask<Void, Void, FullAccount>() {

    private var error: Exception? = null

    interface TaskDelegate {
        fun onAccountReceived(account: FullAccount)
        fun onError(error: Exception?)
    }

    override fun doInBackground(vararg params: Void): FullAccount? {
        try {
            //get the users FullAccount
            return _client.users().currentAccount
        } catch (e: DbxException) {
            e.printStackTrace()
            error = e
        }
        return null
    }

    override fun onPostExecute(account: FullAccount?) {
        super.onPostExecute(account)
        if (account != null && error == null) {
            //User Account received successfully
            _delegate.onAccountReceived(account)
        } else {
            // Something went wrong
            _delegate.onError(error)
        }
    }
}

class UploadTask (
        private val dbxClient: DbxClientV2,
        private val file: File,
        private val context: Context) : AsyncTask<Unit,Unit, Unit>() {
    private var error:Exception? = null

    override fun doInBackground() {
        try {
            // Upload to Dropbox
            val inputStream = FileInputStream(file)
            dbxClient.files().uploadBuilder("/" + file.getName()) //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(inputStream)
            Log.d("Upload Status", "Success")
        } catch (e: DbxException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPostExecute() {
        super.onPostExecute()
        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
    }
}

fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requester = OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient())
    val requestConfig = DbxRequestConfig.newBuilder("viewPagerTodo")
            .withHttpRequestor(requester)
            .build()
    return DbxClientV2(requestConfig, accessToken)
}
