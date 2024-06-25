package com.emergentes.misnotas.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.emergentes.misnotas.R
import com.emergentes.misnotas.databinding.ActivityLoginBinding
import com.emergentes.misnotas.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val CODE = 777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_ID))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding.btnGoogle.setOnClickListener {

            binding.btnGoogle.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
                Log.i("LoginActivity", account.idToken!!)
            } catch (e: ApiException) {
                binding.btnGoogle.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Log.i("LoginActivity", "${e}, activityforResult")
            }
        }
        binding.btnGoogle.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Registramos al usuario
                    val db = FirebaseFirestore.getInstance()
                    val user = FirebaseAuth.getInstance().currentUser

                    val data = mapOf(
                        "username" to user!!.displayName,
                        "correo" to user.email
                    )

                    db.collection("usuarios").document(user.email!!).set(data)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    binding.btnGoogle.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this, "No se pudo iniciar sesión," +
                                " intente nuevamente.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}