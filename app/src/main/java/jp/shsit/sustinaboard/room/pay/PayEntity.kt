package jp.shsit.sustinaboard.room.pay

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pay_table")
data class PayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "word") val word: String?,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "sum") var sum: Int,
    @ColumnInfo(name = "group") val group: Int
)