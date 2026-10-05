package pe.edu.senati.saborapp.data

import android.content.Context

data class PlatoTop(val nombre: String, val cantidadTotal: Int)

class ReporteDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun obtenerVentaDelDia(): Double {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT SUM(total) FROM ${DBHelper.TABLE_PEDIDO}
            WHERE estado = 'CERRADO' AND date(fecha) = date('now','localtime')
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun obtenerTotalPedidosDia(): Int {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT COUNT(id) FROM ${DBHelper.TABLE_PEDIDO}
            WHERE estado = 'CERRADO' AND date(fecha) = date('now','localtime')
        """.trimIndent()
        val cursor = db.rawQuery(sql, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun obtenerTicketPromedioDia(): Double {
        val ventaTotal = obtenerVentaDelDia()
        val totalPedidos = obtenerTotalPedidosDia()
        return if (totalPedidos > 0) ventaTotal / totalPedidos else 0.0
    }

    fun obtenerTopPlatosVendidos(): List<PlatoTop> {
        val list = mutableListOf<PlatoTop>()
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT p.nombre, SUM(dp.cantidad) as total_cant
            FROM ${DBHelper.TABLE_DETALLE_PEDIDO} dp
            JOIN ${DBHelper.TABLE_PEDIDO} pe ON dp.id_pedido = pe.id
            JOIN ${DBHelper.TABLE_PLATO} p ON dp.id_plato = p.id
            WHERE pe.estado = 'CERRADO'
            GROUP BY p.id
            ORDER BY total_cant DESC
            LIMIT 5
        """.trimIndent()

        val cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val cant = cursor.getInt(1)
            list.add(PlatoTop(nombre, cant))
        }
        cursor.close()
        return list
    }
}
