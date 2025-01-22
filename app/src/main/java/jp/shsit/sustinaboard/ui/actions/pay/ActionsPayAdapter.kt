package jp.shsit.sustinaboard.ui.actions.pay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jp.shsit.sustinaboard.R

class ActionsPayAdapter(context: Context, private var mItemList: ArrayList<ActionsPayManual.PayItem>) : ArrayAdapter<ActionsPayManual.PayItem>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_ocr_result, parent, false)
        }
        val item = mItemList[position]

        val name = view?.findViewById<TextView>(R.id.item_name)
        val price = view?.findViewById<TextView>(R.id.item_price)
        val day = view?.findViewById<TextView>(R.id.item_date)
        val kind = view?.findViewById<TextView>(R.id.item_kind)

        name?.text = item.name
        price?.text = item.price
        day?.text = item.date
        kind?.text = item.kind

        return view!!
    }
}
