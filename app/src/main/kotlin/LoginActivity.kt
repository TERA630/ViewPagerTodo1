import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.example.yoshi.viewpagertodo1.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


const val DROPBOX_ACCESSTOKEN = "dropbox_accesstoken"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.yoshi.viewpagertodo1.R.layout.activity_login)

        val signInButton = sign_in_button
        signInButton.setOnClickListener {
            Auth.startOAuth2Authentication(applicationContext, getString(com.example.yoshi.viewpagertodo1.R.string.DROPBOX_APP_KEY))
        }
    }

    override fun onResume() {
        super.onResume()
        getAccessToken()
    }

    fun getAccessToken() {
        val accessToken = Auth.getOAuth2Token() //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            val prefs = getSharedPreferences(DROPBOX_ACCESSTOKEN, Context.MODE_PRIVATE)
            prefs.edit().putString("access-token", accessToken).apply()

            val currentUid = Auth.getUid()
            val storedUid = prefs.getString("user-id", null)
            storedUid?.let {
                if (!currentUid.equals(storedUid)) prefs.edit().putString("user-id", currentUid).apply()
            }

            //Proceed to MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}



