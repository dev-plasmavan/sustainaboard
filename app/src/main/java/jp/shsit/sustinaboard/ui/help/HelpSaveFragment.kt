package jp.shsit.sustinaboard.ui.help

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.R
import com.github.barteksc.pdfviewer.PDFView

@Suppress("NAME_SHADOWING", "DEPRECATION")
class HelpSaveFragment : Fragment() {
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_help_save, container, false)
        val backBtn = view.findViewById<Button>(R.id.button)
        val pdfView = view.findViewById<View>(R.id.help_page) as PDFView

        pdfView.fromAsset("help.pdf").load()

        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("HelpSelector", 0)
        }

        return view
    }
}