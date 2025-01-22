package jp.shsit.sustinaboard.room.pay

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface PayDao {
    @Query("SELECT * FROM pay_table")
    fun getAll(): LiveData<List<PayEntity>>

    @Insert
    fun insert(vararg users: PayEntity)

    @Update
    fun update(vararg users: PayEntity)

    @Query("DELETE FROM pay_table")
    fun deleteAll()
    //件数
    @Query("SELECT COUNT(*) FROM pay_table")
    fun count(): LiveData<Int>

    @Query("SELECT * FROM pay_table WHERE date = :targetDate")
    fun findDate(targetDate: String):LiveData<List<PayEntity>>

    @Query("SELECT sum(price)  FROM pay_table Group BY date")
    fun groupDatePrice(): LiveData<List<Int>>
    /*
    @Query("SELECT price,date  FROM pay_table Group BY date")
    fun groupDate(): LiveData<List<Group>>
*/
    @Query("DELETE FROM pay_table WHERE id= :id_obj")
    fun del(id_obj:Int)

    @Query("DELETE FROM pay_table WHERE `group`= :id_obj")
    fun deleteGroup(id_obj: Int)

    @Query("SELECT * FROM pay_table WHERE date LIKE '%'||:date ||'%' ORDER BY date DESC")
    fun monSelect(date: String):LiveData<List<PayEntity>>

    @Query("SELECT *  FROM pay_table WHERE date LIKE '%'||:date ||'%' Group BY `group` ")
    fun monSelectGroup(date: String):LiveData<List<PayEntity>>

    @Query("SELECT * FROM pay_table WHERE `group` = :number")
    fun groupSelect(number: Int):LiveData<List<PayEntity>>

}