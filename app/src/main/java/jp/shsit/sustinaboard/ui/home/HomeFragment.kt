package jp.shsit.sustinaboard.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.shsit.sustinaboard.databinding.FragmentHomeBinding
import jp.shsit.sustinaboard.room.income.IncomeEntity
import jp.shsit.sustinaboard.room.income.IncomeViewModel
import jp.shsit.sustinaboard.room.mark.MarkViewModel
import jp.shsit.sustinaboard.room.pay.PayEntity
import jp.shsit.sustinaboard.room.pay.PayViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Suppress("NAME_SHADOWING", "DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarkViewModel by viewModels()
    private val payViewModel: PayViewModel by viewModels()
    private val incomeViewModel: IncomeViewModel by viewModels()
    private lateinit var payList: List<PayEntity>
    private lateinit var incomeList: List<IncomeEntity>
    private var lvText: TextView? = null
    private var lvImg: ImageView? = null
    private var level = 0

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        lvText = binding.levelText
        lvImg = binding.levelImage

        val calendar: Calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR].toString()
        val month = calendar[Calendar.MONTH] + 1
        val day = calendar[Calendar.DAY_OF_MONTH]
        val weekInt = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val date = "$year 年 $month 月 $day 日" + weekName[weekInt]

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val readValue = sharedPref?.getInt("level2", 0)
        level = readValue!!.toInt()

        lvText!!.text = "SDGs達成レベル:$level"

        var lv = level % 30
        if (lv == 0) {
            lv = 30
        }
        val img = "grow$lv"
        val reID = resources.getIdentifier(img, "drawable", context?.packageName)
        lvImg!!.setImageResource(reID)

        val monthInfo: String = if (month < 10) {
            "0$month"
        } else {
            month.toString()
        }

        val dateInfo = "$year 年 $monthInfo 月"

        find(date)

        payViewModel.monSelect(dateInfo).observe(requireActivity()) { item ->
            payList = item
            val pay: Int = payList.sumOf { it.price }
            binding.textPay.text = "今月の支出：" + pay.toString() + "円"
        }

        incomeViewModel.monSelect(dateInfo).observe(requireActivity()) { item ->
            incomeList = item
            val income: Int = incomeList.sumOf { it.price }
            binding.textIncome.text = "今月の収入：" + income.toString() + "円"
        }

        viewModel.datecount(date).observe(requireActivity()) { item ->
            binding.textMarks.text = "今日の環境ラベルの数：" + item.toString() + "個"
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    private fun find(date: String) {
        viewModel.datecount(date).observe(requireActivity()) { item ->
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            val findDate = sharedPref!!.getString("findDate", "0")
            if(!findDate.equals(date)) {
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref!!.edit()) {
                    putInt("key", 0)
                    apply()
                }
                levelUp(item, date)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun levelUp(number: Int, date: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val readValue = sharedPref!!.getInt("key", 0)
        println(readValue)
        if (readValue < number) {

            level++

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with(sharedPref!!.edit()) {
                putInt("key", number)
                putString("findDate", date)
                apply()
            }

            lvText!!.text = "SDGs達成レベル:$level"

            MainScope().launch {
                async {
                    fadeOut()
                }
                val a = async {
                    delay(3000)
                    fadeIn()
                }
                a.await()
            }
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityCreated(savedInstanceState)",
            "androidx.fragment.app.Fragment"
        )
    )
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    private fun fadeOut() {
        val lv = level % 30
        var lv1 = lv - 1
        if (lv1 == 0) {
            lv1 = 30
        }
        if (lv1 == -1) {
            lv1 = 29
        }
        val img = "grow$lv1"
        val reID = resources.getIdentifier(img, "drawable", context?.packageName)
        lvImg!!.setImageResource(reID)

        val alphaFadeout = AlphaAnimation(1.0f, 0.0f)

        alphaFadeout.duration = 3000

        alphaFadeout.fillAfter = true
        lvImg!!.startAnimation(alphaFadeout)

        val level2 = level - 1
        lvText!!.text = "SDGs達成レベル：$level2"
        lvText!!.startAnimation(alphaFadeout)
    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    private fun fadeIn() {
        var lv = level % 30
        if (lv == 0) {
            lv = 30
        }
        val img = "grow$lv"
        val reID = resources.getIdentifier(img, "drawable", context?.packageName)
        lvImg!!.setImageResource(reID)

        val alphaFadeIn = AlphaAnimation(0.0f, 1.0f)

        alphaFadeIn.duration = 3000

        alphaFadeIn.fillAfter = true
        lvImg!!.startAnimation(alphaFadeIn)
        lvText!!.text = "SDGs達成レベル：$level"
        lvText!!.startAnimation(alphaFadeIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("level2", level)
            apply()
            println("通過３")
        }
        _binding = null
    }

    private var weekName = arrayOf(
        "（日）", "（月）", "（火）", "（水）",
        "（木）", "（金）", "（土）"
    )
}