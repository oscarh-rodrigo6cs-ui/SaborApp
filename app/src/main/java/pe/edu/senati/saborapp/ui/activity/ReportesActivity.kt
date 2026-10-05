package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.ReporteDao
import pe.edu.senati.saborapp.databinding.ActivityReportesBinding
import pe.edu.senati.saborapp.ui.adapter.TopPlatoAdapter
import pe.edu.senati.saborapp.utils.SessionManager
import java.util.Locale

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var reporteDao: ReporteDao
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: TopPlatoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.obtenerRol().uppercase() != "ADMIN") {
            Toast.makeText(this, getString(R.string.acceso_denegado_admin), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.reportes_ventas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        reporteDao = ReporteDao(this)

        adapter = TopPlatoAdapter(emptyList())
        binding.rvTopPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvTopPlatos.adapter = adapter

        cargarReportes()

        binding.btnCompartirResumen.setOnClickListener {
            compartirResumen()
        }
    }

    private fun cargarReportes() {
        val ventaTotal = reporteDao.obtenerVentaDelDia()
        val totalPedidos = reporteDao.obtenerTotalPedidosDia()
        val ticketPromedio = reporteDao.obtenerTicketPromedioDia()
        val topPlatos = reporteDao.obtenerTopPlatosVendidos()

        binding.tvVentaDia.text = String.format(Locale.getDefault(), "S/ %.2f", ventaTotal)
        binding.tvTotalPedidos.text = totalPedidos.toString()
        binding.tvTicketPromedio.text = String.format(Locale.getDefault(), "S/ %.2f", ticketPromedio)

        adapter.actualizarLista(topPlatos)
    }

    private fun compartirResumen() {
        val ventaTotal = reporteDao.obtenerVentaDelDia()
        val totalPedidos = reporteDao.obtenerTotalPedidosDia()
        val ticketPromedio = reporteDao.obtenerTicketPromedioDia()
        val topPlatos = reporteDao.obtenerTopPlatosVendidos()

        val sb = StringBuilder()
        sb.append("📊 *SABORAPP - RESUMEN DE VENTAS HOY* 📊\n")
        sb.append("--------------------------------------\n")
        sb.append("💰 *Venta Total:* S/ ").append(String.format(Locale.getDefault(), "%.2f", ventaTotal)).append("\n")
        sb.append("📋 *Total Pedidos:* ").append(totalPedidos).append("\n")
        sb.append("🎟️ *Ticket Promedio:* S/ ").append(String.format(Locale.getDefault(), "%.2f", ticketPromedio)).append("\n\n")

        if (topPlatos.isNotEmpty()) {
            sb.append("🔥 *Top Platos Vendidos:*\n")
            topPlatos.forEachIndexed { idx, item ->
                sb.append(" ${idx + 1}. ").append(item.nombre).append(" (").append(item.cantidadTotal).append(" und.)\n")
            }
        }

        sb.append("--------------------------------------\n")
        sb.append("¡Pollería El Buen Sabor!")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }

        val chooser = Intent.createChooser(sendIntent, "Compartir reporte del día vía")
        startActivity(chooser)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
