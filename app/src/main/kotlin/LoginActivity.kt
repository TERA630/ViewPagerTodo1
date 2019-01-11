package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File

const val DROPBOX_ACCESSTOKEN = "dropbox_accesstoken"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.yoshi.viewpagertodo1.R.layout.activity_login)
        // tokenが取得ずみであれば、UploadやDownloadを、取得していなければAuthActivityを行う。
        val token = loadStringFromPreference(DROPBOX_ACCESSTOKEN, this@LoginActivity.applicationContext)
        if (token == null) {
            val signInButton = sign_in_button
            signInButton.setOnClickListener {
                Auth.startOAuth2Authentication(applicationContext, getString(com.example.yoshi.viewpagertodo1.R.string.DROPBOX_APP_KEY))
            }
        } else {
            val request = this.intent.getIntExtra("operation", 0)
            itemsExchangeToDropBox(request, token)
        }
    }

    override fun onResume() {
        super.onResume()
        getAccessToken()
    }

    private fun getAccessToken() {
        val accessToken = Auth.getOAuth2Token() //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            val prefs = getSharedPreferences(DROPBOX_ACCESSTOKEN, Context.MODE_PRIVATE)
            prefs.edit().putString("access-token", accessToken).apply()

            val currentUid = Auth.getUid()
            userIdlbl.text = currentUid
            val storedUid = prefs.getString("user-id", null)
            storedUid?.let {
                if (!currentUid.equals(storedUid)) saveStringToPreference("user-id", currentUid, this@LoginActivity.applicationContext)
            }
            val data = Intent()
            data.putExtra("user-id",currentUid)
            data.putExtra("access-token",accessToken)
            //Proceed to upload or download
            val request = this.intent.getIntExtra("operation", 0)
            itemsExchangeToDropBox(request, accessToken)
            setResult(Activity.RESULT_OK,data)
        } else {
            Log.w("test", "Fail to get access_token")
            setResult(Activity.RESULT_CANCELED)
        }
    }

    fun itemsExchangeToDropBox(request: Int, token: String) {
        val client = getDropBoxClient(token)
        if (request == REQUEST_CODE_DROPBOX_UPLOAD) {
            val task = UploadTask(client, File(TODO_TEXT_FILE))

        } else if (request == REQUEST_CODE_DROPBOX_DOWNLOAD) {
            val task = DownloadTask(client, File(TODO_TEXT_FILE))
        } else {
        }


    }
}