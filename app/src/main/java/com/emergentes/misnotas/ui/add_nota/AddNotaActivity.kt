package com.emergentes.misnotas.ui.add_nota

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.PendingIntentCompat.send
import com.emergentes.misnotas.R
import com.emergentes.misnotas.databinding.ActivityAddNotaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddNotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveNota.setOnClickListener { saveNota() }
        binding.btnBack.setOnClickListener { finish() }

    }

    private fun saveNota() {

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val id = UUID.randomUUID().toString()
        val titulo = binding.etTitulo.text.toString()
        val descripcion = binding.etTexto.text.toString()

        if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
            val data = mapOf(
                "id" to id,
                "titulo" to titulo,
                "descripcion" to descripcion
            )
            db.collection("usuarios").document(user!!.email!!).collection("notas").document(id)
                .set(data)
            Toast.makeText(this, "Nota agregada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Llena los campos", Toast.LENGTH_SHORT).show()
        }

    }

}
