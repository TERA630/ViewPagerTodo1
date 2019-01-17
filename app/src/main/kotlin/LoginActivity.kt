package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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
            signInButton.isEnabled = true
            signInButton.setOnClickListener { Auth.startOAuth2Authentication(applicationContext, getString(com.example.yoshi.viewpagertodo1.R.string.DROPBOX_APP_KEY)) }
            status_login.text = getString(R.string.status_not_login)
        } else {
            signInButton.isEnabled = false
            val (client, userName) = convertTokenToClientValidated(token)
            userName?.let {
                status_login.text = getString(R.string.status_login, it)
                val action = intent.getIntExtra("action", 0)
                accessToDropBox(action, client)
            }
        } ?: errorFinishActivity("fail to establish client connection at onCreate")
    }

    override fun onResume() {
        super.onResume()
        startAccessDBxWithAuth()
    }

    private fun startAccessDBxWithAuth() {
        val accessToken = Auth.getOAuth2Token() //generate Access Token
        if (accessToken != null) {
            saveStringToPreference(DROPBOX_TOKEN, accessToken, this@LoginActivity.applicationContext)
            val currentUid = Auth.getUid()
            status_login.text = getString(R.string.status_login, currentUid)
            Log.i("test","$currentUid was logged in")

            val storedUid = loadStringFromPreference("user-id", this@LoginActivity.applicationContext)
            storedUid?.let {
                if (currentUid != storedUid) saveStringToPreference("user-id", currentUid, this@LoginActivity.applicationContext)
            }
            val (clientV2, name) = convertTokenToClientValidated(accessToken)
            if (name != null) {
                val action = this.intent.getIntExtra("action", 0)
                accessToDropBox(action, clientV2)
                setResult(Activity.RESULT_OK)
            } else {
                errorFinishActivity("fail to get access_token at startAccesssDropboxafterAuth")
            }
        }
    }

    private fun convertTokenToClientValidated(token: String): Pair<DbxClientV2, String?> {
        val clientV2 = getDropBoxClient(token)
        val nameOfClient = canClientLinkedName(clientV2)?.name?.displayName
        return Pair(clientV2, nameOfClient)
    }

    private fun accessToDropBox(request: Int, client: DbxClientV2): Boolean {
        when (request) {
            REQUEST_CODE_DROPBOX_UPLOAD -> {
                status_login.text = getString(R.string.status_start_upload)
                try {
                    val fis = openFileInput(TODO_TEXT_FILE)
                    val delegate = object : UploadTask.TaskDelegate {
                        override fun onSuccessUpLoad() {
                            Log.i("test", "upload succeeded.")
                            status_login.text = getString(R.string.status_complete_upload, TODO_TEXT_FILE)
                        }
                        override fun onError(error: Exception?) {
                            Log.w("test", "${error?.message} occur at accessToDropBox#UPLOAD")
                            throw IOException()
                        }
                    }
                    val task = UploadTask(client, fis, delegate)
                    task.execute()
                    sign_in_button.text = "元のアクティビティに戻ります"
                    sign_in_button.setOnClickListener {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.w("test", "fail to Upload.")
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            REQUEST_CODE_DROPBOX_DOWNLOAD -> {
                status_login.text = getString(R.string.status_start_download)
                val downDelegate = object : DownloadTask.TaskDelegate {
                    override fun onSuccessUpLoad() {
                        Log.i("test", "upload succeeded.")
                        status_login.text = getString(R.string.status_complete_download)
                    }

                    override fun onError(error: Exception?) {
                        Log.w("test", "${error?.message} occur at accessToDropBox#DOWNLOAD")
                        throw IOException()
                    }
                }
                val task = DownloadTask(client, downDelegate)
                task.execute()
                sign_in_button.text = "元のアクティビティに戻ります"
                sign_in_button.setOnClickListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                return (task.error != null)
            }
            else -> return false
        }
        return false
    }

    private fun canClientLinkedName(client: DbxClientV2): FullAccount? {
        try {
            runBlocking {
                val deferredAccount = GlobalScope.async(Dispatchers.Default) {
                    client.users().currentAccount
                }
                return@runBlocking deferredAccount.await()
            }
        } catch (e: Exception) {
            errorFinishActivity("client could not linked User name")
        }
        return null
    }

    private fun errorFinishActivity(message: String) {
        Log.w("test", message)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
/*
}
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
        }*/
}