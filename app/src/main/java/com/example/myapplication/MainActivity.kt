package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.util.forEach
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class MainActivity : AppCompatActivity() {


    private var requestForCameraQr: ActivityResultLauncher<String>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initializeCameraLauncherForQR()
    }

    private var isBarCodeScanPossible = false

    private fun initializeCameraLauncherForQR() {
        requestForCameraQr = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) { isPermissionGranted ->
            if (!isPermissionGranted)
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            else {
                isBarCodeScanPossible = isPermissionGranted
                onResume()
            }
        }
    }


    private var qrOrBarCodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    private fun settingUpSurfaceViewCallBacks() {
        qrOrBarCodeDetector = BarcodeHelper.getResultOfBarcodeDetectorBuildInstance(this)
        cameraSource =
            BarcodeHelper.getResultOfCameraSourceBuildInstance(qrOrBarCodeDetector!!, this)

        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource?.start(binding.surfaceView.holder)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }

        })

        qrOrBarCodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been stopped", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(barcodesDetcetionList: Detector.Detections<Barcode>) {
                val bCode = barcodesDetcetionList.detectedItems
                if (bCode.size() == 0)
                    binding.testValueOfBarcode.post {
                        binding.testValueOfBarcode.text = ""
                    }
                else
                    bCode.forEach { key, value ->
                        binding.testValueOfBarcode.post {
                            binding.testValueOfBarcode.text = value.displayValue
                        }
                    }


            }

        })


    }

    override fun onPause() {
        super.onPause()
        cameraSource?.release()
    }

    override fun onResume() {
        super.onResume()
        if(isBarCodeScanPossible)
        settingUpSurfaceViewCallBacks()
        else requestForCameraQr?.launch(android.Manifest.permission.CAMERA)
    }
}