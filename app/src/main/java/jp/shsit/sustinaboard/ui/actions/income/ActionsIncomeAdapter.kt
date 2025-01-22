package jp.shsit.sustinaboard.ui.actions.income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.shsit.sustinaboard.R

class ActionsIncomeAdapter(context: Context, private var mItemList: ArrayList<ActionsIncome.IncomeItem>) : ArrayAdapter<ActionsIncome.IncomeItem>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_income, parent, false)
        }
        val item = mItemList[position]

        val price = view?.findViewById<TextView>(R.id.item_price)
        val day = view?.findViewById<TextView>(R.id.item_date)

        price?.text = item.price
        day?.text = item.date

        return view!!
    }
}