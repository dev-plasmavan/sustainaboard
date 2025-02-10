package jp.shsit.sustinaboard.ui.actions.sdgsmarks

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import jp.shsit.sustinaboard.ml.ModelUnquant
import jp.shsit.sustinaboard.databinding.FragmentSdgsmarksReadBinding
import jp.shsit.sustinaboard.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutionException
import kotlin.math.floor
import kotlin.math.min


class SdgsmarksRead : Fragment() {
    private var _binding: FragmentSdgsmarksReadBinding? = null
    // private val binding get() = _binding!!
    private var imageView: ImageView? = null
    private var imageSize = 224
    private var imageCapture: ImageCapture? = null
    private val REQUIRED_PERMISSIONS =
        arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
    private val REQUEST_CODE_FOR_PERMISSIONS = 1234
    private lateinit var itemArray : ArrayList<String>
    var image: Nothing? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSdgsmarksReadBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        //result = binding.result
        // confidence = binding.confidence

        if (checkPermissions()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_FOR_PERMISSIONS
            )
        }

        startCamera()
        _binding!!.button.setOnClickListener {
            capturePhoto()
        }

        val backBtn = root.findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("actions",0)
        }

        csvReader()

        return root
    }
    private fun checkPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val previewView: PreviewView = _binding!!.viewFinder
                val preview =
                    Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                val cameraControl = camera.cameraControl

                @SuppressLint("UseSwitchCompatOrMaterialCode") val sw: Switch =
                    requireView().findViewById(R.id.switch1)
                sw.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cameraControl.enableTorch(true)
                    } else {
                        cameraControl.enableTorch(false)
                    }
                }

            } catch (e: ExecutionException) {
                //  Log.e(TAG, e.getLocalizedMessage(), e);
            } catch (_: InterruptedException) {

            } catch (_: NullPointerException) {

            }
        }, ContextCompat.getMainExecutor(requireContext()))
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun capturePhoto()
    {
        // 撮影してテキスト認識 (後で記載)
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback() {

            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                super.onCaptureSuccess(imageProxy)
                var bitmap2:Bitmap = imageProxytoBitmap(imageProxy)

                /***縮小**********************************************************/
                Log.i("test", "変更前" + bitmap2.width + "," + bitmap2.height)
                val height = 672* bitmap2.height / bitmap2.width
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 672, height, true)
                Log.i("test", "変更後" + bitmap2.width + "," + bitmap2.height)
                /***カット**********************************************************/
                val result = Bitmap.createBitmap(bitmap2, bitmap2.width/2-112, bitmap2.height/2-112, 224, 224, null, true)
                // val result = Bitmap.createBitmap(bitmap2, bitmap2.width/2-112, bitmap2.height/2-112, 224, 224, null, true)

                // binding.imageView2.setImageBitmap(result)
                classifyImage(result)

            }
        })
    }
    private fun imageProxytoBitmap(image: ImageProxy): Bitmap {
        val planeProxy: ImageProxy.PlaneProxy = image.planes[0]

        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    private fun classifyImage(image: Bitmap?) {
        try {
            val model = ModelUnquant.newInstance(requireContext().applicationContext)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // get 1D array of 224 * 224 pixels in image
            val intValues = IntArray(imageSize * imageSize)
            image!!.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            println(itemArray.size)
            println(confidences.size)

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            if (maxPos >= itemArray.size) {
                maxPos = itemArray.size - 1
            }
            println(maxPos)
            // result!!.text = classes[maxPos]
            println(itemArray[maxPos])
            var s = ""
            for (i in confidences.indices) {
                println(itemArray[i])
                println(confidences[i] * 100)
                s += String.format("%s: %.1f%%\n", itemArray[i], confidences[i] * 100)
            }
            //confidence!!.text = s
            println(s)

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val sub = SdgsmarksResult()
            val bundle = Bundle()

            val percentage = confidences[maxPos] * 100
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_text_layout, null)
            val resultImage = dialogView.findViewById<ImageView>(R.id.dialog_image_view)

            val strId = resources.getIdentifier("mark${maxPos + 1}", "drawable", activity?.packageName)
            resultImage.setImageResource(strId)

            if (maxPos < 30) {
                if (percentage > 85) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("環境ラベルの読み取り")
                        .setMessage("マークを読み取りました。\n" + "結果：" + itemArray[maxPos] + "が" + floor(percentage) + "％でした。\n" + "続ける場合は「はい」をタップし、リストから選ぶ場合は「リストから」をタップしてください。")
                        .setView(dialogView)
                        .setPositiveButton("はい") { _, _ ->
                            bundle.putInt("key1", maxPos)
                            sub.arguments = bundle

                            transaction?.addToBackStack(null)
                            transaction?.replace(R.id.fragment1, sub)
                            transaction?.commit()

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

                            val cameraProvider = cameraProviderFuture.get()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
                            val cameraControl = camera.cameraControl

                            cameraControl.enableTorch(false)

                            model.close()
                        }

                        .setNeutralButton("リストから") { _, _ ->
                            transaction?.addToBackStack(null)
                            transaction?.replace(R.id.fragment1, SdgsmarksList())
                            transaction?.commit()

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

                            val cameraProvider = cameraProviderFuture.get()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
                            val cameraControl = camera.cameraControl

                            cameraControl.enableTorch(false)
                        }

                        .setNegativeButton("再読取り") { _, _ ->

                            startCamera()

                            model.close()

                        }.show()
                }
                else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("環境ラベルの読み取り")
                        .setMessage("マークが読み取れませんでした。再度読み取る場合は「再読取り」をタップし、リストから選ぶ場合は「リストから」をタップしてください。")

                        .setPositiveButton("再読取り") { _, _ ->
                            startCamera()

                            model.close()
                        }

                        .setNeutralButton("リストから") { _, _ ->
                            transaction?.addToBackStack(null)
                            transaction?.replace(R.id.fragment1, SdgsmarksList())
                            transaction?.commit()

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

                            val cameraProvider = cameraProviderFuture.get()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            val camera = cameraProvider.bindToLifecycle(this, cameraSelector)
                            val cameraControl = camera.cameraControl

                            cameraControl.enableTorch(false)
                        }.show()
                }
            }
            else {
                Toast.makeText(context, "非対象マークを読み取りました。もう一度やり直してください。", Toast.LENGTH_LONG).show()

                startCamera()
            }

        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            var image = data!!.extras!!["data"] as Bitmap?
            val dimension = min(image!!.width, image.height)
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            imageView!!.setImageBitmap(image)
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
            classifyImage(image)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun csvReader() {
        itemArray = ArrayList()

        val inputStream = resources.assets.open("SDGsMarks.csv")
        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        val bufferedReader = BufferedReader(inputStreamReader)
        bufferedReader.forEachLine {
            if (it.isNotBlank()) {
                val line = it.split(",").toTypedArray()
                val v1 = line[1]
                itemArray.add(v1)
            }
        }
    }
}