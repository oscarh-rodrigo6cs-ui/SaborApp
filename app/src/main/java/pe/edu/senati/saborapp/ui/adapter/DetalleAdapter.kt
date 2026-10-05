package pe.edu.senati.saborapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.senati.saborapp.databinding.ItemDetalleBinding
import pe.edu.senati.saborapp.model.DetallePedido

class DetalleAdapter(
    private var listaDetalle: List<DetallePedido>,
    private val esEditable: Boolean = true,
    private val onEliminarClick: ((DetallePedido) -> Unit)? = null
) : RecyclerView.Adapter<DetalleAdapter.DetalleViewHolder>() {

    fun actualizarLista(nuevaLista: List<DetallePedido>) {
        this.listaDetalle = nuevaLista
        notifyDataSetChanged()
    }

    inner class DetalleViewHolder(val binding: ItemDetalleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetalleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val item = listaDetalle[position]
        with(holder.binding) {
            tvNombreItem.text = item.nombrePlato
            tvInfoItem.text = String.format("%d x S/ %.2f", item.cantidad, item.precioUnit)
            tvSubtotalItem.text = String.format("S/ %.2f", item.subtotal)

            if (esEditable) {
                btnEliminarItem.visibility = View.VISIBLE
                btnEliminarItem.setOnClickListener {
                    onEliminarClick?.invoke(item)
                }
            } else {
                btnEliminarItem.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = listaDetalle.size
}
