package pe.edu.senati.saborapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "SaborAppSession"
        private const val KEY_USUARIO = "key_usuario"
        private const val KEY_ROL = "key_rol"
        private const val KEY_IS_LOGGED = "key_is_logged"
    }

    fun guardarSesion(usuario: String, rol: String) {
        prefs.edit().apply {
            putString(KEY_USUARIO, usuario)
            putString(KEY_ROL, rol)
            putBoolean(KEY_IS_LOGGED, true)
            apply()
        }
    }

    fun haySesion(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED, false)
    }

    fun obtenerUsuario(): String {
        return prefs.getString(KEY_USUARIO, "") ?: ""
    }

    fun obtenerRol(): String {
        return prefs.getString(KEY_ROL, "") ?: ""
    }

    fun cerrarSesion() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }
}
