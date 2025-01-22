package jp.shsit.sustinaboard.room.date

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import jp.shsit.sustinaboard.room.SustinaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DateViewModel (application: Application) : AndroidViewModel(application) {

    private val dao: DateDao

    init {
        val db = SustinaDatabase.buildDatabase(application)
        dao = db.dateDao()
    }

    var items = dao.getAll()

    fun insert(date:String,name:String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                DateEntity(
                    id = 0,
                    date = date,
                    name = name
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
}