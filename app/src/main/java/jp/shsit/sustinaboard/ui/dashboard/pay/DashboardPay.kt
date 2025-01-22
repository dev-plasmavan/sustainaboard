package jp.shsit.sustinaboard.ui.dashboard.pay

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
import jp.shsit.sustinaboard.databinding.FragmentDashboardPayBinding
import jp.shsit.sustinaboard.room.pay.PayEntity
import jp.shsit.sustinaboard.room.pay.PayViewModel
import java.util.*

class DashboardPay : Fragment() {

    private var _binding: FragmentDashboardPayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PayViewModel by viewModels()
    lateinit var list: List<PayEntity>
    lateinit var list2: MutableList<PayEntity>
    private var textDay: TextView? = null
    private var textSum: TextView? = null
    var sumPrice:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardPayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listview = root.findViewById<ListView>(R.id.list)

        textDay = _binding!!.textView
        textSum = _binding!!.textSum

        val calendar: Calendar = Calendar.getInstance()
        var year = calendar[Calendar.YEAR].toString()
        var month = calendar[Calendar.MONTH] + 1
        textDay!!.text = month.toString()+ "月"
        selectMon(year,month,listview)

        binding.nextBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            year = calendar[Calendar.YEAR].toString()
            month = calendar[Calendar.MONTH] + 1
            textDay!!.text = month.toString()+ "月"
            selectMon(year,month,listview)
        }

        binding.returnBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            year = calendar[Calendar.YEAR].toString()
            month = calendar[Calendar.MONTH] + 1
            textDay!!.text = month.toString()+ "月"
            selectMon(year,month,listview)
        }

        binding.btnchart.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val sub = PayChart()

            val bundle = Bundle()
            bundle.putSerializable("chart1",calendar)
            sub.arguments = bundle

            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, sub)
            transaction?.commit()
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("dashboard",0)
        }

        listview.setOnItemClickListener { _, _, position, _ ->
            val no = list2[position].group
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val sub = DashboardPayItem()
            val bundle = Bundle()
            //レシートナンバーを渡す
            bundle.putInt("group",no)
            sub.arguments =bundle

            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, sub)
            transaction?.commit()
        }

        listview.setOnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(requireContext())
                .setTitle("記録の削除")
                .setMessage("選択された項目を削除していいですか？")
                .setPositiveButton("はい") { _, _ ->
                    val no = list2[position].group
                    list2.removeAt(position)
                    sumPrice()

                    for (item in list) {
                        if (no == item.group) {
                            viewModel.deleteGroup(item.group)
                        }
                    }
                    Toast.makeText(context, "削除されました", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // TODO
                }
                .show()
            return@setOnItemLongClickListener true
        }

        return root
    }

    @SuppressLint("SetTextI18n")
    fun selectMon(year:String, mon:Int, listview:ListView){
        val month:String = if(mon<10){
            "0$mon"
        }else{
            mon.toString()
        }

        val date = "$year 年 $month 月"
        println(date)

        viewModel.monSelect(date).observe(requireActivity()) { item ->
            list = item
            var i = 0
            while(i < list.size) {
                val datestr = list[i].date
                val start = datestr.indexOf("月")
                val day = datestr.substring(start + 1)
                list[i].date = day
                i+=1
            }
            //項目を全て合計して月の合計を求める。
            val sum: Int = list.sumOf { it.price }
            textSum?.text = sum.toString() + "円"
        }

        viewModel.monSelectGroup(date).observe(requireActivity()) { item ->
            list2 = item.toMutableList()

            var i = 0
            while(i < list2.size) {
                val datestr = list2[i].date
                val start = datestr.indexOf("月")
                val day = datestr.substring(start + 1)
                list2[i].date = day
                i+=1
            }

            list2 = list2.sortedBy { it.date }.reversed().toMutableList()
            val itemAdapter = PayAdapter(requireContext(), list2)
            listview.adapter = itemAdapter

        }
    }

    fun sumPrice(){
        sumPrice = 0
        for(item in list2){
            val price = item.price.toInt()?:0
            sumPrice += price
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}