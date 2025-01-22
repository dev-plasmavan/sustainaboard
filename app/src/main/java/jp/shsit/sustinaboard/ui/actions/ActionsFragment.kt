package jp.shsit.sustinaboard.ui.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.ui.actions.income.ActionsIncome
import jp.shsit.sustinaboard.ui.actions.sdgsmarks.SdgsmarksRead
import com.google.android.material.tabs.TabLayout
import jp.shsit.sustinaboard.databinding.FragmentActionsBinding
import jp.shsit.sustinaboard.databinding.FragmentSdgsmarksListBinding
import jp.shsit.sustinaboard.ui.actions.date.ActionsDate
import jp.shsit.sustinaboard.ui.actions.pay.ActionsPayManual
import jp.shsit.sustinaboard.ui.actions.pay.ocr.OcrFragment

@Suppress("NAME_SHADOWING")
class ActionsFragment : Fragment() {

    private var _binding : FragmentActionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionsBinding.inflate(inflater, container, false)

        val transaction = activity?.supportFragmentManager?.beginTransaction()

        binding.button7.setOnClickListener {
            transaction?.addToBackStack("ocr_reader")
            transaction?.replace(R.id.fragment1, OcrFragment())
            transaction?.commit()
        }

        binding.button8.setOnClickListener {
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, ActionsPayManual())
            transaction?.commit()
        }

        binding.button9.setOnClickListener {
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, ActionsIncome())
            transaction?.commit()
        }

        binding.button10.setOnClickListener {
            transaction?.addToBackStack("mark_read")
            transaction?.replace(R.id.fragment1, SdgsmarksRead())
            transaction?.commit()
        }

        binding.button11.setOnClickListener {
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, ActionsDate())
            transaction?.commit()
        }


        return binding.root
    }
}