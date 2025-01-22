package jp.shsit.sustinaboard.ui.dashboard.pay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.room.pay.PayEntity

class PayAdapter(context: Context, private var mItemList: List<PayEntity>) : ArrayAdapter<PayEntity>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_pay, parent, false)
        }
        val item = mItemList[position]

        val name = view?.findViewById<TextView>(R.id.item_name)
        name?.text = item.company
        val price = view?.findViewById<TextView>(R.id.item_price)
        price?.text = item.sum.toString()
        val date = view?.findViewById<TextView>(R.id.item_date)
        date?.text = item.date
        // val kind = view?.findViewById<TextView>(R.id.item_kind)
        //  kind?.text = item.kind

        return view!!
    }
}