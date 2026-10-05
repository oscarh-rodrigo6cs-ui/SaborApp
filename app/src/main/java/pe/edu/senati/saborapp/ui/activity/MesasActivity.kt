package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
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

        mesaDao = MesaDao(this)

        adapter = MesaAdapter(emptyList()) { mesa ->
            abrirPedidoOCuenta(mesa)
        }

        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter

        binding.btnGuardar.setOnClickListener {
            val numStr = binding.etNumeroMesa.text.toString().trim()
            val capStr = binding.etCapacidadMesa.text.toString().trim()

            if (numStr.isEmpty() || capStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val numero = numStr.toIntOrNull()
            val capacidad = capStr.toIntOrNull()

            if (numero == null || capacidad == null || numero <= 0 || capacidad <= 0) {
                Toast.makeText(this, "Número y capacidad deben ser mayores a 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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
                    Toast.makeText(this, "Error al guardar mesa", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "El número de mesa ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirPedidoOCuenta(mesa: Mesa) {
        val intent = Intent(this, PedidoActivity::class.java)
        intent.putExtra("MESA_ID", mesa.id)
        intent.putExtra("MESA_NUMERO", mesa.numero)
        startActivity(intent)
    }

    private fun cargarMesas() {
        adapter.actualizarLista(mesaDao.obtenerTodas())
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
    }
}
