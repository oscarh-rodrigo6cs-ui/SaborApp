package pe.edu.senati.saborapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.data.PlatoTop
import pe.edu.senati.saborapp.databinding.ItemTopPlatoBinding

class TopPlatoAdapter(
    private var listaTop: List<PlatoTop>
) : RecyclerView.Adapter<TopPlatoAdapter.TopPlatoViewHolder>() {

    fun actualizarLista(nuevaLista: List<PlatoTop>) {
        this.listaTop = nuevaLista
        notifyDataSetChanged()
    }

    inner class TopPlatoViewHolder(val binding: ItemTopPlatoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlatoViewHolder {
        val binding = ItemTopPlatoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopPlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopPlatoViewHolder, position: Int) {
        val item = listaTop[position]
        with(holder.binding) {
            tvRanking.text = "#${position + 1}"
            tvNombrePlato.text = item.nombre
            tvCantidadVendida.text = "${item.cantidadTotal} und."
        }
    }

    override fun getItemCount(): Int = listaTop.size
}
