package pe.edu.senati.saborapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "saborapp.db"
        private const val DATABASE_VERSION = 2

        // Tablas
        const val TABLE_USUARIO = "usuario"
        const val TABLE_PLATO = "plato"
        const val TABLE_MESA = "mesa"
        const val TABLE_PEDIDO = "pedido"
        const val TABLE_DETALLE_PEDIDO = "detalle_pedido"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla Usuario
        val createUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL
            );
        """.trimIndent()

        // Tabla Plato
        val createPlato = """
            CREATE TABLE $TABLE_PLATO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT,
                precio REAL,
                disponible INTEGER
            );
        """.trimIndent()

        // Tabla Mesa
        val createMesa = """
            CREATE TABLE $TABLE_MESA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER UNIQUE NOT NULL,
                capacidad INTEGER NOT NULL,
                estado TEXT NOT NULL
            );
        """.trimIndent()

        // Tabla Pedido (DB_VERSION 2)
        val createPedido = """
            CREATE TABLE $TABLE_PEDIDO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mesa INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                estado TEXT NOT NULL,
                total REAL NOT NULL,
                FOREIGN KEY(id_mesa) REFERENCES $TABLE_MESA(id)
            );
        """.trimIndent()

        // Tabla Detalle Pedido (DB_VERSION 2)
        val createDetallePedido = """
            CREATE TABLE $TABLE_DETALLE_PEDIDO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_plato INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precio_unit REAL NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY(id_pedido) REFERENCES $TABLE_PEDIDO(id) ON DELETE CASCADE,
                FOREIGN KEY(id_plato) REFERENCES $TABLE_PLATO(id)
            );
        """.trimIndent()

        db.execSQL(createUsuario)
        db.execSQL(createPlato)
        db.execSQL(createMesa)
        db.execSQL(createPedido)
        db.execSQL(createDetallePedido)

        // Seed data
        // Insert admin user
        db.execSQL("INSERT INTO $TABLE_USUARIO (usuario, clave, rol) VALUES ('admin', '1234', 'ADMIN');")
        db.execSQL("INSERT INTO $TABLE_USUARIO (usuario, clave, rol) VALUES ('mozo', '1234', 'MOZO');")

        // Seed mesas
        for (i in 1..6) {
            db.execSQL("INSERT INTO $TABLE_MESA (numero, capacidad, estado) VALUES ($i, 4, 'LIBRE');")
        }

        // Seed platos
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Causa Rellena', 'Entradas', 18.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Lomo Saltado', 'Fondos', 35.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Ají de Gallina', 'Fondos', 28.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Chicha Morada 1L', 'Bebidas', 12.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Crema Volteada', 'Postres', 15.00, 1);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createPedido = """
                CREATE TABLE IF NOT EXISTS $TABLE_PEDIDO (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_mesa INTEGER NOT NULL,
                    fecha TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    total REAL NOT NULL,
                    FOREIGN KEY(id_mesa) REFERENCES $TABLE_MESA(id)
                );
            """.trimIndent()

            val createDetallePedido = """
                CREATE TABLE IF NOT EXISTS $TABLE_DETALLE_PEDIDO (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_pedido INTEGER NOT NULL,
                    id_plato INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL,
                    precio_unit REAL NOT NULL,
                    subtotal REAL NOT NULL,
                    FOREIGN KEY(id_pedido) REFERENCES $TABLE_PEDIDO(id) ON DELETE CASCADE,
                    FOREIGN KEY(id_plato) REFERENCES $TABLE_PLATO(id)
                );
            """.trimIndent()

            db.execSQL(createPedido)
            db.execSQL(createDetallePedido)
        }
    }
}
