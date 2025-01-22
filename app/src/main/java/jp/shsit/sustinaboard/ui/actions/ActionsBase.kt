package jp.shsit.sustinaboard.ui.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.R


class ActionsBase : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_base, container, false)

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.addToBackStack("actions")
        transaction?.replace(R.id.fragment1, ActionsFragment())
        transaction?.commit()

        return view
    }
}