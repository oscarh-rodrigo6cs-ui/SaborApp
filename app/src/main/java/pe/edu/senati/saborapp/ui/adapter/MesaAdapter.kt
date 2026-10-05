package pe.edu.senati.saborapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.databinding.ItemMesaBinding
import pe.edu.senati.saborapp.model.Mesa

class MesaAdapter(
    private var listaMesas: List<Mesa>,
    private val onItemClick: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    fun actualizarLista(nuevaLista: List<Mesa>) {
        this.listaMesas = nuevaLista
        notifyDataSetChanged()
    }

    inner class MesaViewHolder(val binding: ItemMesaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = listaMesas[position]
        with(holder.binding) {
            tvNumeroMesa.text = "Mesa ${mesa.numero}"
            tvCapacidadMesa.text = "${mesa.capacidad} pers."
            tvEstadoMesa.text = mesa.estado

            if (mesa.estado.uppercase() == "LIBRE") {
                cardMesa.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.mesa_libre)
                )
            } else {
                cardMesa.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.mesa_ocupada)
                )
            }

            root.setOnClickListener { onItemClick(mesa) }
        }
    }

    override fun getItemCount(): Int = listaMesas.size
}
