package pe.edu.senati.saborapp.data

import android.content.Context
import pe.edu.senati.saborapp.model.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun validarUsuario(usuario: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val sql = "SELECT id, usuario, clave, rol FROM ${DBHelper.TABLE_USUARIO} WHERE usuario = ? AND clave = ?"
        val cursor = db.rawQuery(sql, arrayOf(usuario, clave))

        var user: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val u = cursor.getString(cursor.getColumnIndexOrThrow("usuario"))
            val c = cursor.getString(cursor.getColumnIndexOrThrow("clave"))
            val rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
            user = Usuario(id, u, c, rol)
        }
        cursor.close()
        return user
    }
}
