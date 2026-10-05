package pe.edu.senati.saborapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.databinding.ItemMesaBinding
import pe.edu.senati.saborapp.model.Mesa

class MesaAdapter(
    private var listaMesas: List<Mesa>,
    private val onItemClick: ((Mesa) -> Unit)? = null
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
                cardMesa.setCardBackgroundColor(Color.parseColor("#F1F8E9"))
                cardMesa.strokeColor = Color.parseColor("#C5E1A5")
                tvEstadoMesa.setTextColor(Color.parseColor("#2E7D32"))
                tvEstadoMesa.setBackgroundColor(Color.parseColor("#DCEDC8"))
            } else {
                cardMesa.setCardBackgroundColor(Color.parseColor("#FBE9E7"))
                cardMesa.strokeColor = Color.parseColor("#FFAB91")
                tvEstadoMesa.setTextColor(Color.parseColor("#D84315"))
                tvEstadoMesa.setBackgroundColor(Color.parseColor("#FFCCBC"))
            }

            root.setOnClickListener {
                onItemClick?.invoke(mesa)
            }
        }
    }

    override fun getItemCount(): Int = listaMesas.size
}
