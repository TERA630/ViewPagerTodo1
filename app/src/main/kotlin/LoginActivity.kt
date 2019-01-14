package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.io.IOException

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
            if (canClientLinkedName(clientV2)) {
                val sb = StringBuilder(resources.getText(R.string.inLoginStatus))
                val action = this.intent.getIntExtra("action", 0)
                accessToDropBox(action, clientV2)
            } else throw IllegalStateException()
        }
    }

    override fun onResume() {
        super.onResume()
        startAccessWithToken()
    }

    private fun startAccessWithToken() {
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
            if (!canClientLinkedName(clientV2)) {
                Log.w("test", "Fail to get access_token")
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                val action = this.intent.getIntExtra("action", 0)
                accessToDropBox(action, clientV2)
                setResult(Activity.RESULT_OK)
            }
        }
    }

    private fun accessToDropBox(request: Int, client: DbxClientV2): Boolean {
        when (request) {
            REQUEST_CODE_DROPBOX_UPLOAD -> {
                statusOfDropBox.text = resources.getText(R.string.startUpload)
                try {
                    val delegate = object : UploadTask.TaskDelegate {
                        override fun onSuccessUpLoad() {
                            Log.i("test", "upload succeeded.")
                        }

                        override fun onError(error: Exception?) {
                            Log.w("test", "${error?.message} at ${error?.cause}")
                            throw IOException()
                        }
                    }
                    val task = UploadTask(client, File(TODO_TEXT_FILE), delegate)
                    task.execute()
                } catch (e: Exception) {
                    Log.w("test", "fail to Upload.")
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            REQUEST_CODE_DROPBOX_DOWNLOAD -> {
                val downDelegate = object : DownloadTask.TaskDelegate {
                    override fun onSuccessUpLoad() {
                        Log.i("test", "upload succeeded.")
                    }

                    override fun onError(error: Exception?) {
                        Log.w("test", "${error?.message} at ${error?.cause}")
                        throw IOException()
                    }
                }
                val task = DownloadTask(client, downDelegate)
                task.execute()
                return (task.error != null)
            }
            else -> return false
        }
        return false
    }

    private fun canClientLinkedName(client: DbxClientV2): Boolean {
        try {
            val delegate = object : UserAccountTask.TaskDelegate {
                override fun onAccountReceived(account: FullAccount) {
                    Log.i("test", "${account.name.displayName} was linked")
                }

                override fun onError(error: Exception?) {
                    Log.w("test", "${error?.message} at ${error?.cause}")
                    throw IllegalStateException()
                }
            }
            val task = UserAccountTask(client, delegate)
            task.execute()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}