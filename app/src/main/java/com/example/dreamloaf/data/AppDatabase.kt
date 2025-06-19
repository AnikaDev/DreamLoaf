package com.example.dreamloaf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class, Product::class, Production::class, Sale::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?
    abstract fun productDao(): ProductDao?
    abstract fun productionDao(): ProductionDao?
    abstract fun saleDao(): SaleDao?

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        private val MIGRATION_1_4: Migration = object : Migration(1, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `products_new` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT, " +
                            "`weight` REAL NOT NULL, " +
                            "`price` REAL NOT NULL, " +
                            "`costPrice` REAL NOT NULL DEFAULT 0)"
                )
                database.execSQL(
                    "INSERT INTO products_new (id, name, weight, price, costPrice) " +
                            "SELECT id, name, weight, price, 0 FROM products"
                )
                database.execSQL("DROP TABLE products")
                database.execSQL("ALTER TABLE products_new RENAME TO products")
            }
        }

        fun getInstance(context: Context): AppDatabase? {
            if (AppDatabase.Companion.INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (AppDatabase.Companion.INSTANCE == null) {
                        AppDatabase.Companion.INSTANCE = databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "dreamloaf_database.db"
                        )
                            .addMigrations(AppDatabase.Companion.MIGRATION_1_4)
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return AppDatabase.Companion.INSTANCE
        }
    }
}