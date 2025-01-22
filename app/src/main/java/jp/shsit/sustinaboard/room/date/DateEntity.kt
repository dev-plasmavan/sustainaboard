package jp.shsit.sustinaboard.room.date

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "date_table")
data class DateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "name") val name: String
)
