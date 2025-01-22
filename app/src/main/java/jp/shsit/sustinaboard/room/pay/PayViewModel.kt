package jp.shsit.sustinaboard.room.pay

import android.app.Application
import androidx.lifecycle.*
import jp.shsit.sustinaboard.room.SustinaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PayViewModel (application: Application) : AndroidViewModel(application) {

    private val dao: PayDao

    init {
        val db = SustinaDatabase.buildDatabase(application)
        dao = db.payDao()
    }

    fun insert(word: String, price: Int, date:String, kind:String, company:String, sum:Int, group:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                PayEntity(
                    id = 0,
                    word = word,
                    price = price,
                    date = date,
                    kind = kind,
                    company = company,
                    sum = sum,
                    group = group
                )
            )
        }
    }

    fun update(id: Int, word: String, price: Int, date: String, kind: String, company: String, sum: Int, group: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(
                PayEntity(
                    id = id,
                    word = word,
                    price = price,
                    date = date,
                    kind = kind,
                    company = company,
                    sum = sum,
                    group = group
                )
            )
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAll()
        }
    }

    fun del(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.del(id)
        }
    }

    fun monSelect(date: String): LiveData<List<PayEntity>> {
        return dao.monSelect(date)
    }

    fun monSelectGroup(date: String): LiveData<List<PayEntity>> {
        return dao.monSelectGroup(date)
    }

    fun groupSelect(number: Int): LiveData<List<PayEntity>> {
        return dao.groupSelect(number)
    }

    fun deleteGroup(group: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteGroup(group)
        }
    }

}