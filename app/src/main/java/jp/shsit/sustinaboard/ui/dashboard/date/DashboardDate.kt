package jp.shsit.sustinaboard.ui.dashboard.date

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentDashboardDateBinding
import jp.shsit.sustinaboard.room.date.DateEntity
import jp.shsit.sustinaboard.room.date.DateViewModel
import jp.shsit.sustinaboard.ui.actions.date.ActionsDateAdapter

class DashboardDate : Fragment() {

    private var _binding: FragmentDashboardDateBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private var textDate: TextView? = null
    private lateinit var list: List<DateEntity>
    val viewModel: DateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardDateBinding.inflate(inflater, container, false)

        val listview = _binding!!.list
        var foodContent: String = ""

        viewModel.items.observe(requireActivity()) { item ->
            list = item

            list.forEach {
                foodContent += "\n${it.name}"
            }

            val itemAdapter = ActionsDateAdapter(requireContext(), list)
            listview.adapter = itemAdapter
        }

        val backBtn = _binding!!.backBtn
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("dashboard",0)
        }

        val recommendBtn = _binding!!.recommendBtn
        recommendBtn.setOnClickListener {
            val sub = RecommendMeal()
            val bundle = Bundle()

            bundle.putString("foodContent", foodContent)
            sub.arguments = bundle

            val fragmentManager = activity?.supportFragmentManager?.beginTransaction()
            fragmentManager?.addToBackStack(null)
            fragmentManager?.replace(R.id.fragment1, sub)
            fragmentManager?.commit()
        }

        // Delete selected ID from room
        listview.setOnItemLongClickListener { _, _, position, _ ->

            AlertDialog.Builder(requireContext())
                .setTitle("記録の削除")
                .setMessage("選択された項目を削除していいですか？")
                .setPositiveButton("はい") { _, _ ->
                    // TODO: When tap "yes"
                    viewModel.del(list[position].id)
                    Log.i("test", list[position].id.toString())
                    Toast.makeText(context, "削除されました", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // TODO: When tap "no"
                }
                .show()

            return@setOnItemLongClickListener true
        }

        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}