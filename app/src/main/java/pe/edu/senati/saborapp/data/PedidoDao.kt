package pe.edu.senati.saborapp.data

import android.content.ContentValues
import android.content.Context
import pe.edu.senati.saborapp.model.DetallePedido
import pe.edu.senati.saborapp.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {
    private val dbHelper = DBHelper(context)

    fun obtenerPedidoAbiertoPorMesa(idMesa: Int): Pedido? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, id_mesa, fecha, estado, total FROM ${DBHelper.TABLE_PEDIDO} WHERE id_mesa = ? AND estado = 'ABIERTO'",
            arrayOf(idMesa.toString())
        )
        var pedido: Pedido? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            pedido = Pedido(id, idMesa, fecha, estado, total)
        }
        cursor.close()
        return pedido
    }

    fun crearOObtenerPedidoAbierto(idMesa: Int): Pedido {
        val existente = obtenerPedidoAbiertoPorMesa(idMesa)
        if (existente != null) return existente

        val db = dbHelper.writableDatabase
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put("id_mesa", idMesa)
            put("fecha", fecha)
            put("estado", "ABIERTO")
            put("total", 0.0)
        }
        val id = db.insert(DBHelper.TABLE_PEDIDO, null, values).toInt()

        // Cambiar estado de la mesa a OCUPADA
        val mesaValues = ContentValues().apply { put("estado", "OCUPADA") }
        db.update(DBHelper.TABLE_MESA, mesaValues, "id = ?", arrayOf(idMesa.toString()))

        return Pedido(id, idMesa, fecha, "ABIERTO", 0.0)
    }

    fun agregarItemAPedido(idPedido: Int, idPlato: Int, cantidad: Int, precioUnit: Double): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val subtotal = cantidad * precioUnit
            val values = ContentValues().apply {
                put("id_pedido", idPedido)
                put("id_plato", idPlato)
                put("cantidad", cantidad)
                put("precio_unit", precioUnit) // Congelar precio_unit
                put("subtotal", subtotal)
            }
            db.insert(DBHelper.TABLE_DETALLE_PEDIDO, null, values)

            // Recalcular total del pedido
            recalcularTotalPedido(db, idPedido)

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun eliminarDetalle(idDetalle: Int, idPedido: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(DBHelper.TABLE_DETALLE_PEDIDO, "id = ?", arrayOf(idDetalle.toString()))
            recalcularTotalPedido(db, idPedido)
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    private fun recalcularTotalPedido(db: android.database.sqlite.SQLiteDatabase, idPedido: Int) {
        val cursor = db.rawQuery(
            "SELECT SUM(subtotal) FROM ${DBHelper.TABLE_DETALLE_PEDIDO} WHERE id_pedido = ?",
            arrayOf(idPedido.toString())
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()

        val values = ContentValues().apply { put("total", total) }
        db.update(DBHelper.TABLE_PEDIDO, values, "id = ?", arrayOf(idPedido.toString()))
    }

    fun obtenerDetallesPorPedido(idPedido: Int): List<DetallePedido> {
        val list = mutableListOf<DetallePedido>()
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT dp.id, dp.id_pedido, dp.id_plato, dp.cantidad, dp.precio_unit, dp.subtotal, p.nombre as nombre_plato
            FROM ${DBHelper.TABLE_DETALLE_PEDIDO} dp
            INNER JOIN ${DBHelper.TABLE_PLATO} p ON dp.id_plato = p.id
            WHERE dp.id_pedido = ?
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(idPedido.toString()))
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val idPed = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"))
            val idPlato = cursor.getInt(cursor.getColumnIndexOrThrow("id_plato"))
            val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            val precioUnit = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unit"))
            val subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))
            val nombrePlato = cursor.getString(cursor.getColumnIndexOrThrow("nombre_plato"))

            list.add(DetallePedido(id, idPed, idPlato, cantidad, precioUnit, subtotal, nombrePlato))
        }
        cursor.close()
        return list
    }

    fun cerrarCuenta(idPedido: Int, idMesa: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT SUM(subtotal) FROM ${DBHelper.TABLE_DETALLE_PEDIDO} WHERE id_pedido = ?",
                arrayOf(idPedido.toString())
            )
            var total = 0.0
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0)
            }
            cursor.close()

            val pedidoValues = ContentValues().apply {
                put("estado", "CERRADO")
                put("total", total)
            }
            db.update(DBHelper.TABLE_PEDIDO, pedidoValues, "id = ?", arrayOf(idPedido.toString()))

            val mesaValues = ContentValues().apply {
                put("estado", "LIBRE")
            }
            db.update(DBHelper.TABLE_MESA, mesaValues, "id = ?", arrayOf(idMesa.toString()))

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }
}
