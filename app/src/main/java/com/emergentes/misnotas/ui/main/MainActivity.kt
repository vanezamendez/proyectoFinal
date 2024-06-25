package com.emergentes.misnotas.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.emergentes.misnotas.databinding.ActivityMainBinding
import com.emergentes.misnotas.ui.add_nota.AddNotaActivity
import com.emergentes.misnotas.ui.main.models.Nota
import com.emergentes.misnotas.ui.main.presenter.NotaAdapter
import com.emergentes.misnotas.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NotaAdapter
    private var listNotas: ArrayList<Nota> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarUsuario()
        cargarNotas()

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNotaActivity::class.java))
        }
        binding.ivOpciones.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun cargarNotas() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val rv = binding.rv

        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv.setHasFixedSize(true)
        adapter = NotaAdapter(listNotas)
        rv.adapter = adapter

        // Cargar información
        db.collection("usuarios").document(user!!.email!!).collection("notas")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.i("firestore", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {
                    for (dc in value.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> listNotas.add(dc.document.toObject(Nota::class.java))
                            DocumentChange.Type.MODIFIED -> {
                                val notaModificada = dc.document.toObject(Nota::class.java)
                                val index = listNotas.indexOfFirst { it.id == notaModificada.id }
                                if (index != -1) {
                                    listNotas[index] = notaModificada
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                val notaRemovida = dc.document.toObject(Nota::class.java)
                                listNotas.removeAll { it.id == notaRemovida.id }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                if (listNotas.isEmpty()) {
                    binding.llVacio.visibility = View.VISIBLE
                    rv.visibility = View.INVISIBLE
                } else {
                    binding.llVacio.visibility = View.INVISIBLE
                    rv.visibility = View.VISIBLE
                }

            }

    }

    private fun cargarUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        Glide.with(this).load(user!!.photoUrl).into(binding.ivPerfil)
        binding.tvUserName.text = user.displayName
    }

}