package jp.shsit.sustinaboard.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.shsit.sustinaboard.R

class DashboardBase : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_base, container, false)

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.addToBackStack("dashboard")
        transaction?.replace(R.id.fragment1, DashboardFragment())
        transaction?.commit()

        return view
    }
}