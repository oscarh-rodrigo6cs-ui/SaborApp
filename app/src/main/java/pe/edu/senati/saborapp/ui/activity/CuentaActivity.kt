package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.PedidoDao
import pe.edu.senati.saborapp.databinding.ActivityCuentaBinding
import pe.edu.senati.saborapp.model.DetallePedido
import pe.edu.senati.saborapp.model.Pedido
import pe.edu.senati.saborapp.ui.adapter.DetalleAdapter
import java.util.Locale

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var adapter: DetalleAdapter

    private var idMesa: Int = -1
    private var numeroMesa: Int = -1
    private var pedidoActual: Pedido? = null
    private var listaDetalles: List<DetallePedido> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idMesa = intent.getIntExtra("MESA_ID", -1)
        numeroMesa = intent.getIntExtra("MESA_NUMERO", -1)

        val titulo = if (numeroMesa > 0) getString(R.string.pedido_mesa_formato, numeroMesa) else getString(R.string.btn_cerrar_cuenta)
        supportActionBar?.title = titulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvTitulo.text = titulo

        pedidoDao = PedidoDao(this)

        adapter = DetalleAdapter(emptyList(), esEditable = false)
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(this)
        binding.rvDetallePedido.adapter = adapter

        cargarPedido()

        binding.btnCerrarCuenta.setOnClickListener {
            val ped = pedidoActual
            if (ped == null || listaDetalles.isEmpty()) {
                Toast.makeText(this, getString(R.string.sin_items), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ok = pedidoDao.cerrarCuenta(ped.id, idMesa)
            if (ok) {
                Toast.makeText(this, getString(R.string.cuenta_cerrada), Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error al cerrar cuenta", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCompartirWhatsapp.setOnClickListener {
            compartirPorWhatsapp()
        }
    }

    private fun cargarPedido() {
        pedidoActual = pedidoDao.obtenerPedidoAbiertoPorMesa(idMesa)
        val ped = pedidoActual
        if (ped != null) {
            listaDetalles = pedidoDao.obtenerDetallePedido(ped.id)
            adapter.actualizarLista(listaDetalles)
            val total = listaDetalles.sumOf { it.subtotal }
            binding.tvTotal.text = String.format(Locale.getDefault(), "Total: S/ %.2f", total)
        } else {
            listaDetalles = emptyList()
            adapter.actualizarLista(emptyList())
            binding.tvTotal.text = "Total: S/ 0.00"
        }
    }

    private fun compartirPorWhatsapp() {
        val total = listaDetalles.sumOf { it.subtotal }
        val sb = StringBuilder()
        sb.append("🍽️ *SABORAPP - TICKET DE CONSUMO* 🍽️\n")
        sb.append("----------------------------------\n")
        if (numeroMesa > 0) {
            sb.append("📍 *Mesa:* ").append(numeroMesa).append("\n\n")
        }

        if (listaDetalles.isEmpty()) {
            sb.append("No hay productos registrados.\n")
        } else {
            for (item in listaDetalles) {
                sb.append("• ").append(item.cantidad).append("x ")
                    .append(item.nombrePlato).append(" - S/ ")
                    .append(String.format(Locale.getDefault(), "%.2f", item.subtotal)).append("\n")
            }
        }

        sb.append("----------------------------------\n")
        sb.append("💰 *TOTAL:* S/ ").append(String.format(Locale.getDefault(), "%.2f", total)).append("\n\n")
        sb.append("¡Gracias por su preferencia!")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir ticket vía")
        startActivity(shareIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
