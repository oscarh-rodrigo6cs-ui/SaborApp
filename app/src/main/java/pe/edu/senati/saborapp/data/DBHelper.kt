package pe.edu.senati.saborapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "saborapp.db"
        const val DATABASE_VERSION = 2

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
        val createUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL
            );
        """.trimIndent()

        val createPlato = """
            CREATE TABLE $TABLE_PLATO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT,
                precio REAL CHECK(precio > 0),
                disponible INTEGER NOT NULL
            );
        """.trimIndent()

        val createMesa = """
            CREATE TABLE $TABLE_MESA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER UNIQUE NOT NULL,
                capacidad INTEGER NOT NULL,
                estado TEXT NOT NULL
            );
        """.trimIndent()

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

        val createDetallePedido = """
            CREATE TABLE $TABLE_DETALLE_PEDIDO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_plato INTEGER NOT NULL,
                cantidad INTEGER CHECK(cantidad > 0),
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

        // Inserts iniciales
        db.execSQL("INSERT INTO $TABLE_USUARIO (usuario, clave, rol) VALUES ('admin', '1234', 'ADMIN');")
        db.execSQL("INSERT INTO $TABLE_USUARIO (usuario, clave, rol) VALUES ('mozo', '1234', 'MOZO');")

        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('1/4 Pollo a la Brasa', 'Fondos', 22.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('1/2 Pollo a la Brasa', 'Fondos', 42.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Chicha Morada 1L', 'Bebidas', 12.00, 1);")
        db.execSQL("INSERT INTO $TABLE_PLATO (nombre, categoria, precio, disponible) VALUES ('Inka Kola 1.5L', 'Bebidas', 10.00, 1);")

        db.execSQL("INSERT INTO $TABLE_MESA (numero, capacidad, estado) VALUES (1, 4, 'LIBRE');")
        db.execSQL("INSERT INTO $TABLE_MESA (numero, capacidad, estado) VALUES (2, 4, 'LIBRE');")
        db.execSQL("INSERT INTO $TABLE_MESA (numero, capacidad, estado) VALUES (3, 6, 'LIBRE');")
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
                    cantidad INTEGER CHECK(cantidad > 0),
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
