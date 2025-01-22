package jp.shsit.sustinaboard.ui.actions.pay.ocr

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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentOcrResultBinding
import jp.shsit.sustinaboard.room.pay.PayViewModel
import jp.shsit.sustinaboard.ui.actions.ActionsFragment
import jp.shsit.sustinaboard.ui.actions.pay.ActionsPayManual.Companion.KEY_RESULT_TEXT3
import org.w3c.dom.Text
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NAME_SHADOWING")
class OcrResultFragment : Fragment() {

    private var _binding: FragmentOcrResultBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private var textDate: TextView? = null
    private var textStore: TextView? = null
    var list: ArrayList<OcrItem> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcrResultBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        //店名
        textStore = _binding!!.companyName
        textStore.let{
            val store = arguments?.getString(KEY_RESULT_TEXT).toString()
            it?.setText(store)
        }

        //日時
        var date: String
        textDate = _binding!!.textDay
        textDate.let{
            date = arguments?.getString(KEY_RESULT_TEXT2).toString()

            val newDate = dateTrans(date)
            it?.setText(newDate)

        }
        textDate?.setOnClickListener {
            selectDate()
        }

        list = ArrayList<OcrItem>()
        val resultArray = arguments?.getStringArrayList(KEY_RESULT_TEXT3)

        if (resultArray != null) {
            for(i in 0 until resultArray.size step 2){
                if (resultArray[i] != "") {
                    val item = OcrItem()
                    item.name = resultArray[i]

                    val price = resultArray[i+1]
                    var priceNew = ""

                    //価格を取り出し
                    for (i in price) {
                        Log.i("test",i.toString()+"iです")
                        if ((i in ('0'..'9') )|| (i in "-")) {
                            priceNew += i
                            Log.i("test",priceNew+"priceNewです")
                        }
                    }

                    item.price = priceNew
                    list.add(item)
                }
            }
        }

        val listview = _binding!!.list2
        val itemAdapter = OcrAdapter(requireContext(), list )
        listview.adapter = itemAdapter

