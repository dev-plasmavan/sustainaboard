package jp.shsit.sustinaboard.room.mark

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jp.shsit.sustinaboard.ui.actions.sdgsmarks.MarkGroup
import java.util.*

@Dao
interface MarkDao {
    @Query("SELECT * FROM mark_table")
    fun getAll(): LiveData<List<MarkEntity>>

    @Insert
    fun insert(vararg users: MarkEntity)

    @Query("DELETE FROM mark_table")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM mark_table")
    fun count(): LiveData<Int>

    @Query("SELECT * FROM mark_table WHERE date = :targetDate")
    fun findDate(targetDate: String):LiveData<List<MarkEntity>>

    @Query("SELECT no,date,name,COUNT(name) AS count  FROM mark_table Group BY no ")
    fun GroupDateMark(): LiveData<List<MarkGroup>>
    // @Query("SELECT *  FROM mark_table Group BY no ")
    // fun GroupDate(): LiveData<List<MarkEntity>>

    @Query("DELETE FROM mark_table WHERE id= :id_obj")
    fun del(id_obj:Int)

    @Query("SELECT * FROM mark_table WHERE date LIKE '%'||:date ||'%' ORDER BY id DESC")
    fun MonSelect(date: String):LiveData<List<MarkEntity>>

    @Query("SELECT no,date,name,COUNT(name) AS count FROM mark_table WHERE date LIKE '%'||:date ||'%' Group BY no ")
    fun MonSelectCount(date: String):LiveData<List<MarkGroup>>

    @Query("SELECT COUNT(*) FROM mark_table WHERE date = :date")
    fun datecount(date: String): LiveData<Int>

}