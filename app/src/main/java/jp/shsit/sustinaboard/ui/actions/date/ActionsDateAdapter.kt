package jp.shsit.sustinaboard.ui.actions.date

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.room.date.DateEntity
import java.text.DateFormat
import java.text.ParseException
import java.util.*

class ActionsDateAdapter(context: Context, private var mItemList: List<DateEntity>) : ArrayAdapter<DateEntity>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_date, parent, false)
        }

        val item = mItemList[position]

        val date = view?.findViewById<TextView>(R.id.item_date)
        date?.text = item.date

        val name = view?.findViewById<TextView>(R.id.item_name)
        name?.text = item.name

        val limit = view?.findViewById<TextView>(R.id.item_limit)
        limit?.text = ""

        val cal = parseStrToCal(item.date)
        cal!!.add(Calendar.DATE, 1)

        val currentCal = Calendar.getInstance()

        val diff = currentCal.compareTo(cal)

        if (diff == 0) {
            println("現在")
        } else if (diff > 0) {
            println("過去")
            limit?.text = "期限超え"
            limit?.setTextColor(Color.RED)
        } else {
            println("未来")
            limit?.text = "期限内"
            limit?.setTextColor(Color.GREEN)
        }

        return view!!
    }
    private fun parseStrToCal(str: String?): Calendar? {
        var cal: Calendar? = GregorianCalendar()
        if (str == null) {
            cal = null
        } else {
            try {
                val str1 = str.substring(0, str.indexOf("日"))
                val str2 = str1.replace("年", "/")
                val str3 = str2.replace("月", "/")
                val str4 = str3.replace(" ", "")
                println(str4)
                cal?.time = DateFormat.getDateInstance().parse(str4)!!

                println(cal)
            } catch (_: ParseException) {
            }
        }
        return cal
    }
}