package pe.edu.senati.saborapp.ui.activity

import android.os.Bundle
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

    private val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.nuevo_plato)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        platoDao = PlatoDao(this)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spCategoria.adapter = spinnerAdapter

        binding.btnGuardar.setOnClickListener {
            guardarPlato()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