        val viewModel: PayViewModel by viewModels()
        _binding!!.btnEntry.setOnClickListener {
            val sumPrice = _binding!!.textPrice.text.toString().toInt()
            val company = _binding!!.companyName.text.toString()


            AlertDialog.Builder(requireContext())
                .setTitle("レシート等の支出の記録")
                .setMessage("保存してもいいですか？")
                .setPositiveButton("はい") { _, _ ->
                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    var recipetNo :Int = sharedPref?.getInt("receipt_no",1)!!//何もない場合の初期値

                    try {
                        for(item in list) {
                            if(item.kind == "") {
                                item.kind = "未分類"
                            }

                            val price:Int = item.price.toInt()

                            viewModel.insert(
                                word = item.name,
                                price = price,
                                date = textDate!!.text.toString(),
                                kind = item.kind,
                                company =company,
                                sum = sumPrice,
                                group = recipetNo
                            )
                        }

                    } catch (_: Exception) {
                        Toast.makeText(context, "エラー：保存できませんでした", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    Toast.makeText(context, "保存されました", Toast.LENGTH_LONG).show()
                    Log.i("test",recipetNo.toString()+","+"レシート")
                    recipetNo += 1
                    with(sharedPref.edit()) {
                        putInt("receipt_no", recipetNo)
                        apply()
                    }

                    activity?.supportFragmentManager?.popBackStack("actions", 0)
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // TODO:Noが押された時の挙動
                }.show()
        }
        //項目の編集
        listview.setOnItemClickListener { _, _, position, _ ->
            val transaction = parentFragmentManager.beginTransaction()
            val sub = OcrResultDetailFragment()
            val bundle = Bundle()
            bundle.putString("name",list[position].name)
            bundle.putString("price",list[position].price)
            bundle.putString("kind",list[position].kind)
            bundle.putInt("pos",position)
            sub.arguments = bundle

            transaction.addToBackStack(null)
            transaction.add(R.id.fragment1, sub)
            transaction.commit()
        }




        listview.setOnItemLongClickListener { _, _, position, _ ->
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            builder
                .setTitle("${list[position].name}を削除してもよろしいですか？")
                .setPositiveButton("はい") { dialog, which ->
                    // Do something.

                    try {
                        list.removeAt(position)
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                        //合計金額再計算
                        sumPrice()

                        Toast.makeText(context, "${list[position].name}が削除されました。", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        println("レシートのアイテムを削除できませんでした。理由：$e")
                    }
                }
                .setNegativeButton("いいえ") { dialog, which ->
                    // Do something else.
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()

            return@setOnItemLongClickListener true
        }

        // もどるボタン
        _binding!!.btn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("ocr_reader", 0)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, 0)
        }
        //追加ボタン
        _binding!!.buttonAdd.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.add(R.id.fragment1, AddItemFragment())
            transaction?.commit()
        }

        //分類の一括追加
        _binding!!.buttonKind.setOnClickListener {
            val popup = PopupMenu(
                requireContext(), _binding!!.buttonKind
            )
            popup.menuInflater.inflate(R.menu.context_menu_ocr_result, popup.menu)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.kind1 -> {
                        for(item in list){
                            item.kind = "食料費"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind2 -> {
                        for(item in list){
                            item.kind = "生活用品"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind3 -> {
                        for(item in list){
                            item.kind = "衣類"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind4 -> {
                        for(item in list){
                            item.kind = "交通費"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind5 -> {
                        for(item in list){
                            item.kind = "通信費"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind6 -> {
                        for(item in list){
                            item.kind = "光熱費"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind7 -> {
                        for(item in list){
                            item.kind = "趣味"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind8 -> {
                        for(item in list){
                            item.kind = "娯楽"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind9 -> {
                        for(item in list){
                            item.kind = "税"
                        }
                        //listviewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                    R.id.kind10 -> {
                        for(item in list) {
                            item.kind = "その他"
                        }
                        // listViewの再描画
                        itemAdapter.notifyDataSetChanged()
                    }
                }
                true
            }
        }

        //OcrItemFragmentから返される値の監視
        setFragmentResultListener("result") { _, bundle ->
            val position = bundle.getInt("key4")
            list[position].name = bundle.getString("key1").toString()
            list[position].price = bundle.getString("key2").toString()
            list[position].kind = bundle.getString("key3").toString()
            //listviewの再描画
            itemAdapter.notifyDataSetChanged()
            //合計金額再計算
            sumPrice()
        }
        //AddItemFragmentから返される値の監視
        setFragmentResultListener("resultAdd") { _, bundle ->

            val name = bundle.getString("key1").toString()
            val price = bundle.getString("key2").toString()
            val kind = bundle.getString("key3").toString()
            if(name != "" && price != ""){
                val Item:OcrItem = OcrItem()
                Item.name=name
                Item.price=price
                Item.kind=kind
                list.add(Item)
                //listviewの再描画
                itemAdapter.notifyDataSetChanged()
                //合計金額再計算
                sumPrice()

                Toast.makeText(context, "${name}を追加しました。", Toast.LENGTH_LONG).show()
                Log.i("test","koko1")
            }else{
                Toast.makeText(context, "追加できませんでした。商品名・金額は必須です。", Toast.LENGTH_LONG).show()
                Log.i("test","koko2")
            }

        }

        //合計金額
        sumPrice()

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
    //合計金額を求める
    fun sumPrice(){
        var sum = 0
        for (item in list){
            var price =0
            price = try {
                item.price.toInt()
            }catch(e:Exception) {
                0
            }
            sum += price
        }
        _binding!!.textPrice.text = sum.toString()

    }


    @SuppressLint("SimpleDateFormat")
    private fun dateTrans(created:String):String{

        var year ="2024"
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
}