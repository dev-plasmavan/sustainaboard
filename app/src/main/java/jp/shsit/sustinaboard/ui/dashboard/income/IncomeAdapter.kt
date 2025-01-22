package jp.shsit.sustinaboard.ui.dashboard.income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.room.income.IncomeEntity

class IncomeAdapter(context: Context, private var mItemList: List<IncomeEntity>) : ArrayAdapter<IncomeEntity>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_income, parent, false)
        }
        val item = mItemList[position]

        val kind = view?.findViewById<TextView>(R.id.item_kind)
        kind?.text = item.kind
        val price = view?.findViewById<TextView>(R.id.item_price)
        price?.text = item.price.toString()
        val date = view?.findViewById<TextView>(R.id.item_date)
        date?.text = item.date

        return view!!
    }
}