package jp.shsit.sustinaboard.room.income

import android.app.Application
import androidx.lifecycle.*
import jp.shsit.sustinaboard.room.SustinaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IncomeViewModel (application: Application) : AndroidViewModel(application) {

    private val dao: IncomeDao

    init {
        val db = SustinaDatabase.buildDatabase(application)
        dao = db.incomeDao()
    }

    var items = dao.getAll()

    fun insert(price:Int,date:String,kind:String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                IncomeEntity(
                    id = 0,
                    price = price,
                    date = date,
                    kind = kind
                )
            )
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAll()
        }
    }

    fun SelectDate(date: String): LiveData<List<IncomeEntity>> {
        return dao.findDate(date)
    }

    var datacount = dao.count()

    //var GroupDate = dao.GroupDate()
    // var GroupDatePrice = dao.GroupDatePrice()

    //1件削除
    fun del(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.del(id)
        }
    }

    fun monSelect(mon: String): LiveData<List<IncomeEntity>> {
        return dao.monSelect(mon)

    }
}