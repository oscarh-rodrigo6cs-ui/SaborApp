package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import pe.edu.senati.saborapp.data.PlatoDao
import pe.edu.senati.saborapp.databinding.ActivityPlatosBinding
import pe.edu.senati.saborapp.ui.adapter.PlatoAdapter

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private lateinit var platoDao: PlatoDao
    private lateinit var adapter: PlatoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        adapter = PlatoAdapter(emptyList()) { plato ->
            val intent = Intent(this, PlatoFormActivity::class.java)
            intent.putExtra("PLATO_ID", plato.id)
            startActivity(intent)
        }

        binding.rvPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvPlatos.adapter = adapter

        binding.etBuscarPlato.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            adapter.actualizarLista(platoDao.buscarPorNombre(query))
        }

        binding.btnGuardar.setOnClickListener {
            val intent = Intent(this, PlatoFormActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val query = binding.etBuscarPlato.text?.toString() ?: ""
        adapter.actualizarLista(platoDao.buscarPorNombre(query))
    }
}
