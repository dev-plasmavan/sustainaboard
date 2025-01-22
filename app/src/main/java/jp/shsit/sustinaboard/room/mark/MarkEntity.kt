package jp.shsit.sustinaboard.room.mark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mark_table")
data class MarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "no") val no: String,
    @ColumnInfo(name = "name") val name: String
)
