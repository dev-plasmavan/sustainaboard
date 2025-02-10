package jp.shsit.sustinaboard.ui.actions.sdgsmarks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentSdgsmarksResultBinding
import jp.shsit.sustinaboard.room.mark.MarkViewModel
import jp.shsit.sustinaboard.ui.actions.ActionsBase
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class SdgsmarksResult : Fragment() {
    private var _binding: FragmentSdgsmarksResultBinding? = null
    private lateinit var itemArray: ArrayList<CsvItem>
    private val binding get() = _binding!!
    private var markList: ArrayList<String> = ArrayList<String>()


    @SuppressLint("DiscouragedApi")
    @Suppress("NAME_SHADOWING")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSdgsmarksResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        csvReader()

        val args = arguments?.getInt("key1", 0)
        Log.i("test", args.toString())
        val no = args?.let { itemArray[it].no }
        val name = args?.let { itemArray[it].name }
        val image = args?.let { itemArray[it].mark }
        val disc = args?.let { itemArray[it].dis }

        binding.textView.text = name
        binding.textView2.text = disc
        val reID = resources.getIdentifier(image, "drawable", context?.packageName)
        binding.imageView.setImageResource(reID)


        val imageView1 = binding.imageView1
        val imageView2 = binding.imageView2
        val imageView3 = binding.imageView3
        val imageView4 = binding.imageView4
        val imageView5 = binding.imageView5
        val imageView6 = binding.imageView6
        val imageView7 = binding.imageView7
        val imageView8 = binding.imageView8
        val imageView9 = binding.imageView9
        val imageView10 = binding.imageView10
        val imageView11 = binding.imageView11
        val imageView12 = binding.imageView12

        println("mark")
        val no1: Int? = args
        println(markList[no1!!])
        val markItem = markList[no1]
        val imagList = markItem.split("/").toTypedArray()

        var i = 1
        for (item in imagList) {
            val imageName = "sdgs$item"
            val reID = resources.getIdentifier(imageName, "drawable", context?.packageName)

            when (i) {
                1 -> {
                    imageView1.setImageResource(reID)
                }
                2 -> {
                    imageView2.setImageResource(reID)
                }
                3 -> {
                    imageView3.setImageResource(reID)
                }
                4 -> {
                    imageView4.setImageResource(reID)
                }
                5 -> {
                    imageView5.setImageResource(reID)
                }
                6 -> {
                    imageView6.setImageResource(reID)
                }
                7 -> {
                    imageView7.setImageResource(reID)
                }
                8 -> {
                    imageView8.setImageResource(reID)
                }
                9 -> {
                    imageView9.setImageResource(reID)
                }
                10 -> {
                    imageView10.setImageResource(reID)
                }
                11 -> {
                    imageView11.setImageResource(reID)
                }
                12 -> {
                    imageView12.setImageResource(reID)
                }
            }
            i += 1
        }

        binding.button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1, ActionsBase())
            transaction?.commit()
        }

        //初期値　現在の日時
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val monthOfYear: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val weekInt = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val monthOfYearResult = monthOfYear + 1
        val day = "$year 年 $monthOfYearResult 月 $dayOfMonth 日" + weekName[weekInt]


        //room用
        val viewModel: MarkViewModel by viewModels()

        binding.entryBtn.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("環境ラベルの保存")
                .setMessage("この環境ラベルを保存してもよろしいですか？")
                    /*
                .setNeutralButton("マークリストへ") { _, _ ->
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.addToBackStack(null)
                    transaction?.replace(R.id.fragment1, SdgsmarksList())
                    transaction?.commit()
                }
                     */
                .setPositiveButton("はい") { _, _ ->
                    try {
                        if (no != null) {
                            if (name != null) {
                                viewModel.insert(
                                    date = day,
                                    no = no,
                                    name = name
                                )
                            }
                        }
                    } catch (_: Exception) {
                        Toast.makeText(context, "エラー：登録できませんでした", Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(context, "登録されました", Toast.LENGTH_LONG).show()

                    activity?.supportFragmentManager?.popBackStack("actions", 0)
                }
                .setNegativeButton("いいえ") { _, _ ->
                    /*
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.addToBackStack(null)
                    transaction?.replace(R.id.fragment1, SdgsmarksRead())
                    transaction?.commit()
                    */
                }.show()
        }

        return root
    }

    private fun csvReader() {
        itemArray = ArrayList<CsvItem>()
        var item: CsvItem
        val inputStream = resources.assets.open("SDGsMarks.csv")
        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        val bufferedReader = BufferedReader(inputStreamReader)
        bufferedReader.forEachLine {
            if (it.isNotBlank()) {
                item = CsvItem()
                val line = it.split(",").toTypedArray()
                val v0 = line[0]
                val v1 = line[1]
                val v2 = line[2]
                val v3 = line[3]
                val v4 = line[4]
                item.no = v0
                item.name = v1
                item.dis = v2
                item.mark = v3
                itemArray.add(item)
                markList.add(v4)
            }
        }
    }

    private var weekName = arrayOf(
        "（日）", "（月）", "（火）", "（水）",
        "（木）", "（金）", "（土）"
    )
}