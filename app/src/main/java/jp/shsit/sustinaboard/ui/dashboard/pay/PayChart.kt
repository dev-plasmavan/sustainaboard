@file:Suppress("NAME_SHADOWING", "DEPRECATION")
package jp.shsit.sustinaboard.ui.dashboard.pay

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentChartPayBinding
import jp.shsit.sustinaboard.room.pay.PayEntity
import jp.shsit.sustinaboard.room.pay.PayViewModel
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

class PayChart : Fragment() {

    private var _binding: FragmentChartPayBinding? = null
    // private val binding get() = _binding!!
    private var itemList: ArrayList<PayEntity> = ArrayList()
    private val viewModel: PayViewModel by viewModels()
    private var textSum: TextView? = null


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentChartPayBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root
        textSum = _binding!!.textSum

        val monText = root.findViewById<TextView>(R.id.textView2)

        val calendar: Calendar = arguments?.getSerializable("chart1") as Calendar
        val year = calendar[Calendar.YEAR].toString()
        val month = calendar.get(Calendar.MONTH) + 1
        Log.i("test", month.toString())

        monText.text = month.toString() + "月"

        chart(year, month)

        val nextBtn = root.findViewById<Button>(R.id.nextBtn)
        nextBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            val year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            monText.text = month.toString() + "月"

            chart(year, month)
        }

        val returnBtn = root.findViewById<Button>(R.id.returnBtn)
        returnBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            val year = calendar[Calendar.YEAR].toString()
            val month = calendar[Calendar.MONTH] + 1
            monText.text = month.toString() + "月"

            chart(year, month)
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("DashboardPay", 0)
        }

        return root
    }

    private fun log(message: String = "") {
        println("[%s] %s".format(Thread.currentThread().name, message))

    }

    @SuppressLint("SetTextI18n")
    fun chart(year: String, mon: Int) {
        val month: String = if (mon < 10) {
            "0$mon"
        } else {
            mon.toString()
        }

        val date = "$year 年 $month 月"
        println(date)
        var list: List<PayEntity>
        itemList = ArrayList()

        MainScope().launch {
            async {
                log("ASYNC - A - START")

                viewModel.monSelect(date).observe(requireActivity()) { item ->
                    list = item
                    for (item in list) {
                        val a: PayEntity = item
                        itemList.add(a)
                        Log.i("test", item.date)
                    }
                }
                delay(500)
                

                val menuList =
                    listOf("食料費", "生活用品", "趣味", "交通費", "通信費", "光熱費", "衣類", "娯楽","税", "その他", "未分類")
                val priceList: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                var i = 0

                for (list in menuList) {
                    println(list + "," + menuList[i] + "," + priceList[i])
                    for (item in itemList) {
                        println("通過1" + item.kind + "," + list)
                        if (item.kind == list) {
                            println("通過2")
                            priceList[i] += item.price
                        }
                    }
                    i++
                }


                val sum = itemList.sumOf { it.price }
                textSum!!.text = sum.toString() + "円"
                for (i in priceList) {
                    Log.i("test", i.toString())
                }

                val dimensions: ArrayList<String> = ArrayList()
                val values: ArrayList<Float> = ArrayList()

                i = 0
                for (item in priceList) {
                    if (item != 0) {
                        dimensions.add(menuList[i])
                        values.add(priceList[i].toFloat() / sum.toFloat())
                    }
                    i++
                }

                val entryList = mutableListOf<PieEntry>()
                for (i in values.indices) {
                    entryList.add(
                        PieEntry(values[i], dimensions[i])
                    )
                    Log.i("test", values[i].toString())
                    Log.i("test", dimensions[i])
                }

                try {
                    val pieDataSet = PieDataSet(entryList, "candle")

                    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                    pieDataSet.setDrawValues(false)

                    val pieData = PieData(pieDataSet)

                    val pieChart = _binding!!.pieChartPay
                    pieChart.data = pieData

                    pieChart.legend.isEnabled = false

                    pieChart.description.text = ""

                    pieChart.description.textSize = 12f

                    pieChart.invalidate()

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
                    mv.chartView = pieChart
                    _binding!!.pieChartPay.marker = mv

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