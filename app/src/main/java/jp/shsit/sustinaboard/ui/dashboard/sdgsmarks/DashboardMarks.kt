@file:Suppress("NAME_SHADOWING")

package jp.shsit.sustinaboard.ui.dashboard.sdgsmarks

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentDashboardMarkBinding
import jp.shsit.sustinaboard.room.mark.MarkEntity
import jp.shsit.sustinaboard.room.mark.MarkViewModel
import java.util.*

class DashboardMarks : Fragment() {

    private var _binding: FragmentDashboardMarkBinding? = null
    private val binding get() = _binding!!
    lateinit var list: List<MarkEntity>
    private val viewModel: MarkViewModel by viewModels()
    private var textDay:TextView? = null


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardMarkBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listview = root.findViewById<ListView>(R.id.list)

        textDay = _binding!!.textView

        val calendar: Calendar = Calendar.getInstance()
        var year = calendar[Calendar.YEAR].toString()
        val month = calendar[Calendar.MONTH] + 1
        selectMon(year,month,listview)

        textDay!!.text = month.toString()+ "月"

        binding.nextBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            textDay!!.text = month.toString() + "月"

            selectMon(year,month,listview)

        }
        binding.returnBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            textDay!!.text = month.toString() + "月"

            selectMon(year,month,listview)
        }

        binding.btnchart.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val sub = MarksChart()

            val bundle = Bundle()
            bundle.putSerializable("chart",calendar)
            sub.arguments =bundle

            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, sub)
            transaction?.commit()
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("dashboard",0)
        }

        viewModel.GroupDateMark.observe(viewLifecycleOwner) { items ->
            items.forEach {
                println(it.name + "," + it.count + "," + it.date)
            }
        }

        listview.setOnItemClickListener { _, _, _, _ ->
            //    Log.i("test",list[position].id.toString())
        }

        listview.setOnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(requireContext())
                .setTitle("記録の削除")
                .setMessage("選択された項目を削除していいですか？")
                .setPositiveButton("はい") { _, _ ->

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

        return root
    }

    private fun selectMon(year:String, mon:Int, listview:ListView){
        val month:String = mon.toString()

        val date = "$year 年 $month 月"
        println(date)

        viewModel.mon_select(date).observe(requireActivity()) { item ->
            list = item
            var i = 0
            while(i < list.size) {
                val datestr = list[i].date
                val start = datestr.indexOf("月")
                val day = datestr.substring(start + 1)
                list[i].date = day
                i+=1
            }
            val itemAdapter = MarksAdapter(requireContext(), list)
            listview.adapter = itemAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}