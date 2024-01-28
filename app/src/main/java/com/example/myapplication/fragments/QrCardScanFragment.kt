package com.example.myapplication.fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import com.example.myapplication.PermissionUtils
import com.example.myapplication.R
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.constants.AppConstants.ADDRESS_KEY
import com.example.myapplication.constants.AppConstants.COMPANY
import com.example.myapplication.constants.AppConstants.INSTAGRAM
import com.example.myapplication.constants.AppConstants.JOB_TITLE
import com.example.myapplication.constants.AppConstants.LINKED_IN
import com.example.myapplication.constants.AppConstants.MAIL
import com.example.myapplication.constants.AppConstants.NAME
import com.example.myapplication.constants.AppConstants.NEW_PAPER_CARD
import com.example.myapplication.constants.AppConstants.PHONE
import com.example.myapplication.constants.AppConstants.WEB
import com.example.myapplication.databinding.FragmentQrCardScanBinding
import com.example.myapplication.dialog.CardFailureDialog
import com.example.myapplication.dialog.CardScanDetectDialog
import com.example.myapplication.model.ContactScanModel
import com.example.myapplication.model.TextDataResponse
import com.example.myapplication.model.TextResponse
import com.example.myapplication.nfcSupport.AnimationUtils
import com.example.myapplication.nfcSupport.ImageUtils

import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toTimeUnit


class QrCardScanFragment : Fragment() {

