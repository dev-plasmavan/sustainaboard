package jp.shsit.sustinaboard.ui.actions.sdgsmarks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.R
import jp.shsit.sustinaboard.databinding.FragmentSdgsmarksListBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class SdgsmarksList : Fragment() {

    private var _binding : FragmentSdgsmarksListBinding? = null
    private lateinit var itemArray : ArrayList<CsvItem>

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!

    @SuppressLint("DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSdgsmarksListBinding.inflate(inflater, container, false)

        csvReader()

        val listData = ArrayList<ListItem>()
        for(item in itemArray){
            val list = ListItem()
            list.listNo = item.no
            list.listName = item.name
            list.listID =  resources.getIdentifier(item.mark,"drawable",context?.packageName)
            listData.add(list)
        }

        val listview =  binding.list

        val customAdapter = CustomAdapter(requireContext(),listData)
        listview.adapter = customAdapter

        listview.setOnItemClickListener { _, _, position, _ ->
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val sub = SdgsmarksResult()

            val bundle = Bundle()
            bundle.putInt("key1",position)
            sub.arguments =bundle

            transaction?.addToBackStack(null)
            transaction?.replace(R.id.fragment1,sub)
            transaction?.commit()

            Log.i("test",position.toString() + "です")

        }

        val backButton = binding.buttonBack
        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("mark_read", 0)
        }

        return binding.root
    }

    private fun csvReader() {
        itemArray = ArrayList()
        var item: CsvItem
        val inputStream = resources.assets.open("SDGsMarks.csv")
        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        val bufferedReader = BufferedReader(inputStreamReader)
        bufferedReader.forEachLine {
            if (it.isNotBlank() && itemArray.size < 30) {
                item = CsvItem()
                val line = it.split(",").toTypedArray()
                val v0 = line[0]
                val v1 = line[1]
                val v2 = line[2]
                val v3 = line[3]
                item.no = v0
                item.name = v1
                item.dis = v2
                item.mark = v3
                itemArray.add(item)
                println("size" + itemArray.size)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, 0)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

