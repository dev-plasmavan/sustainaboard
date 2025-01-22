package jp.shsit.sustinaboard.ui.actions.sdgsmarks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.shsit.sustinaboard.R

class SdgsmarksBase : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_base, container, false)

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.addToBackStack(null)
        transaction?.replace(R.id.fragment1, SdgsmarksList())
        transaction?.commit()

        return view
    }
}