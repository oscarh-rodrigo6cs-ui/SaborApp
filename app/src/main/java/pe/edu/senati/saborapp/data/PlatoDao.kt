package pe.edu.senati.saborapp.data

import android.content.ContentValues
import android.content.Context
import pe.edu.senati.saborapp.model.Plato

class PlatoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertar(plato: Plato): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put("disponible", plato.disponible)
        }
        return db.insert(DBHelper.TABLE_PLATO, null, values)
    }

    fun listar(): List<Plato> {
        val list = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, categoria, precio, disponible FROM ${DBHelper.TABLE_PLATO} ORDER BY id DESC",
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible"))
            list.add(Plato(id, nombre, categoria, precio, disponible))
        }
        cursor.close()
        return list
    }
}
