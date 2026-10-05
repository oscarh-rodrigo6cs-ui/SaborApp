package pe.edu.senati.saborapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.edu.senati.saborapp.R
import pe.edu.senati.saborapp.data.UsuarioDao
import pe.edu.senati.saborapp.databinding.ActivityLoginBinding
import pe.edu.senati.saborapp.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        usuarioDao = UsuarioDao(this)

        if (sessionManager.haySesion()) {
            irAMenu()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            binding.tilUsuario.error = null
            binding.tilClave.error = null

            var hayError = false

            if (usuario.isEmpty()) {
                binding.tilUsuario.error = getString(R.string.error_campo_vacio)
                hayError = true
            }

            if (clave.isEmpty()) {
                binding.tilClave.error = getString(R.string.error_campo_vacio)
                hayError = true
            }

            if (hayError) return@setOnClickListener

            val user = usuarioDao.validarUsuario(usuario, clave)

            if (user != null) {
                sessionManager.guardarSesion(user.usuario, user.rol)
                irAMenu()
            } else {
                Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
}
