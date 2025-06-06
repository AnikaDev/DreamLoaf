package com.example.dreamloaf.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {User.class, Product.class, Production.class, Sale.class},
        version = 4,
        exportSchema = false
)

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract ProductionDao productionDao();
    public abstract SaleDao saleDao();

    private static volatile AppDatabase INSTANCE;


    private static final Migration MIGRATION_1_4 = new Migration(1, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `products_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`weight` REAL NOT NULL, " +
                    "`price` REAL NOT NULL, " +
                    "`costPrice` REAL NOT NULL DEFAULT 0)");
            database.execSQL("INSERT INTO products_new (id, name, weight, price, costPrice) " +
                    "SELECT id, name, weight, price, 0 FROM products");
            database.execSQL("DROP TABLE products");
            database.execSQL("ALTER TABLE products_new RENAME TO products");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "dreamloaf_database.db")
                            .addMigrations(MIGRATION_1_4)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}