   private lateinit var binding : FragmentQrCardScanBinding
   private lateinit var mContext : Context
   private lateinit var TAG : String







    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) setUpCamera()
            else PermissionUtils.showPermissionSettings(
                binding.root,
                activity,
                getString(R.string.camera_permission_needed)
            )
        }


    private lateinit var textRecognizer: TextRecognizer
    private fun recognizeText(imgUri: Uri?) {

        if (imgUri != null) {
            val image: InputImage
            try {
                image = InputImage.fromFilePath(mContext, imgUri)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // Task completed successfully
                        // ...
                        processText(visionText.textBlocks)
                        uri = imgUri.toString()
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                        Log.e(TAG, e.toString())
                    }


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private var contactDataList = mutableMapOf<String?, String?>()




    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "NFC CARD SCAN"
    }





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQrCardScanBinding.inflate(inflater, container, false)
        // AppConstants.allContacts = null
        // camera permission
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        binding.bsContact.btnRequest.setOnClickListener {
            if (::outputOpt.isInitialized)
                capturePhoto(outputOpt)
        }
        binding.bsContact.imgHistory.setOnClickListener {
          //  imgPickerScan()
        }
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        binding.imgBack.setOnClickListener {
            goBack()
        }
        // observeForText()
        return binding.root
    }

    private fun goBack() {
        if (parentFragmentManager.backStackEntryCount == 0) activity?.finish()
        else findNavController().popBackStack()
    }


    /**
     * Custom process
     */
    private fun processText(
        textBlocks: MutableList<Text.TextBlock>
    ) {
        contactDataList.clear()
        var resultText = ""
        val addNSlash = """\n"""


        for ((_, block) in textBlocks.withIndex()) {
            for (line in block.lines) {
                resultText = resultText + line.text + addNSlash
            }
        }


        return
    }


    /**
     * Do something with this data ..
     */
    private fun observeForText(body : String) {
        val response =
            Gson().fromJson(body, TextResponse::class.java)

    }

    private fun initialView(title: String? = null) {
        contactDataList.clear()
        bsScan.dismiss()
        startScan()
        serverErrorFail(title)
        return
    }

    private var isAlreadyCalled = false
    private fun serverErrorFail(title: String? = null) {
        if (title == null) return
        if (!isAlreadyCalled)
            CardFailureDialog(
                mContext,
                object : CardFailureDialog.OnDismissListener {
                    override fun onDismiss() {
                        isAlreadyCalled = false
                    }
                }, title, true
            )
        isAlreadyCalled = true
    }

    private fun setDataView(data: TextDataResponse?) {
        if (data == null) return
        contactDataList.clear()
        contactDataList.apply {
            put(NAME, data.name)
            put(MAIL, data.email)
            put(PHONE, data.phoneNumber)
            put(ADDRESS_KEY, data.address)
            put(COMPANY, data.company)
            put(JOB_TITLE, data.jobTitle)
            put(WEB, data.website)
            put(LINKED_IN, data.linkedin)
            put(INSTAGRAM, data.instagram)
        }
        bsScan.dismiss()

    }


    private fun getBody(data: String): RequestBody? {
      // Prepare json body
      return null
    }

    private var uri: String? = null

    private fun getCardScanBottomSheet(): CardScanDetectDialog {
        return CardScanDetectDialog(
            mContext,
            object : CardScanDetectDialog.OnScanSuccess {
                override fun onSuccess() {
                    if (contactDataList.isNotEmpty()) {
                        NEW_PAPER_CARD = true
                        val data = ContactScanModel(uri, contactDataList, true, null)

                        activity?.finish()
                    }
                }

            })
    }


    private lateinit var cameraController: LifecycleCameraController
    private lateinit var scanAnimator: ObjectAnimator
    lateinit var camera: Camera
    private var isFlushOn = false


    private fun setFlashlight() {

        binding.bsContact.imgAdd.setOnClickListener {
            if (cameraController.cameraInfo?.hasFlashUnit() == true) {
                isFlushOn = if (isFlushOn) {
                    cameraController.cameraControl?.enableTorch(false)
                    false
                } else {
                    cameraController.cameraControl?.enableTorch(true)
                    true
                }

            }


        }


    }


    private fun setAnimation() {
        // scan anim start
        scanAnimator = AnimationUtils.startLineVerticalAnimation(
            binding.viewScan,
            DurationUnit.SECONDS.toTimeUnit().toMillis(2)
        )
    }


    private fun checkArgument(qrCode: String) {

        var data = ""
        if (qrCode.contains(AppConstants.EVER_PROFILE.trim())) {
            stopScan()
            data = qrCode.replace(AppConstants.EVER_PROFILE, "")

           // bundleOf(AppConstants.SHOW_QR_PREVIEW to data)
        } else if (qrCode.contains(AppConstants.HASH_URL_BASE.trim())) {
            stopScan()
            data = qrCode.replace(AppConstants.HASH_URL_BASE, "")


           // bundleOf(AppConstants.DEEP_LINK_TAG to data)

        } else {
            startScan()
        }


    }


    override fun onPause() {
        super.onPause()
        stopScan()
    }


    override fun onResume() {
        super.onResume()
        startScan()
    }


    /**
     * This method will start setting Up the camera
     * @param [No param required]
     * @return [0 as Int]
     */

    private fun setUpCamera(): Int {
        val options = BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes()
            .build()
        val scanner = BarcodeScanning.getClient(options)
        // camera
        cameraController = LifecycleCameraController(mContext)


        // setting up preview and selector
        setPreviewSelector()

        // setting up listener
        setListenerQr(scanner)

        // prepare output options
        outputOpt = getOutPutOptions(getConValues())



        startScan()


        return 0
    }

    private lateinit var outputOpt: ImageCapture.OutputFileOptions


    /**
     * This method will be triggered to capture photo
     * @param outputOptions
     * @return
     */
    private fun capturePhoto(outputOptions: ImageCapture.OutputFileOptions) {

        // Set up image capture listener, which is triggered after photo has
        // been taken
        cameraController.apply {
            takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.d("Cap Exc---" , "$exc")
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        processForImage(output)
                    }
                }
            )
        }


        return

    }

    private lateinit var bsScan: CardScanDetectDialog

    private fun processForImage(output: ImageCapture.OutputFileResults) {
        output.savedUri?.let {
            stopScan()
            bsScan = getCardScanBottomSheet()
            bsScan.show()
            val bitmap = cropImage(
                ImageUtils.getBitmap(mContext, it),
                binding.scannerView, binding.cardShape
            )
            recognizeText(ImageUtils.getUri(mContext, bitmap))
        }
    }

    /**
     * get content values from here
     * no param required
     */
    private fun getConValues(): ContentValues {
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/${context?.getString(R.string.app_name)}"
                )
            }
        }
    }

    /**
     * Get output options from here
     * @param contentValues
     * @return ImageCapture.OutputFileOptions
     */
    private fun getOutPutOptions(contentValues: ContentValues): ImageCapture.OutputFileOptions {


        // Create output options object which contains file + metadata
        return ImageCapture
            .OutputFileOptions.Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()
    }

    /**
     * setting up preview and selector
     * No param required
     */
    private fun setPreviewSelector() {
        // set camera controller to preview
        binding.scannerView.controller = cameraController
        // Select back camera as a default
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()


        // set up selector to contoller
        cameraController.cameraSelector = cameraSelector

        return
    }


    /**
     * This method will set up
     * @param scanner
     * @return
     */
    private fun setListenerQr(
        scanner: BarcodeScanner
    ) {
        // scan qr from here..
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(mContext),
            MlKitAnalyzer(
                listOf(scanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(mContext)
            ) { result: MlKitAnalyzer.Result? ->

                val barcodeResults = result?.getValue(scanner)
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)
                ) {
                    binding.scannerView.overlay.clear()
                    return@MlKitAnalyzer
                }
                // detect qr from here ..
                else if (!barcodeResults.firstOrNull()?.rawValue.isNullOrBlank()) {

                    checkArgument(barcodeResults.first().rawValue.toString())
                }
            }
        )
        return
    }

    private fun stopScan() {
        // scan cancel
        if (::cameraController.isInitialized) cameraController.unbind()

        // scan anim cancel
        if (::scanAnimator.isInitialized) {
            scanAnimator.cancel()
        }
        setStatusBar(isLightBar = true)
    }

    private fun startScan() {
        setStatusBar(isLightBar = false)
        if (::cameraController.isInitialized) {
            cameraController.bindToLifecycle(viewLifecycleOwner)
            setFlashlight()
            setAnimation()
        }

    }



    private fun setStatusBar(isLightBar : Boolean){
        activity?.window?.apply { ->
            WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = isLightBar
        }
    }


    /**
     * scan gallery image ..
     * @param bitmap
     * @return
     */
    private fun scanGalleryImage(bitmap: Bitmap) {
        try {
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val result = MultiFormatReader().decode(binaryBitmap)

            val qrCode = result.text
            checkArgument(qrCode)

        } catch (e: Exception) {
            Toast.makeText(mContext,getString(R.string.image_invalid),Toast.LENGTH_SHORT).show()
        }

        return
    }




    /**
     * This method will cut the card shape
     * @param bitmap
     * @param frame
     * @param reference
     * @return [Bitmap]
     */
    private fun cropImage(bitmap: Bitmap, frame: View, reference: View): Bitmap {
        val heightOriginal = frame.height
        val widthOriginal = frame.width
        val heightFrame = reference.height
        val widthFrame = reference.width
        val leftFrame = reference.left
        val topFrame = reference.top
        val heightReal = bitmap.height
        val widthReal = bitmap.width
        val widthFinal = widthFrame * widthReal / widthOriginal
        val heightFinal = heightFrame * heightReal / heightOriginal
        val leftFinal = leftFrame * widthReal / widthOriginal
        val topFinal = topFrame * heightReal / heightOriginal
        return Bitmap.createBitmap(
            bitmap,
            leftFinal, topFinal, widthFinal, heightFinal
        )
    }

}