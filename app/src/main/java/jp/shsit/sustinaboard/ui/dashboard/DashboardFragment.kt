package jp.shsit.sustinaboard.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.ui.actions.income.ActionsIncome
import jp.shsit.sustinaboard.ui.actions.sdgsmarks.SdgsmarksRead
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.shsit.sustinaboard.databinding.FragmentActionsBinding
import jp.shsit.sustinaboard.databinding.FragmentDashboardBinding
import jp.shsit.sustinaboard.databinding.FragmentSdgsmarksListBinding
import jp.shsit.sustinaboard.ui.actions.pay.ActionsPayManual
import jp.shsit.sustinaboard.ui.actions.pay.ocr.OcrFragment
import jp.shsit.sustinaboard.ui.dashboard.date.DashboardDate
import jp.shsit.sustinaboard.ui.dashboard.income.DashboardIncome
import jp.shsit.sustinaboard.ui.dashboard.pay.DashboardPay
import jp.shsit.sustinaboard.ui.dashboard.sdgsmarks.DashboardMarks

class DashboardFragment : Fragment() {

    private var _binding : FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.paylist.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack("DashboardPay")
            transaction?.replace(R.id.fragment1, DashboardPay())
            transaction?.commit()
        }

        binding.incomelist.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack("DashboardIncome")
            transaction?.replace(R.id.fragment1, DashboardIncome())
            transaction?.commit()
        }

        binding.marklist.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack("DashboardMark")
            transaction?.replace(R.id.fragment1, DashboardMarks())
            transaction?.commit()
        }

        binding.datelist.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack("DashboardDay")
            transaction?.replace(R.id.fragment1, DashboardDate())
            transaction?.commit()
        }

        return binding.root
    }
}