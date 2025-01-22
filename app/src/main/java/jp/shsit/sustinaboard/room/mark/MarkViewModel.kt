package jp.shsit.sustinaboard.room.mark

import android.app.Application
import androidx.lifecycle.*
import jp.shsit.sustinaboard.room.SustinaDatabase
import jp.shsit.sustinaboard.ui.actions.sdgsmarks.MarkGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkViewModel (application: Application) : AndroidViewModel(application) {

    private val dao: MarkDao

    init {
        val db = SustinaDatabase.buildDatabase(application)
        dao = db.markDao()
    }

    var items = dao.getAll()

    fun insert(date:String,no:String,name:String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                MarkEntity(
                    id = 0,
                    date = date,
                    no = no,
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

    fun SelectDate(date: String): LiveData<List<MarkEntity>> {
        return dao.findDate(date)
    }

    var datacount = dao.count()

    // var GroupDate = dao.GroupDate()
    var GroupDateMark = dao.GroupDateMark()
    fun del(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.del(id)
        }
    }

    fun mon_select(mon: String): LiveData<List<MarkEntity>> {
        return dao.MonSelect(mon)
    }
    fun mon_select_count(date: String): LiveData<List<MarkGroup>> {
        return dao.MonSelectCount(date)
    }

    fun datecount(date: String): LiveData<Int> {
        return dao.datecount(date)
    }
}