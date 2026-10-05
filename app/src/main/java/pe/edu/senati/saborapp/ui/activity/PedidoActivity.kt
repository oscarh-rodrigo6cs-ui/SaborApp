package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.PedidoDao
import pe.edu.senati.saborapp.data.PlatoDao
import pe.edu.senati.saborapp.databinding.ActivityPedidoBinding
import pe.edu.senati.saborapp.model.Pedido
import pe.edu.senati.saborapp.model.Plato
import pe.edu.senati.saborapp.ui.adapter.DetalleAdapter
import java.util.Locale

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var platoDao: PlatoDao
    private lateinit var adapter: DetalleAdapter

    private var idMesa: Int = -1
    private var numeroMesa: Int = -1
    private var pedidoActual: Pedido? = null
    private var listaPlatosDisponibles: List<Plato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idMesa = intent.getIntExtra("MESA_ID", -1)
        numeroMesa = intent.getIntExtra("MESA_NUMERO", -1)

        if (idMesa == -1) {
            idMesa = 1
            numeroMesa = 1
        }

        val titulo = getString(R.string.pedido_mesa_formato, numeroMesa)
        supportActionBar?.title = titulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvTitulo.text = titulo

        pedidoDao = PedidoDao(this)
        platoDao = PlatoDao(this)

        adapter = DetalleAdapter(emptyList(), esEditable = true) { detalle ->
            pedidoActual?.let { ped ->
                pedidoDao.eliminarItemPedido(detalle.id, ped.id)
                cargarPedidoYDetalles()
            }
        }

        binding.rvDetallePedido.layoutManager = LinearLayoutManager(this)
        binding.rvDetallePedido.adapter = adapter

        cargarPlatosEnSpinner()
        cargarPedidoYDetalles()

        binding.btnAgregarPlato.setOnClickListener {
            agregarPlato()
        }

        binding.btnCerrarCuenta.setOnClickListener {
            val intent = Intent(this, CuentaActivity::class.java)
            intent.putExtra("MESA_ID", idMesa)
            intent.putExtra("MESA_NUMERO", numeroMesa)
            startActivity(intent)
        }
    }

    private fun cargarPlatosEnSpinner() {
        listaPlatosDisponibles = platoDao.listar().filter { it.disponible == 1 }
        val nombres = listaPlatosDisponibles.map {
            "${it.nombre} (${it.categoria}) - S/ ${String.format(Locale.getDefault(), "%.2f", it.precio)}"
        }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)
        binding.spCategoria.adapter = spinnerAdapter
    }

    private fun agregarPlato() {
        val selectedIndex = binding.spCategoria.selectedItemPosition
        if (selectedIndex < 0 || selectedIndex >= listaPlatosDisponibles.size) {
            Toast.makeText(this, "Seleccione un plato disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val cantStr = binding.etCantidad.text.toString().trim()
        val cantidad = cantStr.toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            binding.tilCantidad.error = "Ingrese una cantidad válida (>0)"
            return
        }

        binding.tilCantidad.error = null
        val plato = listaPlatosDisponibles[selectedIndex]

        val ok = pedidoDao.agregarItemPedido(idMesa, plato.id, cantidad, plato.precio)
        if (ok) {
            Toast.makeText(this, "Plato agregado al pedido", Toast.LENGTH_SHORT).show()
            binding.etCantidad.setText("1")
            cargarPedidoYDetalles()
        } else {
            Toast.makeText(this, "Error al agregar el plato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarPedidoYDetalles() {
        pedidoActual = pedidoDao.obtenerPedidoAbiertoPorMesa(idMesa)
        val ped = pedidoActual
        if (ped != null) {
            val detalles = pedidoDao.obtenerDetallePedido(ped.id)
            adapter.actualizarLista(detalles)
            val total = detalles.sumOf { it.subtotal }
            binding.tvTotal.text = String.format(Locale.getDefault(), "Total: S/ %.2f", total)
        } else {
            adapter.actualizarLista(emptyList())
            binding.tvTotal.text = "Total: S/ 0.00"
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPlatosEnSpinner()
        cargarPedidoYDetalles()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
