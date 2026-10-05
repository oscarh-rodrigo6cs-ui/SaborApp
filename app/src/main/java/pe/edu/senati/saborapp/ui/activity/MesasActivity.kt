package pe.edu.senati.saborapp.ui.activity

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.MesaDao
import pe.edu.senati.saborapp.databinding.ActivityMesasBinding
import pe.edu.senati.saborapp.model.Mesa
import pe.edu.senati.saborapp.ui.adapter.MesaAdapter

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var mesaDao: MesaDao
    private lateinit var adapter: MesaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.gestion_mesas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mesaDao = MesaDao(this)
        adapter = MesaAdapter(emptyList())

        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter

        binding.btnGuardarMesa.setOnClickListener {
            guardarMesa()
        }

        cargarMesas()
    }

    private fun guardarMesa() {
        val numStr = binding.etNumeroMesa.text.toString().trim()
        val capStr = binding.etCapacidadMesa.text.toString().trim()

        binding.tilNumeroMesa.error = null
        binding.tilCapacidadMesa.error = null

        var hayError = false

        if (numStr.isEmpty()) {
            binding.tilNumeroMesa.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (capStr.isEmpty()) {
            binding.tilCapacidadMesa.error = getString(R.string.error_campo_vacio)
            hayError = true
        }

        if (hayError) return

        val numero = numStr.toIntOrNull()
        val capacidad = capStr.toIntOrNull()

        if (numero == null || numero <= 0) {
            binding.tilNumeroMesa.error = "Número inválido"
            return
        }

        if (capacidad == null || capacidad < 1 || capacidad > 12) {
            binding.tilCapacidadMesa.error = getString(R.string.error_capacidad_invalida)
            return
        }

        try {
            val nuevaMesa = Mesa(numero = numero, capacidad = capacidad, estado = "LIBRE")
            val res = mesaDao.insertar(nuevaMesa)
            if (res > 0) {
                Toast.makeText(this, getString(R.string.mesa_guardada), Toast.LENGTH_SHORT).show()
                binding.etNumeroMesa.text?.clear()
                binding.etCapacidadMesa.text?.clear()
                cargarMesas()
            } else {
                Toast.makeText(this, "Error al registrar la mesa", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SQLiteConstraintException) {
            Toast.makeText(this, getString(R.string.error_mesa_existe), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al registrar la mesa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarMesas() {
        val lista = mesaDao.listar()
        adapter.actualizarLista(lista)
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
