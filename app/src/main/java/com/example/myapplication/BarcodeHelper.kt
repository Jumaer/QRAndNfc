package com.example.myapplication
import android.content.Context
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

object BarcodeHelper {

    private var qrOrBarCodeDetector : BarcodeDetector? = null
    fun getResultOfBarcodeDetectorBuildInstance(context : Context)
    : BarcodeDetector {
        if(qrOrBarCodeDetector != null)
            return qrOrBarCodeDetector as BarcodeDetector
        return BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    }

    fun getResultOfCameraSourceBuildInstance(
        anyQrOrBarCodeDetector : BarcodeDetector,
        context : Context
    ): CameraSource {
        return   CameraSource.Builder(context, anyQrOrBarCodeDetector)
            .setRequestedPreviewSize(1920,1080)
            .setAutoFocusEnabled(true)
            .build()
    }
}