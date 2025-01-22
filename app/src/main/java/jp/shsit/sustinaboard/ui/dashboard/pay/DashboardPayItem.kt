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
import jp.shsit.sustinaboard.databinding.FragmentDashboardPayItemBinding
import jp.shsit.sustinaboard.room.pay.PayEntity
import jp.shsit.sustinaboard.room.pay.PayViewModel

class DashboardPayItem : Fragment() {

    private var _binding: FragmentDashboardPayItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PayViewModel by viewModels()
    lateinit var list: ArrayList<PayEntity>
    private var textDay: TextView? = null
    private var textSum: TextView? = null
    private var textStore: TextView? = null
    private var sumPrice:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardPayItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listview = root.findViewById<ListView>(R.id.list)

        textDay = _binding!!.textDay
        textSum = _binding!!.textSum
        textStore = _binding!!.textStore

        val no: Int? = arguments?.getInt("group")
        Log.i("test", ",$no")

        viewModel.groupSelect(no!!).observe(requireActivity()) { item ->
            list = item as ArrayList<PayEntity>
            try {
                textDay!!.text = list[0].date
                textStore!!.text = list[0].company
            }catch (_:IndexOutOfBoundsException){

            }
            sumprice()
            textSum!!.text = sumPrice.toString()
            for(i in list){
                Log.i("test",i.word.toString()+","+no)
                sumprice()
            }

            val itemAdapter = PayItemAdapter(requireContext(), list)
            listview.adapter = itemAdapter
        }

        listview.setOnItemLongClickListener { _, _, position, _ ->

            AlertDialog.Builder(requireContext())
                .setTitle("記録の削除")
                .setMessage("選択された項目を削除していいですか？")
                .setPositiveButton("はい") { _, _ ->



                    viewModel.del(list[position].id)
                    list.removeAt(position)
                    //  Log.i("test", list[position].id.toString())
                    Toast.makeText(context, "削除されました", Toast.LENGTH_LONG).show()

                    sumprice()
                    textSum!!.text = sumPrice.toString()
                    //データベース更新
                    for (item in list) {
                        viewModel.update(item.id,
                            item.word.toString(),item.price,item.date,item.kind,item.company,
                            item.sum,item.group)
                    }
                }
                .setNegativeButton("いいえ") { _, _ ->
                    // TODO: When tap "no"
                }
                .show()

            return@setOnItemLongClickListener true
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("DashboardPay",0)
        }

        return root
    }

    private fun sumprice(){
        sumPrice = 0
        for(item in list){
            val price = item.price
            sumPrice += price
        }
        //合計を書き直す
        for(item in list){
            item.sum = sumPrice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}