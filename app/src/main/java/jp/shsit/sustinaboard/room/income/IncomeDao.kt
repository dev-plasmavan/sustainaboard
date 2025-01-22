package jp.shsit.sustinaboard.room.income

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income_table")
    fun getAll(): LiveData<List<IncomeEntity>>

    @Insert
    fun insert(vararg users: IncomeEntity)

    @Query("DELETE FROM income_table")
    fun deleteAll()
    //件数
    @Query("SELECT COUNT(*) FROM income_table")
    fun count(): LiveData<Int>

    @Query("SELECT * FROM income_table WHERE date = :targetDate")
    fun findDate(targetDate: String):LiveData<List<IncomeEntity>>

    @Query("SELECT sum(price)  FROM income_table Group BY date")
    fun groupDatePrice(): LiveData<List<Int>>
    /*
    @Query("SELECT price,date  FROM pay_table Group BY date")
    fun groupDate(): LiveData<List<Group>>
*/
    @Query("DELETE FROM income_table WHERE id= :id_obj")
    fun del(id_obj:Int)

    @Query("SELECT * FROM income_table WHERE date LIKE '%'||:date ||'%' ORDER BY date DESC")
    fun monSelect(date: String):LiveData<List<IncomeEntity>>

}