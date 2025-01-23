package jp.shsit.sustinaboard.ui.dashboard.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.google.ai.client.generativeai.GenerativeModel
import jp.shsit.sustinaboard.BuildConfig
import jp.shsit.sustinaboard.databinding.FragmentDashboardRecommendMealBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class RecommendMeal : Fragment() {

    private var _binding: FragmentDashboardRecommendMealBinding? = null

    private lateinit var loadingView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardRecommendMealBinding.inflate(inflater, container, false)

        val foodContent = arguments?.getString("foodContent")

        if (foodContent != null && foodContent != "") {
            val inputContent: String = "以下の材料のどれか一つまたは複数で作れる料理を詳しい手順含みで提案してください。: $foodContent"

            loadingView = _binding!!.loadingView
            showLoadingState()

            val responseText = _binding!!.responseResult
            generateResponse(inputContent) {
                responseText.text = it
            }

            val backBtn = _binding!!.backBtn
            backBtn.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack("DashboardDay", 0)
            }
        } else {
            Toast.makeText(context, "登録されている食品がないためおすすめの料理を提案できません。", Toast.LENGTH_LONG).show()
            activity?.supportFragmentManager?.popBackStack("DashboardDay", 0)
        }

        return _binding!!.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private fun showLoadingState() {
        loadingView.visibility = View.VISIBLE
    }

    private fun showContentState() {
        loadingView.visibility = View.GONE
    }

    private fun generateResponse(prompt: String, onResponseReceived: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash-exp",
                apiKey = BuildConfig.apiKey,
            )

            val response: String = generativeModel.generateContent(prompt).text.toString()

            withContext(Dispatchers.Main) {
                showContentState()
                onResponseReceived(response)
            }
        }
    }
}