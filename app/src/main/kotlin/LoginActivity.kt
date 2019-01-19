package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

const val DROPBOX_TOKEN = "dropbox_access_token"

// TODO クラウド上のファイルの確認
//　TODO　ストレージ上のファイルの確認
//　TODO ネットにつながっていないときどする？


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val token = loadStringFromPreference(DROPBOX_TOKEN, this@LoginActivity.applicationContext)
        val signInButton = sign_in_button
        comeBack_MainActivity.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        if (token == null) {
            // tokenが未取得であれば、AuthActivityへ遷移する。
            signInButton.isEnabled = true
            upload_dropbox.isEnabled = false
            download_dropbox.isEnabled = false
            signInButton.setOnClickListener { Auth.startOAuth2Authentication(applicationContext, getString(com.example.yoshi.viewpagertodo1.R.string.DROPBOX_APP_KEY)) }
            status_login.text = getString(R.string.status_not_login)
            status_connection.text = getString(R.string.status_not_login)
        } else { // Tokenが取得されていれば、ユーザー名とリンクされているかを確認する。
            canClientLinkedName(token)
            signInButton.isEnabled = false
        }
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
            Log.i("test", "$currentUid was logged in")
            storeIfIdUpdated(currentUid)
            }
        }
    private fun validateAndBeginDropBox(token: String, nameOfClient: String?, action: Int) {
        val clientV2 = getDropBoxClient(token)
        if (nameOfClient == null) {
            errorFinishActivity("fail to establish DropBox connection")
        } else {
            status_login.text = getString(R.string.status_login, nameOfClient)
            accessToDropBox(action, clientV2)
        }
    }
    private fun accessToDropBox(request: Int, client: DbxClientV2): Boolean {
        when (request) {
            REQUEST_CODE_DROPBOX_UPLOAD -> uploadTextFile(client)
            REQUEST_CODE_DROPBOX_DOWNLOAD -> downLoadTextFile(client)
            else -> return false
        }
        return false
    }

    private fun canClientLinkedName(token: String) {
        try {
            val client = getDropBoxClient(token)
            GlobalScope.launch {
                // 非ブロックコルーチン
                val account = GlobalScope.async(Dispatchers.Default) {
                    client.users().currentAccount
                }.await()

                val displayName = account.name.displayName
                displayName?.let { enableDropBoxConnection(it, token) }
            }
        } catch (e: Exception) {
            errorFinishActivity("client could not linked User name")
        }
    }

    private suspend fun enableDropBoxConnection(displayName: String, token: String) {
        GlobalScope.launch(Dispatchers.Main) {
            status_login.text = getString(R.string.status_login, displayName)
            status_connection.text = getString(R.string.status_canConnect)
            upload_dropbox.isEnabled = true
            upload_dropbox.setOnClickListener { validateAndBeginDropBox(token, displayName, REQUEST_CODE_DROPBOX_UPLOAD) }
            download_dropbox.isEnabled = true
            download_dropbox.setOnClickListener { validateAndBeginDropBox(token, displayName, REQUEST_CODE_DROPBOX_DOWNLOAD) }
        }
    }


    private fun uploadTextFile(_client: DbxClientV2) {
        status_connection.text = getString(R.string.status_start_upload)
        try {
            val fis = openFileInput(TODO_TEXT_FILE)
            val job = GlobalScope.launch {
                _client.files().uploadBuilder("/$TODO_TEXT_FILE")
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(fis)
                fis.close()
            }
            GlobalScope.launch(Dispatchers.Main) {
                job.join()
                status_connection.text = getString(R.string.status_complete_upload, TODO_TEXT_FILE)
            }

        } catch (e: NoSuchFileException) {
            status_connection.text = getString(R.string.status_file_not_found, TODO_TEXT_FILE)
        } catch (e: Exception) {
            status_connection.text = getString(R.string.status_error_on_upload, e.message)
        }
    }

    private fun downLoadTextFile(_client: DbxClientV2) {
        status_connection.text = getString(R.string.status_start_download)
        try {
            val job = GlobalScope.launch {
                _client.files().download("/$TODO_TEXT_FILE")
            }
            GlobalScope.launch(Dispatchers.Main) {
                job.join()
                status_connection.text = getString(R.string.status_complete_download, TODO_TEXT_FILE)
            }

        } catch (e: NoSuchFileException) {
            status_connection.text = getString(R.string.status_file_not_found, TODO_TEXT_FILE)
        } catch (e: Exception) {
            status_connection.text = getString(R.string.status_error_on_upload, e.message)
        }
    }
    private fun errorFinishActivity(message: String) {
        Log.w("test", message)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun storeIfIdUpdated(current: String?) {
        val storedUid = loadStringFromPreference("user-id", this@LoginActivity.applicationContext)
        if (current == null || storedUid == null) return
        else {
            if (current != storedUid) saveStringToPreference("user-id", current, this@LoginActivity.applicationContext)
        }
    }
}
