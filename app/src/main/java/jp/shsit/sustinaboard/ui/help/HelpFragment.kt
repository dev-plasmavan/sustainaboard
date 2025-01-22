package jp.shsit.sustinaboard.ui.help

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.R

@Suppress("NAME_SHADOWING", "DEPRECATION")
class HelpFragment : Fragment() {
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_help, container, false)
        val backBtn = view.findViewById<Button>(R.id.button)
        val helpText = view.findViewById<Button>(R.id.help_text1)
        val helpText1 = view.findViewById<Button>(R.id.help_text2)

        helpText.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, HelpSaveFragment())
            transaction?.commit()
        }

        helpText1.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, HelpDashboardFragment())
            transaction?.commit()
        }

        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("HomeFragment", 0)
        }

        return view
    }
}