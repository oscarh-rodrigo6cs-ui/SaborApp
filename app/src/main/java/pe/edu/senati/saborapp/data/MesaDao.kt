package pe.edu.senati.saborapp.data

import android.content.ContentValues
import android.content.Context
import pe.edu.senati.saborapp.model.Mesa

class MesaDao(context: Context) {
    private val dbHelper = DBHelper(context)

    fun insertar(mesa: Mesa): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", mesa.numero)
            put("capacidad", mesa.capacidad)
            put("estado", mesa.estado)
        }
        return db.insert(DBHelper.TABLE_MESA, null, values)
    }

    fun actualizarEstado(idMesa: Int, estado: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", estado)
        }
        return db.update(DBHelper.TABLE_MESA, values, "id = ?", arrayOf(idMesa.toString()))
    }

    fun obtenerTodas(): List<Mesa> {
        val list = mutableListOf<Mesa>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, numero, capacidad, estado FROM ${DBHelper.TABLE_MESA} ORDER BY numero ASC", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val numero = cursor.getInt(cursor.getColumnIndexOrThrow("numero"))
            val capacidad = cursor.getInt(cursor.getColumnIndexOrThrow("capacidad"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            list.add(Mesa(id, numero, capacidad, estado))
        }
        cursor.close()
        return list
    }

    fun obtenerPorId(id: Int): Mesa? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, numero, capacidad, estado FROM ${DBHelper.TABLE_MESA} WHERE id = ?", arrayOf(id.toString()))
        var mesa: Mesa? = null
        if (cursor.moveToFirst()) {
            val numero = cursor.getInt(cursor.getColumnIndexOrThrow("numero"))
            val capacidad = cursor.getInt(cursor.getColumnIndexOrThrow("capacidad"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            mesa = Mesa(id, numero, capacidad, estado)
        }
        cursor.close()
        return mesa
    }
}
