package pe.edu.senati.saborapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.databinding.ItemPlatoBinding
import pe.edu.senati.saborapp.model.Plato

class PlatoAdapter(
    private var listaPlatos: List<Plato>,
    private val onItemClick: (Plato) -> Unit
) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    fun actualizarLista(nuevaLista: List<Plato>) {
        this.listaPlatos = nuevaLista
        notifyDataSetChanged()
    }

    inner class PlatoViewHolder(val binding: ItemPlatoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = listaPlatos[position]
        with(holder.binding) {
            tvNombrePlato.text = plato.nombre
            tvCategoriaPlato.text = plato.categoria
            tvPrecioPlato.text = String.format("S/ %.2f", plato.precio)

            if (plato.disponible) {
                tvDisponibilidadPlato.text = "Disponible"
                tvDisponibilidadPlato.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                tvDisponibilidadPlato.text = "Agotado"
                tvDisponibilidadPlato.setTextColor(Color.parseColor("#D32F2F"))
            }

            root.setOnClickListener { onItemClick(plato) }
        }
    }

    override fun getItemCount(): Int = listaPlatos.size
}
