@file:Suppress("NAME_SHADOWING", "DEPRECATION")
package jp.shsit.sustinaboard.ui.dashboard.income

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentChartIncomeBinding
import jp.shsit.sustinaboard.room.income.IncomeEntity
import jp.shsit.sustinaboard.room.income.IncomeViewModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class IncomeChart : Fragment() {

    private var _binding: FragmentChartIncomeBinding? = null
    // private var binding get() = _binding!!
    private var itemList : ArrayList<IncomeEntity> = ArrayList()
    private val viewModel: IncomeViewModel by viewModels()
    private var textSum: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartIncomeBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        textSum = _binding!!.textSum
        // pieChartIncome = binding.pieChartIncome

        val monText = root.findViewById<TextView>(R.id.textView2)

        val calendar: Calendar = arguments?.getSerializable("chart1") as Calendar
        val year = calendar[Calendar.YEAR].toString()
        val month = calendar.get(Calendar.MONTH) + 1
        Log.i("test",month.toString())

        monText.text = month.toString() + "月"

        chart(year, month)

        val nextBtn = root.findViewById<Button>(R.id.nextBtn)
        nextBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            val year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            monText.text = month.toString() + "月"

            chart(year,month)
        }

        val returnBtn = root.findViewById<Button>(R.id.returnBtn)
        returnBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            val year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            monText.text = month.toString() + "月"

            chart(year,month)
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("DashboardIncome",0)
        }

        return root
    }
    private fun log(message: String = "") {
        println("[%s] %s".format(Thread.currentThread().name, message))

    }

    @SuppressLint("SetTextI18n")
    fun chart(year:String, mon:Int){
        val month:String = if(mon<10){
            "0$mon"
        }else{
            mon.toString()
        }

        val date = "$year 年 $month 月"
        println(date)
        var list: List<IncomeEntity>
        itemList = ArrayList()

        MainScope().launch {
            async {
                log("ASYNC - A - START")

                viewModel.monSelect(date).observe(requireActivity()) { item ->
                    list = item
                    for (item in list) {
                        val a: IncomeEntity = item
                        itemList.add(a)
                        Log.i("test", item.date)
                    }
                }
                delay(500)

                val menuList = listOf("給料", "臨時収入", "お小遣い", "その他", "未分類")
                val priceList: Array<Int> = arrayOf(0,0,0,0,0)
                var i = 0

                for(list in menuList){
                    println(list +","+menuList[i]+","+priceList[i])
                    for(item in itemList) {
                        println("通過1"+item.kind+ ","+list)
                        if (item.kind == list) {
                            println("通過2")
                            priceList[i] += item.price
                        }
                    }
                    i++
                }

                val sum = itemList.sumOf { it.price }
                textSum?.text = sum.toString()+"円"
                for(i in  priceList) {
                    Log.i("test", i.toString())
                }

                val dimensions: ArrayList<String> = ArrayList()
                val values: ArrayList<Float> = ArrayList()

                i=0
                for(item in priceList){
                    if(item!=0){
                        dimensions.add(menuList[i])
                        values.add(priceList[i].toFloat()/sum.toFloat())
                    }
                    i++
                }

                val entryList = mutableListOf<PieEntry>()
                for(i in values.indices){
                    entryList.add(
                        PieEntry(values[i], dimensions[i])
                    )
                    Log.i("test", values[i].toString())
                    Log.i("test", dimensions[i])
                }

                try {
                    val incomeDataSet = PieDataSet(entryList, "candle")

                    incomeDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                    val incomeData = PieData(incomeDataSet)

                    incomeDataSet.setDrawValues(false)

                    val incomeChart = _binding!!.pieChartIncome
                    incomeChart.data = incomeData

                    incomeChart.legend.isEnabled = false

                    incomeChart.description.text = ""

                    incomeChart.description.textSize = 12f

                    incomeChart.invalidate()

                    val mv = object : MarkerView(requireContext(), R.layout.pay_marker_view) {
                        @SuppressLint("SetTextI18n")
                        override fun refreshContent(e: Entry?, highlight: Highlight?) {
                            val textView: TextView = findViewById(R.id.marker_text)
                            val value1 = e!!.y
                            val value2: Float = value1 * sum
                            val value3: Int = value2.toInt()
                            textView.text = "${value3}円"
                            Log.i("test", sum.toString() + "," + e.y.toString())
                            super.refreshContent(e, highlight)
                        }

                        override fun getOffset(): MPPointF {
                            return MPPointF(-width / 2f, -height - 20f)
                        }
                    }
                    mv.chartView = incomeChart
                    _binding!!.pieChartIncome.marker = mv

                } catch (_: NullPointerException) {
                    //TODO
                }
            }

            async {
                log("ASYNC - B - START")
                delay(1000)
                log("ASYNC - B - END")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}