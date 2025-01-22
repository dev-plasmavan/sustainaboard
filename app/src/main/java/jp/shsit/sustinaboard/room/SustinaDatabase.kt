package jp.shsit.sustinaboard.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import jp.shsit.sustinaboard.room.date.DateDao
import jp.shsit.sustinaboard.room.date.DateEntity
import jp.shsit.sustinaboard.room.income.IncomeDao
import jp.shsit.sustinaboard.room.income.IncomeEntity
import jp.shsit.sustinaboard.room.mark.MarkDao
import jp.shsit.sustinaboard.room.mark.MarkEntity
import jp.shsit.sustinaboard.room.pay.PayDao
import jp.shsit.sustinaboard.room.pay.PayEntity

@Database(entities = [PayEntity::class, IncomeEntity::class, MarkEntity::class, DateEntity::class], version = 1, exportSchema = false )
abstract class SustinaDatabase : RoomDatabase() {
    abstract fun payDao(): PayDao
    abstract fun incomeDao(): IncomeDao
    abstract fun markDao(): MarkDao

    abstract fun dateDao(): DateDao

    companion object {
/*
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE date_table (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            " date TEXT  " +
                            " name TEXT  " +
                            ");"
                )
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE mark_table (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            " date TEXT  " +
                            " no TEXT " +
                            " name TEXT  " +
                            ");"
                )
                database.execSQL(
                    "CREATE TABLE income_table (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            " price INTEGER " +
                            " date TEXT  " +
                            " kind TEXT  " +
                            ");"
                )
            }
        }
*/
        fun buildDatabase(context: Context): SustinaDatabase {
            return Room.databaseBuilder(
                context,
                SustinaDatabase::class.java, "database-name"
            ).apply {
             //   addMigrations(MIGRATION_4_5)
                // fallbackToDestructiveMigration()
            }.build()
        }
    }
}