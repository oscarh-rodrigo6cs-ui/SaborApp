package pe.edu.senati.saborapp.model

data class Plato(
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val disponible: Int // 1: Disponible, 0: Agotado
)
