package pe.edu.senati.saborapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.databinding.ItemPlatoBinding
import pe.edu.senati.saborapp.model.Plato
import java.util.Locale

class PlatoAdapter(
    private var listaPlatos: List<Plato>,
    private val onItemClick: ((Plato) -> Unit)? = null
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
            tvPrecioPlato.text = String.format(Locale.getDefault(), "S/ %.2f", plato.precio)

            if (plato.disponible == 1) {
                tvDisponibilidadPlato.text = "Disponible"
                tvDisponibilidadPlato.setBackgroundColor(Color.parseColor("#E8F5E9"))
                tvDisponibilidadPlato.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                tvDisponibilidadPlato.text = "Agotado"
                tvDisponibilidadPlato.setBackgroundColor(Color.parseColor("#FFEBEE"))
                tvDisponibilidadPlato.setTextColor(Color.parseColor("#C62828"))
            }

            root.setOnClickListener {
                onItemClick?.invoke(plato)
            }
        }
    }

    override fun getItemCount(): Int = listaPlatos.size
}
