package com.emergentes.misnotas.ui.main.presenter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emergentes.misnotas.R
import com.emergentes.misnotas.ui.main.models.Nota
import com.emergentes.misnotas.ui.modify_nota.ModifyNotaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotaAdapter(private val notasList: ArrayList<Nota>) :
    RecyclerView.Adapter<NotaAdapter.NotasViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NotasViewHolder(layoutInflater.inflate(R.layout.card_nota, parent, false))
    }

    override fun onBindViewHolder(holder: NotasViewHolder, position: Int) {
        val item = notasList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = notasList.size

    inner class NotasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val db = FirebaseFirestore.getInstance()
        private val user = FirebaseAuth.getInstance().currentUser

        var id = itemView.findViewById<TextView>(R.id.tvIdCardNota)
        var titulo = itemView.findViewById<TextView>(R.id.tvTituloCardNota)
        var descripcion = itemView.findViewById<TextView>(R.id.tvDescripcionCardNota)
        var delete = itemView.findViewById<ImageView>(R.id.btnDeleteCardNote)

        fun render(notasModel: Nota) {
            id.text = notasModel.id
            titulo.text = notasModel.titulo
            descripcion.text = notasModel.descripcion

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ModifyNotaActivity::class.java)
                intent.putExtra("id", notasModel.id)
                intent.putExtra("titulo", notasModel.titulo)
                intent.putExtra("descripcion", notasModel.descripcion)
                itemView.context.startActivity(intent)
            }

            // Borrar nota
            delete.setOnClickListener {
                db.collection("usuarios").document(user!!.email!!).collection("notas")
                    .document(notasModel.id!!).delete()
                Toast.makeText(itemView.context, "Nota eliminada", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
