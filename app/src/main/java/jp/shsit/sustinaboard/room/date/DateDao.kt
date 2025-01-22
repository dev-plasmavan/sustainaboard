package jp.shsit.sustinaboard.room.date

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface DateDao {
    @Query("SELECT * FROM date_table")
    fun getAll(): LiveData<List<DateEntity>>

    @Insert
    fun insert(vararg users: DateEntity)

    @Query("DELETE FROM date_table")
    fun deleteAll()

    @Query("DELETE FROM date_table WHERE id= :id_obj")
    fun del(id_obj:Int)

}