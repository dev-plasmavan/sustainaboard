package jp.shsit.sustinaboard.room.income

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_table")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "kind") val kind: String
)