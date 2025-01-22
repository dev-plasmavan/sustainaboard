package jp.shsit.sustinaboard.ui.actions.pay

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentActionsPayManualBinding
import jp.shsit.sustinaboard.room.pay.PayEntity
import jp.shsit.sustinaboard.room.pay.PayViewModel
import jp.shsit.sustinaboard.ui.actions.ActionsFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class ActionsPayManual : Fragment() {

    private var _binding: FragmentActionsPayManualBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private var textDate: TextView? = null
    private var textKind: TextView? = null
    lateinit var list: List<PayEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActionsPayManualBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        // var date: String
        textDate = _binding!!.textDay
        textKind =  _binding!!.textkind

        textDate.let{
            // date = arguments?.getString(KEY_RESULT_TEXT2).toString()

            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val monthOfYear: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val monthOfYearResult = monthOfYear + 1
            val day = "$year 年 $monthOfYearResult 月 $dayOfMonth 日"

            val newDate = dateTrans(day)
            it?.setText(newDate)

        }
        textDate?.setOnClickListener {
            selectDate()
        }
         val list: ArrayList<PayItem> = ArrayList()
        val resuletArray = arguments?.getStringArrayList(KEY_RESULT_TEXT3)


        if (resuletArray != null) {

            for(i in 0 until resuletArray.size step 2){
                val item = PayItem()
                item.name = resuletArray[i]

                val price = resuletArray[i+1]
                var priceNew = ""
                for (i in price) {
                    if (i in ('0'..'9')) {
                        priceNew += i
                    }
                }

                item.price = priceNew
                list.add(item)
            }
        }

        ActionsPayAdapter(requireContext(), list )

        textKind!!.setOnClickListener {
            val popup = PopupMenu(
                requireContext(), textKind!!
            )
            popup.menuInflater.inflate(R.menu.context_menu_ocr_result, popup.menu)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.kind1 -> {
                        textKind!!.text = "食料費"
                    }
                    R.id.kind2 -> {
                        textKind!!.text = "生活用品"
                    }
                    R.id.kind3 -> {
                        textKind!!.text = "衣類"
                    }
                    R.id.kind4 -> {
                        textKind!!.text = "交通費"
                    }
                    R.id.kind5 -> {
                        textKind!!.text = "通信費"
                    }
                    R.id.kind6 -> {
                        textKind!!.text = "光熱費"
                    }
                    R.id.kind7 -> {
                        textKind!!.text = "趣味"
                    }
                    R.id.kind8 -> {
                        textKind!!.text = "娯楽"
                    }
                    R.id.kind9 -> {
                        textKind!!.text = "税"
                    }
                    R.id.kind10 -> {
                        textKind!!.text = "その他"
                    }
                }
                true
            }
        }


        val viewModel: PayViewModel by viewModels()
        _binding!!.btnEntry.setOnClickListener {
            val name = _binding!!.editName.text.toString()
            val price = _binding!!.editPrice.text.toString()
            var kind =  textKind!!.text.toString()

            AlertDialog.Builder(requireContext())
                .setTitle("レシート等の支出の記録")
                .setMessage("保存してもいいですか？")
                .setPositiveButton("はい") { _, _ ->
                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    var noRegisteredNo :Int = sharedPref?.getInt("no_registered_no", 1)!!

                    if(kind == "") {
                        kind = "未分類"
                    }

                    try {
                        viewModel.insert(
                            word = name,
                            price = price.toInt(),
                            date = textDate!!.text.toString(),
                            kind = kind,
                            company = "未登録 - $noRegisteredNo",
                            sum = price.toInt(),
                            group = noRegisteredNo
                        )
                    } catch (_: Exception) {
                        Toast.makeText(context, "保存できませんでした。", Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(context, "保存されました。", Toast.LENGTH_LONG).show()

                    noRegisteredNo++
                    with(sharedPref.edit()) {
                        putInt("no_registered_no", noRegisteredNo)
                        apply()
                    }

                    activity?.supportFragmentManager?.popBackStack("actions", 0)
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // TODO:Noが押された時の挙動
                }.show()
        }

        _binding!!.backBtn.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, ActionsFragment())
            transaction?.commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, 0)
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

    companion object {
        const val KEY_RESULT_TEXT = "RESULT_TEXT"
        const val KEY_RESULT_TEXT2 = "RESULT_DAY"
        const val KEY_RESULT_TEXT3 = "RESULT_value"
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateTrans(created:String):String{

        var year ="2023"
        var mon ="1"
        var day ="1"
        try {
            var start = created.indexOf("年")
            year = created.substring(0, start)
            val end = created.indexOf("月")
            mon = created.substring(start + 1, end)
            start = created.indexOf("日")
            day = created.substring(end + 1, start)
        }catch (_: Exception){

        }
        val newDay = "$year 年 $mon 月 $day 日 "
        Log.i("test", "koko2:$newDay")

        val datePattern = "yyyy 年 MM 月 dd 日"
        val dateformat = SimpleDateFormat(datePattern)
        var date: Date? = null
        try {
            date = Date(dateformat.parse(newDay)!!.time)
        } catch (_: ParseException) {

        }
        val calCreated = Calendar.getInstance()
        try {
            calCreated.time = date!!
        }catch (_: NullPointerException) {

        }

        var str = SimpleDateFormat(datePattern).format(calCreated.time)

        val weekInt = calCreated.get(Calendar.DAY_OF_WEEK) - 1
        str +=  weekName[weekInt]
        Log.d("test", str)
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

    class PayItem {
        lateinit var name:String
        lateinit var price:String
        lateinit var date:String
        lateinit var kind:String
    }
}