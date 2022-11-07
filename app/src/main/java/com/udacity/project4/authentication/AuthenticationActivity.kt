package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */


// (DONE) TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
// (DONE) TODO: If the user was authenticated, send him to RemindersActivity
// (DONE) TODO: a bonus is to customize the sign in flow to look nice using :
// https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout


class AuthenticationActivity : AppCompatActivity() {
    private var _binding: ActivityAuthenticationBinding? = null
    val binding: ActivityAuthenticationBinding
        get() = _binding!!

    private lateinit var authenticationViewModel: AuthenticationViewModel

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->

        authenticationViewModel.onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authenticationViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        authenticationViewModel.currentUser.observe(this) { currentUser ->
            currentUser?.let {
                startActivity(Intent(this, RemindersActivity::class.java))
                finish()
            }
        }

        with(binding) {
            login.setOnClickListener {
                startAuthentication()
            }

            register.setOnClickListener {
                startAuthentication()
            }
        }
    }

    private fun startAuthentication() {

        // choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Customize the authentication layout
        val customLayout = AuthMethodPickerLayout.Builder(R.layout.custom_auth)
            .setEmailButtonId(R.id.login_with_email)
            .setGoogleButtonId(R.id.login_with_google)
            .build()

        // launch signin intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAuthMethodPickerLayout(customLayout)
            .build()

        signInLauncher.launch(signInIntent)
    }
}
