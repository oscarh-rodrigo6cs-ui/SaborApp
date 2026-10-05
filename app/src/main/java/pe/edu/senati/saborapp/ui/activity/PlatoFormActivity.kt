package pe.edu.senati.saborapp.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        if (platoId > 0) {
            supportActionBar?.title = getString(R.string.actualizar)
            binding.tvTitulo.text = getString(R.string.actualizar)
            binding.btnGuardar.visibility = View.GONE
            binding.btnActualizar.visibility = View.VISIBLE
            binding.btnEliminar.visibility = View.VISIBLE

            cargarDatosPlato()
        } else {
            supportActionBar?.title = getString(R.string.nuevo_plato)
            binding.tvTitulo.text = getString(R.string.nuevo_plato)
            binding.btnGuardar.visibility = View.VISIBLE
            binding.btnActualizar.visibility = View.GONE
            binding.btnEliminar.visibility = View.GONE
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnGuardar.setOnClickListener { guardarPlato() }
        binding.btnActualizar.setOnClickListener { actualizarPlato() }
        binding.btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private fun cargarDatosPlato() {
        val plato = platoDao.obtenerPorId(platoId) ?: return
        binding.etNombrePlato.setText(plato.nombre)
        binding.etPrecioPlato.setText(plato.precio.toString())
        binding.swDisponible.isChecked = (plato.disponible == 1)

        val idx = categorias.indexOf(plato.categoria)
        if (idx >= 0) {
            binding.spCategoria.setSelection(idx)
        }
    }

    private fun guardarPlato() {
        val nombre = binding.etNombrePlato.text.toString().trim()
        val precioStr = binding.etPrecioPlato.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem?.toString() ?: "Fondos"
        val disponible = if (binding.swDisponible.isChecked) 1 else 0

        binding.tilNombrePlato.error = null
        binding.tilPrecioPlato.error = null

        var hayError = false

        if (nombre.isEmpty()) {
            binding.tilNombrePlato.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (precioStr.isEmpty()) {
            binding.tilPrecioPlato.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (hayError) return

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            binding.tilPrecioPlato.error = getString(R.string.error_precio_invalido)
            return
        }

        val nuevoPlato = Plato(
            nombre = nombre,
            categoria = categoria,
            precio = precio,
            disponible = disponible
        )

        val res = platoDao.insertar(nuevoPlato)
        if (res > 0) {
            Toast.makeText(this, getString(R.string.plato_guardado), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar el plato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarPlato() {
        val nombre = binding.etNombrePlato.text.toString().trim()
        val precioStr = binding.etPrecioPlato.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem?.toString() ?: "Fondos"
        val disponible = if (binding.swDisponible.isChecked) 1 else 0

        binding.tilNombrePlato.error = null
        binding.tilPrecioPlato.error = null

        var hayError = false

        if (nombre.isEmpty()) {
            binding.tilNombrePlato.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (precioStr.isEmpty()) {
            binding.tilPrecioPlato.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (hayError) return

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            binding.tilPrecioPlato.error = getString(R.string.error_precio_invalido)
            return
        }

        val plato = Plato(
            id = platoId,
            nombre = nombre,
            categoria = categoria,
            precio = precio,
            disponible = disponible
        )

        val res = platoDao.actualizar(plato)
        if (res > 0) {
            Toast.makeText(this, getString(R.string.plato_actualizado), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el plato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminar() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar))
            .setMessage(getString(R.string.confirmar_eliminar))
            .setPositiveButton("Sí") { _, _ -> eliminarPlato() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarPlato() {
        try {
            val rows = platoDao.eliminar(platoId)
            if (rows > 0) {
                Toast.makeText(this, getString(R.string.plato_eliminado), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar el plato", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_eliminar_pedido), Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
