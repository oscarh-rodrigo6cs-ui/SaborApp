package pe.edu.senati.saborapp.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.PlatoDao
import pe.edu.senati.saborapp.databinding.ActivityPlatoFormBinding
import pe.edu.senati.saborapp.model.Plato

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private lateinit var platoDao: PlatoDao
    private var platoId: Int = -1

    private val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spCategoria.adapter = spinnerAdapter

        platoId = intent.getIntExtra("PLATO_ID", -1)

        if (platoId != -1) {
            // Modo Edición
            binding.tvTitulo.text = getString(R.string.actualizar)
            binding.btnGuardar.visibility = View.GONE
            binding.btnActualizar.visibility = View.VISIBLE
            binding.btnEliminar.visibility = View.VISIBLE

            cargarDatosPlato()
        } else {
            // Modo Creación
            binding.tvTitulo.text = getString(R.string.nuevo_plato)
            binding.btnGuardar.visibility = View.VISIBLE
            binding.btnActualizar.visibility = View.GONE
            binding.btnEliminar.visibility = View.GONE
        }

        binding.btnGuardar.setOnClickListener { guardarPlato() }
        binding.btnActualizar.setOnClickListener { actualizarPlato() }
        binding.btnEliminar.setOnClickListener { eliminarPlato() }
    }

    private fun cargarDatosPlato() {
        val plato = platoDao.obtenerPorId(platoId) ?: return
        binding.etNombrePlato.setText(plato.nombre)
        binding.etPrecioPlato.setText(plato.precio.toString())
        binding.swDisponible.isChecked = plato.disponible

        val index = categorias.indexOf(plato.categoria)
        if (index >= 0) {
            binding.spCategoria.setSelection(index)
        }
    }

    private fun guardarPlato() {
        val nombre = binding.etNombrePlato.text.toString().trim()
        val precioStr = binding.etPrecioPlato.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem?.toString() ?: "Otros"
        val disponible = binding.swDisponible.isChecked

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoPlato = Plato(nombre = nombre, categoria = categoria, precio = precio, disponible = disponible)
        val res = platoDao.insertar(nuevoPlato)

        if (res > 0) {
            Toast.makeText(this, getString(R.string.plato_guardado), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar plato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarPlato() {
        val nombre = binding.etNombrePlato.text.toString().trim()
        val precioStr = binding.etPrecioPlato.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem?.toString() ?: "Otros"
        val disponible = binding.swDisponible.isChecked

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val plato = Plato(id = platoId, nombre = nombre, categoria = categoria, precio = precio, disponible = disponible)
        val res = platoDao.actualizar(plato)

        if (res > 0) {
            Toast.makeText(this, getString(R.string.plato_actualizado), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar plato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarPlato() {
        try {
            val ok = platoDao.eliminar(platoId)
            if (ok) {
                Toast.makeText(this, getString(R.string.plato_eliminado), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar plato", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_eliminar_plato), Toast.LENGTH_LONG).show()
        }
    }
}
