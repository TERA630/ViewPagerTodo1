package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2
import kotlinx.android.synthetic.main.activity_login.*
import java.io.FileInputStream

const val DROPBOX_TOKEN = "dropbox_access_token"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // tokenが取得ずみであれば、UploadやDownloadを、取得していなければAuthActivityを行う。
        val token = loadStringFromPreference(DROPBOX_TOKEN, this@LoginActivity.applicationContext)
        val signInButton = sign_in_button
        if (token == null) {
            signInButton.setOnClickListener {
                Auth.startOAuth2Authentication(applicationContext, getString(com.example.yoshi.viewpagertodo1.R.string.DROPBOX_APP_KEY))
            }
        } else {
            val clientV2 = getDropBoxClient(token)
            val userName = canClientLinkedName(clientV2)
            userName?.let {
                val sb = StringBuilder(resources.getText(R.string.inLoginStatus))
                sb.insert(0, it)
                val action = this.intent.getIntExtra("action", 0)
                itemsExchangeToDropBox(action, clientV2)
            } ?: throw IllegalStateException()
        }
    }

    override fun onResume() {
        super.onResume()
        getAccessToken()
    }

    private fun getAccessToken() {
        val accessToken = Auth.getOAuth2Token() //generate Access Token
        if (accessToken != null) {
            saveStringToPreference(DROPBOX_TOKEN, accessToken, this@LoginActivity.applicationContext)

            val currentUid = Auth.getUid()
            user_id_label.text = currentUid
            val storedUid = loadStringFromPreference("user-id", this@LoginActivity.applicationContext)
            storedUid?.let {
                if (!currentUid.equals(storedUid)) saveStringToPreference("user-id", currentUid, this@LoginActivity.applicationContext)
            }
            val clientV2 = getDropBoxClient(accessToken)
            //Proceed to upload or download
            canClientLinkedName(clientV2)?.let {
                val action = this.intent.getIntExtra("action", 0)
                itemsExchangeToDropBox(action, clientV2)
                setResult(Activity.RESULT_OK)
            } ?: run {
            Log.w("test", "Fail to get access_token")
            setResult(Activity.RESULT_CANCELED)
            }
        }
    }

    private fun itemsExchangeToDropBox(request: Int, client: DbxClientV2): Boolean {
        if (request == REQUEST_CODE_DROPBOX_UPLOAD) {
            try {
                val fis = FileInputStream(TODO_TEXT_FILE)
                val sb = StringBuilder("/").append(TODO_TEXT_FILE)
                val metadata = client.files().uploadBuilder(sb.toString())
                        .uploadAndFinish(fis)
            } catch (e: Exception) {
                Log.w("test", "fail to Upload.")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else if (request == REQUEST_CODE_DROPBOX_DOWNLOAD) {
            val task = DownloadTask(client)
            task.execute()
            return (task.error != null)
        } else return false
        return false
    }

    private fun canClientLinkedName(client: DbxClientV2): String? {
        return client.users().currentAccount.name.displayName
    }
}