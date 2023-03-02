package com.rasaapps.raul.loginapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    var TAG : String = "ETIQUETA"
    private val SHARED_LOGGEDIN_NAME = "Logged In Name"

    lateinit var buttonLogIn : Button
    lateinit var etUserId : EditText
    lateinit var etPasswd : EditText
    lateinit var tvForgot : TextView

    var progressDialog: ProgressDialog? = null
    var mainLayout : View? = null

    lateinit var mGoogleSignInClient : GoogleSignInClient

    var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUserId = findViewById(R.id.et_useId)
        etPasswd = findViewById(R.id.et_password)
        tvForgot = findViewById(R.id.tv_forgot_passwd)
        tvForgot.setOnClickListener {
            if (etUserId.text.isEmpty()){
                createWarningMessage("UserId")
            } else {
                // Pop up message email will be sent
                createWarningMessage("send_email_recovery")
            }
        }

        mainLayout = findViewById(R.id.main_layout)

        buttonLogIn = findViewById(R.id.login_button)
        buttonLogIn.setOnClickListener(View.OnClickListener {
            if (etUserId.text.isEmpty()) {
                createWarningMessage("UserId")
            } else if (etPasswd.text.isEmpty()) {
                createWarningMessage("Password")
            } else {
                saveToSharedPref(etUserId.text.toString())
                jumpToList()
                //jumpToMap()
            }
        })

        val signInGoogleButton: SignInButton = findViewById(R.id.google_sign_in_button)
        signInGoogleButton.setSize(SignInButton.SIZE_WIDE)
        signInGoogleButton.setOnClickListener(View.OnClickListener {
            if (checkGoogleLogIn()) {
                // Proceed to logout
                logOutFromGoogle()
            } else {
                // Sign In to Google
                activateProgDialog()
                signInToGoogle()
            }
        })


        // GoogleSignInOptionsBuilder *** *** ***
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso) // gets info about google client
        checkGoogleLogIn()


        // Result from updated activityResultLauncher *** *** ***
        activityResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->

            if (progressDialog != null) // Close the waiting progress dialog
                progressDialog!!.dismiss()

            if (result.resultCode == RESULT_OK && result.data != null && result.data!!.extras != null) {
                // There is no request code
                var accountNameGmail: String? = null
                try {
                    val task =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account =
                        task.getResult(ApiException::class.java)
                    accountNameGmail = account.email
                } catch (e: ApiException) {
                    Log.w(TAG, "signInResult:failed code= " + e.statusCode)
                }
                if (accountNameGmail != null) {
                    // Save Gmail user email to Shared Pref
                    saveToSharedPref(accountNameGmail)
                    jumpToList()
                }
            }
        }

        // Facebook login button & callBackManager *** *** ***
        loginButton = findViewById(R.id.facebook_sign_in_button)
        callbackManager = CallbackManager.Factory.create()
        loginButton.setPermissions(listOf("email"))
        // loginButton.setPermissions(listOf("email", "user_birthday"))

        loginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult) {
                val userId = result.accessToken.userId

                val bundle = Bundle()
                bundle.putString("fields", "id, email, first_name, last_name, gender,age_range")

                //Graph API to access the data of user's facebook account
                val request = GraphRequest.newMeRequest(
                    result.accessToken
                ) { fbObject, response ->
                    Log.v("Login Success", response.toString())

                    //For safety measure enclose the request with try and catch
                    try {
                        val name = fbObject?.getString("name")
                        if (name != null) {
                            // Save Fb user name to Shared Pref
                            saveToSharedPref(name)
                            jumpToList()
                        }

                    } //If no data has been retrieve throw some error
                    catch (e: JSONException) {
                    }
                }
                //Set the bundle's data as Graph's object data
                //request.setParameters(bundle)

                //Execute this Graph request asynchronously
                request.executeAsync()

            }

            override fun onCancel() {
                Log.d(TAG, "onCancel: called")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "Fb onError: called")
            }
        })

        checkLogInStatusAll()
    }

    // Check any log in status *** *** ***
    private fun checkLogInStatusAll(){
        // Check Facebook login status
        if (checkFacebookLogIn() || checkGoogleLogIn()){
            // Intent to MainActivityList with Shared Pref logged in name
            jumpToList()
        }
    }

    // Intent to List with Logged In Name *** *** ***
    fun jumpToList() {
        // Recover the last logged in name from Pref
        val nameToSend : String? = retrieveFromSharedPref()

        // Jump to List
        val i = Intent(this, MainActivityList::class.java)
        if (nameToSend != null)
            i.putExtra("NAME FROM PREF", nameToSend)

        getResult.launch(i)
    }

    // Intent Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getStringExtra("RESPONSE_FROM_LIST")

                if (value.equals("logout")){
                    // Proceed to logout
                    fbLogOut()
                    if (checkGoogleLogIn())
                          logOutFromGoogle()
                    etUserId.setText("")
                    etPasswd.setText("")
                } else {
                    finish() // Close the app
                }
            }
        }

    // Save to SharedPrefs *** *** ***
    fun saveToSharedPref(name: String){
        val editor = getPreferences(MODE_PRIVATE).edit()
        editor.putString(SHARED_LOGGEDIN_NAME, name)
        editor.apply()
    }

    // Retrieve from SharedPrefs *** *** ***
    private fun retrieveFromSharedPref(): String? {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        return sharedPreferences.getString(SHARED_LOGGEDIN_NAME, null)
    }

    private fun fbLogOut(){
        if (checkFacebookLogIn()) {// if it is logged in to Facebook
        LoginManager.getInstance().logOut()
        showSnack()
        }
    }


    // Check if already logged in to Facebook
    private fun checkFacebookLogIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return (accessToken != null) && !accessToken.isExpired
    }

    // Check if already logged in o Google *** *** ***
    private fun checkGoogleLogIn() : Boolean {
        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        return account != null
    }

    // Log in to Google *** *** ***
    private fun signInToGoogle(){
        val signInIntent : Intent = mGoogleSignInClient.signInIntent
        activityResultLauncher?.launch(signInIntent)

    }

    // Log out from Google *** *** ***
    private fun logOutFromGoogle(){
        // is already logged in
        mGoogleSignInClient.signOut().addOnCompleteListener {
            Log.i(TAG, "account logged out" )
            // SnackBar show Use Logged out
            showSnack()
        }
    }

    // Facebook callback handling *** *** ***
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "Fb callback onActivityResult data: $data")
    }


    // initiate the progress Dialog - used during waiting period *** *** ***
    private fun activateProgDialog() {
        // Progress Dialog setup
        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog!!.show()
        progressDialog!!.setContentView(R.layout.custom_progressdialog)
        //Set progress Dialog background transparent
        progressDialog!!.window!!.setBackgroundDrawableResource(
            android.R.color.transparent
        )
        progressDialog!!.setCancelable(true)
    }

    private fun showSnack(){
        // SnackBar show Use Logged out
        val snack = mainLayout?.let { it1 -> Snackbar.make(it1, "User Logged Out", Snackbar.LENGTH_LONG) }
        if (snack != null) {
            snack.show()
        }
    }





    fun createWarningMessage(field : String) {
        val builder = AlertDialog.Builder(this)
        if (field.equals("send_email_recovery")){
            builder.setTitle("Passwd recovery")
            builder.setMessage("An Email was sent to your inbox")
            builder.setIcon(R.drawable.black_baseline_email_24)
        } else {
            builder.setTitle("Log In")
            builder.setMessage("Please fill in $field")
            builder.setIcon(R.drawable.black_error_outline_24)
        }
        builder.setPositiveButton("OK"){dialog, id -> dialog.dismiss()}
        builder.show()
    }



}