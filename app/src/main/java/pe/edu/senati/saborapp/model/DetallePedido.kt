package pe.edu.senati.saborapp.model

data class DetallePedido(
    val id: Int = 0,
    val idPedido: Int,
    val idPlato: Int,
    val cantidad: Int,
    val precioUnit: Double,
    val subtotal: Double = cantidad * precioUnit,
    val nombrePlato: String = ""
)
