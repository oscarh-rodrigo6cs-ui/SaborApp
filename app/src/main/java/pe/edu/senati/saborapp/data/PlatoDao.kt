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

    fun actualizar(plato: Plato): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put("disponible", plato.disponible)
        }
        return db.update(DBHelper.TABLE_PLATO, values, "id = ?", arrayOf(plato.id.toString()))
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DBHelper.TABLE_DETALLE_PEDIDO} WHERE id_plato = ?",
            arrayOf(id.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()

        if (count > 0) {
            throw IllegalStateException("No se puede eliminar: tiene pedidos")
        }

        return db.delete(DBHelper.TABLE_PLATO, "id = ?", arrayOf(id.toString()))
    }

    fun listar(filtro: String = ""): List<Plato> {
        val list = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val sql = "SELECT id, nombre, categoria, precio, disponible FROM ${DBHelper.TABLE_PLATO} WHERE nombre LIKE ? OR categoria LIKE ? ORDER BY id DESC"
        val queryParam = "%$filtro%"
        val cursor = db.rawQuery(sql, arrayOf(queryParam, queryParam))
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

    fun obtenerPorId(id: Int): Plato? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre, categoria, precio, disponible FROM ${DBHelper.TABLE_PLATO} WHERE id = ?", arrayOf(id.toString()))
        var plato: Plato? = null
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible"))
            plato = Plato(id, nombre, categoria, precio, disponible)
        }
        cursor.close()
        return plato
    }
}
