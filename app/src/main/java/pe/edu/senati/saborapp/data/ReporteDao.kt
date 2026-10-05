package pe.edu.senati.saborapp.data

import android.content.Context

data class PlatoReporte(val nombre: String, val cantidadTotal: Int)

class ReporteDao(context: Context) {
    private val dbHelper = DBHelper(context)

    fun obtenerVentaDelDia(): Double {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT SUM(total) FROM ${DBHelper.TABLE_PEDIDO}
            WHERE estado = 'CERRADO' AND date(fecha) = date('now', 'localtime')
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun obtenerCantidadPedidosDia(): Int {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT COUNT(*) FROM ${DBHelper.TABLE_PEDIDO}
            WHERE estado = 'CERRADO' AND date(fecha) = date('now', 'localtime')
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }
        cursor.close()
        return cantidad
    }

    fun obtenerTop5Platos(): List<PlatoReporte> {
        val list = mutableListOf<PlatoReporte>()
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT p.nombre, SUM(dp.cantidad) as total_vendido
            FROM ${DBHelper.TABLE_DETALLE_PEDIDO} dp
            INNER JOIN ${DBHelper.TABLE_PLATO} p ON dp.id_plato = p.id
            INNER JOIN ${DBHelper.TABLE_PEDIDO} ped ON dp.id_pedido = ped.id
            WHERE ped.estado = 'CERRADO'
            GROUP BY p.id, p.nombre
            ORDER BY total_vendido DESC
            LIMIT 5
        """.trimIndent()

        val cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val cantidad = cursor.getInt(1)
            list.add(PlatoReporte(nombre, cantidad))
        }
        cursor.close()
        return list
    }
}
