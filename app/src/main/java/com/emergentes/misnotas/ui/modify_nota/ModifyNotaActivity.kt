package com.emergentes.misnotas.ui.modify_nota

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.emergentes.misnotas.databinding.ActivityModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ModifyNotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyBinding
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar datos
        val extras = intent.extras
        if (extras != null) {
            id = extras.getString("id").toString()
            val titulo = extras.getString("titulo")
            val descripcion = extras.getString("descripcion")

            if (id.isNotEmpty() && titulo!!.isNotEmpty() && descripcion!!.isNotEmpty()) {
                binding.etTitulo.setText(titulo)
                binding.etTexto.setText(descripcion)
            }
        }

        binding.btnSaveNota.setOnClickListener { saveNota() }
        binding.btnBack.setOnClickListener { finish() }

    }

    private fun saveNota() {

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

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
            Toast.makeText(this, "Nota modificada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Llena los campos", Toast.LENGTH_SHORT).show()
        }

    }

}