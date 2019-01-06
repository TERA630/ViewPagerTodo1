package com.example.yoshi.viewpagertodo1

import android.os.AsyncTask
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount

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


fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requester = OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient())
    val requestConfig = DbxRequestConfig.newBuilder("viewPagerTodo")
            .withHttpRequestor(requester)
            .build()
    return DbxClientV2(requestConfig, accessToken)
}
