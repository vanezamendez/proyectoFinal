package com.emergentes.misnotas.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.emergentes.misnotas.R
import com.emergentes.misnotas.databinding.ActivityProfileBinding
import com.emergentes.misnotas.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUser()
        cerrarSesion()
    }

    private fun cerrarSesion() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_ID))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // cerrar sesion
        binding.btnCerrarSesion.setOnClickListener {

            binding.progressBarPerfil.visibility = View.VISIBLE
            binding.btnCerrarSesion.visibility = View.INVISIBLE
            val user = FirebaseAuth.getInstance().currentUser

            googleSignInClient.revokeAccess()
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        user!!.delete()
                            .addOnCompleteListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent)
                            }
                    } else {
                        binding.progressBarPerfil.visibility = View.GONE
                        binding.btnCerrarSesion.visibility = View.VISIBLE
                    }
                }
        }

    }

    private fun loadUser() {
        val user = FirebaseAuth.getInstance().currentUser

        Glide.with(this).load(user!!.photoUrl).into(binding.perfil)
        binding.nombre.text = user.displayName
        binding.email.text = user.email
    }
}