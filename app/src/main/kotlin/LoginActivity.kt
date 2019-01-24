package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileInputStream

const val DROPBOX_TOKEN = "dropbox_access_token"
const val REQUEST_DROPBOX_UPLOAD = 3
const val REQUEST_DROPBOX_DOWNLOAD = 4
const val REQUEST_MERGE = 5

//  TODO クラウド上のファイルの確認
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
        if (accessToken != null) { // Tokenが得られていればPreferenceに保存する。
            saveStringToPreference(DROPBOX_TOKEN, accessToken, this@LoginActivity.applicationContext)
            val currentUid = Auth.getUid()
            status_login.text = getString(R.string.status_login, currentUid)
            storeIdIfUpdated(currentUid)
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
            REQUEST_DROPBOX_UPLOAD -> {
                if(upload_mode.checkedRadioButtonId == radioOverWrite.id) uploadTextFile(client)
                else mergeAndUploadTextFile(client)
            }
            REQUEST_DROPBOX_DOWNLOAD -> downLoadTextFile(client)
            else -> return false
        }
        return false
    }
    private fun canClientLinkedName(token: String) {
        try {
            val client = getDropBoxClient(token)
            GlobalScope.launch {
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
    private fun enableDropBoxConnection(displayName: String, token: String) {
        GlobalScope.launch(Dispatchers.Main) {
            status_login.text = getString(R.string.status_login, displayName)
            status_connection.text = getString(R.string.status_canConnect)
            upload_dropbox.isEnabled = true
            upload_dropbox.setOnClickListener { validateAndBeginDropBox(token, displayName, REQUEST_DROPBOX_UPLOAD) }
            download_dropbox.isEnabled = true
            download_dropbox.setOnClickListener { validateAndBeginDropBox(token, displayName, REQUEST_DROPBOX_DOWNLOAD) }
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

    private fun mergeAndUploadTextFile(_client: DbxClientV2) {
        status_connection.text = getString(R.string.status_start_merge)
        var fileInputStream: FileInputStream? = null
        try {    //　現状のファイルをhereItemsに読み込んだ後、クラウドよりアイテムをダウンロードして、ThereItemとする。
            val hereItems = loadListFromTextFile(this@LoginActivity.applicationContext)

            val jobDownLoad = GlobalScope.launch(Dispatchers.Default) {
                _client.files().download("/$TODO_TEXT_FILE")
            }
            GlobalScope.launch(Dispatchers.Main) {
                jobDownLoad.join()
                status_connection.text = getString(R.string.status_complete_download, TODO_TEXT_FILE)
            }
            val jobMerge = GlobalScope.launch(Dispatchers.Default) {
                jobDownLoad.join()
                val thereItems = loadListFromTextFile(this@LoginActivity.applicationContext) //クラウド上のアイテム
                val mergedItem = mergeItem(hereItems, thereItems)
                saveListToTextFile(this@LoginActivity.applicationContext, mergedItem)
            }
            GlobalScope.launch(Dispatchers.Main) {
                jobMerge.join()
                status_connection.text = getString(R.string.status_complete_merge, TODO_TEXT_FILE)
            }

            val jobUpload= GlobalScope.launch(Dispatchers.Default){
                jobMerge.join()
                fileInputStream = openFileInput(TODO_TEXT_FILE)
                _client.files().uploadBuilder("/$TODO_TEXT_FILE")
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(fileInputStream)
            }
            GlobalScope.launch(Dispatchers.Main) {
                jobUpload.join()
                status_connection.text = getString(R.string.status_complete_upload, TODO_TEXT_FILE)
            }

        } catch (e: NoSuchFileException) {
            status_connection.text = getString(R.string.status_file_not_found, TODO_TEXT_FILE)
        } catch (e: Exception) {
            status_connection.text = getString(R.string.status_error_on_upload, e.message)
        } finally {
            fileInputStream?.close()
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
    private fun getDropBoxClient(accessToken: String): DbxClientV2 {
        val requestConfig = DbxRequestConfig.newBuilder("Name/Version")
                .build()
        return DbxClientV2(requestConfig, accessToken)
    }

    private fun storeIdIfUpdated(current: String?) {
        val storedUid = loadStringFromPreference("user-id", this@LoginActivity.applicationContext)
        if (current == null || storedUid == null) return
        else {
            if (current != storedUid) saveStringToPreference("user-id", current, this@LoginActivity.applicationContext)
        }
    }
}
