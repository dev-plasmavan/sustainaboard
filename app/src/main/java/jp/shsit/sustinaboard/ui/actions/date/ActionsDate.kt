package jp.shsit.sustinaboard.ui.actions.date

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
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
import jp.shsit.sustinaboard.databinding.FragmentActionsDateBinding
import jp.shsit.sustinaboard.notification.Receiver
import jp.shsit.sustinaboard.room.date.DateEntity
import jp.shsit.sustinaboard.room.date.DateViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class ActionsDate : Fragment() {

    private var _binding: FragmentActionsDateBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private var textDate: TextView? = null
    private lateinit var list: List<DateEntity>

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionsDateBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        textDate = _binding!!.textDay

        textDate.let{

            val calendar = Calendar.getInstance()
            // calendar.add(Calendar.DATE, 1)
            calendar.time
            val year: Int = calendar.get(Calendar.YEAR)
            val monthOfYear: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val monthOfYearResult = monthOfYear + 1
            val day = "$year 年 $monthOfYearResult 月 $dayOfMonth 日"

            val newDate = dateTrans(day)
            it?.setText(newDate)

        }

        textDate!!.setOnClickListener {
            selectDate()
        }

        val viewModel: DateViewModel by viewModels()
        _binding!!.btnEntry.setOnClickListener {
            val name = _binding!!.editName.text.toString()

            AlertDialog.Builder(requireContext())
                .setTitle("賞味・消費期限の記録")
                .setMessage("保存してもいいですか？")
                .setPositiveButton("はい") { _, _ ->

                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    var id :Int = sharedPref?.getInt("id",1)!!//何もない場合の初期値
                    println(id)
                    val intent = Intent(context, Receiver::class.java)
                    intent.putExtra("id",id)
                    intent.putExtra("mes",name)
                    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    val pendingIntent = PendingIntent.getBroadcast(context, id,intent , PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE)

                    val date = textDate!!.text.toString()
                    var year = "2023"
                    var mon = "1"
                    var day = "1"

                    try {
                        val date1 = date.replace(" ", "")
                        var start = date1.indexOf("年")
                        year = date1.substring(0, start)
                        val end = date1.indexOf("月")
                        mon = date1.substring(start + 1, end)
                        start = date1.indexOf("日")
                        day = date1.substring(end + 1, start)
                    } catch (_: Exception) {

                    }

                    val year1 = year.toInt()
                    val mon1 = mon.toInt() - 1
                    val day1 = day.toInt()
                    val calendar: Calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(year1, mon1, day1, 17, 0)
                    }

                    alarmManager?.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                    //次のidの保存
                    id += 1
                    with(sharedPref.edit()) {
                        putInt("id", id)
                        apply()
                    }

                    try {
                        viewModel.insert(
                            date = textDate!!.text.toString(),
                            name = name
                        )
                    } catch (_: Exception) {
                        Toast.makeText(context, "エラー：保存できませんでした", Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(context, "保存できました", Toast.LENGTH_LONG).show()

                    activity?.supportFragmentManager?.popBackStack("actions", 0)
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // Nothing
                }.show()
        }


        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("actions",0)
        }

        return root
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateTrans(created:String):String {

        val date1 = created.replace(" ", "")
        var year = "2023"
        var mon = "1"
        var day = "1"
        try {
            var start = date1.indexOf("年")
            year = date1.substring(0, start)
            val end = date1.indexOf("月")
            mon = date1.substring(start + 1, end)
            start = date1.indexOf("日")
            day = date1.substring(end + 1, start)
        } catch (_: Exception) {

        }
        val newDay = "$year 年 $mon 月 $day 日 "

        val datePattern = "yyyy 年 MM 月 dd 日"
        val dateFormat = SimpleDateFormat(datePattern)
        var date: Date? = null
        try {
            date = Date(dateFormat.parse(newDay)!!.time)
        } catch (_: ParseException) {

        }
        val calCreated = Calendar.getInstance()
        try {
            calCreated.time = date!!
        } catch (_: NullPointerException) {

        }

        var str = SimpleDateFormat(datePattern).format(calCreated.time)

        val weekInt = calCreated.get(Calendar.DAY_OF_WEEK) - 1
        str +=  weekName[weekInt]

        return str
    }

    @SuppressLint("SetTextI18n")
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val weekInt = cal.get(Calendar.DAY_OF_WEEK) - 1
            val monthOfYear = monthOfYear + 1
            if(monthOfYear < 10) {
                if(dayOfMonth < 10) {
                    textDate?.text = "$year 年 0$monthOfYear 月 0$dayOfMonth 日" + weekName[weekInt]
                }else {
                    textDate?.text = "$year 年 0$monthOfYear 月 $dayOfMonth 日" + weekName[weekInt]
                }
            }else {
                if(dayOfMonth < 10) {
                    textDate?.text = "$year 年 $monthOfYear 月 0$dayOfMonth 日" + weekName[weekInt]
                }else {
                    textDate?.text = "$year 年 $monthOfYear 月 $dayOfMonth 日" + weekName[weekInt]
                }
            }
        }

    private fun selectDate() {

        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val monthOfYear: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, year, monthOfYear, dayOfMonth)

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var weekName = arrayOf(
        "（日）", "（月）", "（火）", "（水）",
        "（木）", "（金）", "（土）"
    )
}