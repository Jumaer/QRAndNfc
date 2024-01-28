package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.dialog.CardCheckNfcDialog
import com.example.myapplication.nfcSupport.NfcUtils
import com.example.myapplication.nfcSupport.NfcViewChangeListener
import com.example.myapplication.R
import com.example.myapplication.constants.AppConstants.DEEP_LINK_TAG
import com.example.myapplication.constants.AppConstants.LINK_OF_DATA_URL
import com.example.myapplication.constants.AppConstants.LINK_OF_DATA_URL_2
import com.example.myapplication.databinding.ActivityNfcQrScanBinding

class NfcQrScanActivity : AppCompatActivity() {



    private lateinit var binding: ActivityNfcQrScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        binding = ActivityNfcQrScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNav()


    }

    private fun handleDeepLink(navGraph: NavGraph, navController: NavController) {
        if (intent.data != null) {
            var moveType = 0

            if (intent.dataString?.contains(LINK_OF_DATA_URL) == true) {
                moveType = 1
            }

            // top up request link
            intent.dataString?.substringAfterLast("/")?.let {

                navGraph.apply {
                    addArgument(DEEP_LINK_TAG, NavArgument.Builder().setDefaultValue(it).build())
                    if (moveType == 0){
                      //  setStartDestination(R.id.setId)
                    }

                    else{
                       //  setStartDestination(R.id.setId)
                    }
                }
                navController.graph = navGraph
            }
        } else navController.setGraph(navGraph, intent.extras)
    }

    private lateinit var currentPage: String
    private fun setNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentPage = destination.label.toString()
        }
        val navGraph = navController.navInflater.inflate(R.navigation.nav_nfc_qr_card)
        this.window.apply { ->
            if (this != null)
                WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = true
        }
        navGraph.apply {
            setStartDestination(R.id.nfcScanFragment)
        }
        navController.setGraph(navGraph,intent.extras)

        handleDeepLink(navGraph,navController)
    }


    /**
     * After getting NFC Result we can get Data ..
     * use readFRomIntent Method ..
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (bottomSheet != null) {
            bottomSheet!!.dismiss()
            nfcViewListener?.readFromIntent(intent)
        }

    }


    private var bottomSheet: CardCheckNfcDialog? = null

    /**
     * Initialize nfc view change listener
     */
    private var nfcViewListener: NfcViewChangeListener? = null

    // create adapter
    var nfcAdapter: NfcAdapter? = null



    fun tryForNfc(listener: NfcViewChangeListener?) {
        nfcViewListener = listener

        /**
         * If nfc not supported need not to stay
         * Change text and hint of view
         */
        if (nfcAdapter == null) {
            nfcViewListener?.onPositiveView(false)
        }

        /**
         * If nfc supported need but not open
         * MOVE for the settings to active..
         */
        else if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, getString(R.string.active_nfc_hint),Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        /**
         * If nfc supported and can use it
         */
        else {
          ifNfcPresentAndActive()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun ifNfcPresentAndActive() {


        openNfcBs()

        //   readFromIntent(intent)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this@NfcQrScanActivity, 0, Intent(this, javaClass).addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                ), PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this@NfcQrScanActivity, 0, Intent(this, javaClass).addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                ), PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)

        val techTypes = arrayOf(arrayOf<String>(NfcF::class.java.name))



        nfcAdapter?.enableForegroundDispatch(
            this@NfcQrScanActivity, pendingIntent, arrayOf(tagDetected), techTypes
        )
    }

    /**
     * OPEN CUSTOM BS
     * ITS NOT MUST
     * ONLY FOR VIEW ..
     */
    private fun openNfcBs() {
        bottomSheet =
            NfcUtils.showScanBottomSheet(object : NfcUtils.BottomSheetNfcCancel {
            override fun onCancel() {
                nfcAdapter?.disableForegroundDispatch(this@NfcQrScanActivity)
            }

        }, this@NfcQrScanActivity)

        bottomSheet?.show()
    }

    /**
     * Disable nfc if not null
     */
    override fun onResume() {
        super.onResume()
        nfcAdapter?.disableForegroundDispatch(this)
    }


    override fun onDestroy() {
        super.onDestroy()
       // ImageUtils.clearCache(this)
    }




}