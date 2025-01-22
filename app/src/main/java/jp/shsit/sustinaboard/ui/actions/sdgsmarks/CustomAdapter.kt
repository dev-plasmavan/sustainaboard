package jp.shsit.sustinaboard.ui.actions.sdgsmarks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.shsit.sustinaboard.R
import java.util.ArrayList


class CustomAdapter(context: Context, private var mList: ArrayList<ListItem>) : ArrayAdapter<ListItem>(context, 0, mList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list_sdgsmarks, parent, false)
        }
        val item = mList[position]

        val imageView = view?.findViewById<ImageView>(R.id.image)
        imageView?.setImageResource(item.listID)

        val name = view?.findViewById<TextView>(R.id.name)
        name?.text = item.listName

        val no = view?.findViewById<TextView>(R.id.no)
        no?.text = item.listNo


        return view!!
    }
}