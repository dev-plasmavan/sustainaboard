package jp.shsit.sustinaboard.ui.actions.pay.ocr

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentOcrItemBinding

class AddItemFragment : Fragment() {
    private var _binding: FragmentOcrItemBinding? = null
    private var textName: EditText? = null
    private var textPrice: EditText? = null
    private var textKind: TextView? = null
    var position:Int=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcrItemBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        //もどるボタン
        _binding!!.backBtn.setOnClickListener {
            setFragmentResult("resultAdd", bundleOf("key1" to textName!!.text.toString(), "key2" to textPrice!!.text.toString(),
                "key3" to textKind!!.text.toString()))
            activity?.supportFragmentManager?.popBackStack("OcrResult",0)
        }

        //項目
        textName = _binding!!.textName

        //金額
        textPrice = _binding!!.editPrice

        //分類
        textKind = _binding!!.textkind



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
                        textKind!!.text = "その他"
                    }
                }
                true
            }
        }

        return root
    }
}