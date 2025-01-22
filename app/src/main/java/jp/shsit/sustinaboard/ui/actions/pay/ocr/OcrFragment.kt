package jp.shsit.sustinaboard.ui.actions.pay.ocr

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import jp.shsit.sustinaboard.databinding.FragmentOcrBinding
import jp.shsit.sustinaboard.R


class OcrFragment : Fragment() {

    private var _binding: FragmentOcrBinding? = null
    private var imageCapture: ImageCapture? = null
    private var cameraControl: CameraControl? = null

    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(
            JapaneseTextRecognizerOptions.Builder().build())
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOcrBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        val capbtn : Button = root.findViewById(R.id.capterbtn)
        capbtn.setOnClickListener {
            capture()
        }

        // _binding!!.textGuide.text = "上端は、日時が入るようにしてください。\n" + "10cm程度離してください。"

        val backBtn : Button = root.findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("actions", 0)
        }

        return root
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showResultText(resultText: String ,resultDay:String,resultArray:ArrayList<String>) {

        val transaction = parentFragmentManager.beginTransaction()
        val sub = OcrResultFragment()
        val bundle = Bundle()
        bundle.putString(OcrResultFragment.KEY_RESULT_TEXT,resultText)
        bundle.putString(OcrResultFragment.KEY_RESULT_TEXT2,resultDay)
        bundle.putStringArrayList(OcrResultFragment.KEY_RESULT_TEXT3,resultArray)
        sub.arguments = bundle

        transaction.addToBackStack("OcrResult")
        transaction.replace(R.id.fragment1, sub)
        transaction.commit()

    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider =
                cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            preview.setSurfaceProvider( _binding?.viewFinder?.surfaceProvider)

            imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()


            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            cameraControl = camera.cameraControl

            @SuppressLint("UseSwitchCompatOrMaterialCode") val sw: Switch = requireView().findViewById(R.id.switch2)
            sw.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cameraControl!!.enableTorch(true)
                } else {
                    cameraControl!!.enableTorch(false)
                }
            }

            try {

            } catch(exc: Exception) {
                Log.e(TAG, "ユースケースのバインディングに失敗", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        startCamera()
    }
    override fun onPause() {
        super.onPause()
        try
        {cameraControl!!.enableTorch(false)
        }
        catch (_:Exception){}
        _binding = null
    }

    private fun capture() {
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback() {

            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                var resultString = ""
                val lines : ArrayList<ValueLine> = ArrayList()
                super.onCaptureSuccess(imageProxy)
                var count = 0
                var titleMax=0
                var storeName=""

                imageProxy.image?.let { mediaImage ->
                    val image = InputImage.fromMediaImage(
                        mediaImage, imageProxy.imageInfo.rotationDegrees)
                    textRecognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            for( block in visionText.textBlocks ){
                                for(line in block.lines){
                                    val rect1 = line.cornerPoints?.get(0)?.y
                                    val rect2 = line.cornerPoints?.get(0)?.x
                                    // val rect3 = line.boundingBox?.right
                                    // val rect4 = line.boundingBox?.bottom

                                    val line1 = ValueLine()
                                    line1.name = line.text
                                    line1.yPoint = rect1.toString().toInt()
                                    line1.xPoint = rect2.toString().toInt()
                                    lines.add(line1)


                                    var textHeight = line.cornerPoints?.get(3)?.y!! - line.cornerPoints?.get(0)?.y!!
                                    //店名の読み込み 初めの５行で大きな文字
                                    if(count<5){
                                        if(titleMax < textHeight){
                                            storeName = line.text
                                            titleMax= textHeight
                                            Log.i("test",storeName + " max1")
                                        }
                                    }
                                    Log.i("test",line.text + " max2"+  textHeight)
                                    count += 1

                                }

                            }

                            lines.sortBy{it.yPoint}

                            var resultDay = ""

                            for(line in lines){
                                if((line.name.contains("年"))&&(line.name.contains("月"))&&(line.name.contains("日"))){
                                    val finish = line.name.indexOf('日')
                                    resultDay= line.name.substring(0, finish+1)


                                    Log.i("test",resultDay+"koko3")
                                }
                                else if((line.name.contains("/"))&&(line.name.contains("202"))){
                                    println( line.name.indexOf("202") )

                                    var year: String
                                    var mon: String
                                    var day: String
                                    val start = line.name.indexOf("202")
                                    line.name.indexOf("/")
                                    val date = line.name.substring(start)
                                    val split: Array<String> = date.split("/".toRegex()).dropLastWhile {
                                        it.isEmpty() }.toTypedArray()
                                    year=split[0]
                                    mon=split[1]
                                    day = split[2]
                                    if(day.length>2){
                                        day=day.substring(0,2)
                                    }
                                    resultDay = year +"年"+mon+"月"+day+"日"
                                    Log.i("test", "スラッシュ検出,$year,$mon,$day")
                                }


                            }

                            val linesNew : ArrayList<ValueLine> = ArrayList()
                            var i=0
                            while(i<lines.size){
                                val line = lines[i]

                                if(i==lines.size-1){
                                    linesNew.add(line)
                                    resultString += lines[i].name

                                }
                                else if(lines[i+1].yPoint -line.yPoint > 80){
                                    linesNew.add(line)
                                    resultString += lines[i].name
                                    resultString += '\n'


                                }
                                else{
                                    if(lines[i+1].xPoint>line.xPoint){
                                        linesNew.add(line)
                                        linesNew.add(lines[i+1])
                                        resultString += lines[i].name
                                        resultString +="　" + lines[i+1].name
                                        resultString += '\n'


                                    }else{
                                        linesNew.add(lines[i+1])
                                        linesNew.add(line)
                                        resultString += lines[i+1].name
                                        resultString +="　"+ lines[i].name
                                        resultString += '\n'
                                    }
                                    i++
                                }
                                i++
                            }

                            val resultArray:ArrayList<String> = ArrayList()

                            var strOld = ""
                            var str: String

                            val priceRegex = Regex("(^[¥￥+＋ー*＊※込非軽-])\\d")
                            val priceAfterRegex = Regex("\\d([¥￥+＋ー*＊※込非軽-]$)")
                            val numberRegex = Regex("^[0-9]+$")
                            val trimRegex = Regex("[ー*＊※込非軽]")

                            for(line in linesNew){
                                str = line.name

                                println("str = $str")

                                if (str.contains("円")){
                                    str = str.replace("円","")
                                    Log.i("test",str + "です 円削除 ")
                                }

                                Log.i("test",str + "です syoukei ")
                                if (str.contains("合計") || str.contains("小計")) {
                                    break
                                }

                                if (str.contains("¥")) {
                                    var indexPrice: Int = 0

                                    indexPrice = str.indexOf("¥")

                                    resultArray.add(str.substring(0, indexPrice))
                                    resultArray.add(str.substring(indexPrice))

                                    println("resultArray added contains some = ${str.substring(0, indexPrice)} and ${str.substring(indexPrice)}")
                                }

                                if ((str.contains(priceRegex) || str.contains(priceAfterRegex)) || str.contains(numberRegex) ) {
                                    val strTrim: String = str.replace(trimRegex, "")
                                    println("strTrim = $strTrim")

                                    if (strTrim.contains("-")) {
                                        resultArray.add("割引")
                                        resultArray.add(strTrim)

                                        println("resultArray with save money = $strTrim")
                                    }
                                    else {
                                        resultArray.add(strOld)
                                        resultArray.add(strTrim)

                                        println("resultArray add = $strOld and $strTrim")
                                    }
                                }

                                strOld = str

                            }
                            showResultText(storeName,resultDay,resultArray)
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(context, "読み取りに失敗しました。もう一度やり直してください。", Toast.LENGTH_LONG).show()
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                            cameraControl!!.enableTorch(false)
                        }
                }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, exc.message, exc)
                Toast.makeText(requireContext(),
                    "エラー:" + exc.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val TAG = "TextRecognitionTestApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            arrayOf(android.Manifest.permission.CAMERA)
    }

    class ValueLine {
        var name:String=""
        var yPoint:Int=0
        var xPoint:Int=0

    }
}