@file:Suppress("NAME_SHADOWING", "DEPRECATION")
package jp.shsit.sustinaboard.ui.dashboard.sdgsmarks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentChartSdgsmarksBinding
import jp.shsit.sustinaboard.room.mark.MarkViewModel
import jp.shsit.sustinaboard.ui.actions.sdgsmarks.MarkGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class MarksChart : Fragment() {

    private var _binding: FragmentChartSdgsmarksBinding? = null
    // private val binding get() = _binding!!
    var itemList :ArrayList<MarkGroup> = ArrayList()
    private val viewModel: MarkViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding = FragmentChartSdgsmarksBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root
        val monText = root.findViewById<TextView>(R.id.textView2)

        val calendar: Calendar = arguments?.getSerializable("chart") as Calendar
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
            activity?.supportFragmentManager?.popBackStack("DashboardMark",0)
        }

        return root
    }

    private fun log(message: String = "") {
        println("[%s] %s".format(Thread.currentThread().name, message))

    }

    private fun chart(year:String, mon:Int){
        val month:String = mon.toString()

        val date = "$year 年 $month 月"
        println(date)
        var list:List<MarkGroup>
        itemList  = ArrayList()


        MainScope().launch {
            async {
                log("ASYNC - A - START")

                viewModel.mon_select_count(date).observe(requireActivity()) { item ->
                    list = item
                    for (item in list) {
                        val a: MarkGroup = item
                        itemList.add(a)
                        Log.i("test", item.name + "," + item.count.toString())
                    }
                }

                delay(500)
                Log.i("test",itemList.size.toString())

                try {
                    val barChart = _binding!!.barChartSdgsmarks

                    barChart.data = BarData(getBarData())

                    barChart.axisLeft.apply {
                        axisMinimum = 0f
                        axisMaximum = 30f
                        labelCount = 5
                        setDrawTopYLabelEntry(true)

                        valueFormatter = IntegerValueFormatter()
                    }

                    barChart.axisRight.apply {
                        setDrawLabels(false)
                        setDrawGridLines(false)
                        setDrawZeroLine(false)
                        setDrawTopYLabelEntry(true)
                    }

                    val labels: ArrayList<String> = ArrayList()

                    labels.add("")
                    for (i in 0 until itemList.size) {
                        labels.add(itemList[i].name)
                    }
                    barChart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        labelCount = 3
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawLabels(true)
                        textSize = 10f
                        setDrawGridLines(false)
                        setDrawAxisLine(true)
                    }

                    barChart.apply {
                        setDrawValueAboveBar(true)
                        description.isEnabled = false
                        isClickable = false
                        legend.isEnabled = false //凡例
                        setScaleEnabled(false)
                        animateY(1200, Easing.EaseInCubic)
                    }

                    val mv = object : MarkerView(requireContext(), R.layout.sdgsmarks_marker_view) {
                        @SuppressLint("DiscouragedApi")
                        override fun refreshContent(e: Entry?, highlight: Highlight?) {
                            val textView: TextView = findViewById(R.id.markerText)
                            e!!.y
                            val value2 = e.x
                            val value3 = itemList[value2.toInt() - 1].name
                            textView.text = value3

                            val imageView: ImageView = findViewById(R.id.markerImg)
                            val value4 = itemList[value2.toInt() - 1].no
                            Log.i("test", "mark$value4")
                            val listID = resources.getIdentifier(
                                "mark$value4",
                                "drawable",
                                context?.packageName
                            )
                            imageView.setImageResource(listID)

                            super.refreshContent(e, highlight)
                        }

                        override fun getOffset(): MPPointF {
                            return MPPointF(-width / 2f, -height - 50f)
                        }
                    }
                    mv.chartView = barChart
                    _binding!!.barChartSdgsmarks.marker = mv

                    barChart.invalidate()

                } catch (e: NullPointerException) {
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

    private fun getBarData(): ArrayList<IBarDataSet> {

        val entries = ArrayList<BarEntry>().apply {
            for(i in 0 until itemList.size) {
                val x:Float = (i+1).toFloat()
                val y:Float = itemList[i].count.toFloat()
                add(BarEntry(x, y))
            }
        }

        val dataSet = BarDataSet(entries, "bar").apply {

            valueTextSize = 20f
            valueFormatter = IntegerValueFormatter()
            setColors(intArrayOf(R.color.m_blue, R.color.m_green, R.color.m_yellow,R.color.m_amber,R.color.m_bluegray, R.color.m_orange,R.color.m_indigo,R.color.m_lightgreen,R.color.m_lime,R.color.m_pink), requireContext())
        }

        val bars = ArrayList<IBarDataSet>()
        bars.add(dataSet)
        return bars
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class IntegerValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }
}

