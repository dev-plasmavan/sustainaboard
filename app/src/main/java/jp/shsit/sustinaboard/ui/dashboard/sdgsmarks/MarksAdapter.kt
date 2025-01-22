package jp.shsit.sustinaboard.ui.dashboard.sdgsmarks

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.room.mark.MarkEntity

class MarksAdapter(context: Context, private var mItemList: List<MarkEntity>) : ArrayAdapter<MarkEntity>(context, 0, mItemList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("DiscouragedApi")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_sdgsmarks2, parent, false)
        }

        val item = mItemList[position]

        val imageView = view?.findViewById<ImageView>(R.id.image)

        val img: String = "mark"+item.no

        val reID =  view?.resources?.getIdentifier(img,"drawable", context.packageName)
        reID?.let { imageView?.setImageResource(it) }

        val name = view?.findViewById<TextView>(R.id.name)
        name?.text = item.name

        val day = view?.findViewById<TextView>(R.id.date)
        day?.text = item.date

        return view!!
    }
}