package com.example.myapplication.nfcSupport

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.constants.AppConstants
import okhttp3.ResponseBody
import retrofit2.Response


object NetworkUtils {


    /**
     * check if the internet is available
     * **/
    fun isInternetAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }





    /**
     * Access connected wifi name from this var ..because we are using listener .. to set value ..
     * @see setCallBack method ..
     */
    var connectedWifiName = ""



    /**
     * This method will set the connected wifi name for all phones
     * @see connectedWifiName variable
     * @param mContext is the current fragment context ..
     * must be called when permission is granted ..
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    fun setConnectedWifiName(mContext : Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            val connManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connManager.registerNetworkCallback(request, setCallBack())
        } else {
            setWifiName(mContext)
        }

    }

    /**
     * This method will only be called for new phones from s/31/And -12 -- Snow Cone
     * required ...  ACCESS_NETWORK_STATE and ACCESS_FINE_LOCATION permissions
     * need to pass FLAG_INCLUDE_LOCATION_INFO to NetworkCallback(), otherwise you will get "unknow ssid" only
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setCallBack(): ConnectivityManager.NetworkCallback {
        val networkCallback =
            object : ConnectivityManager.NetworkCallback(
                FLAG_INCLUDE_LOCATION_INFO
            ) {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    setUpWifiName(networkCapabilities.transportInfo as WifiInfo)
                }
            }

        return networkCallback

    }

    /**
     * This method will only be called --- below s/31/And -12 -- Snow Cone
     * @param mContext is current fragment context
     * ACCESS_WIFI_STATE  is required
     * ACCESS_COARSE_LOCATION with ACCESS_FINE_LOCATION for some specific version --->= 28/0/Oreo
     * @see [https://developer.android.com/develop/connectivity/wifi/wifi-scan#wifi-scan-permissions] for reference ..
     */
    @Suppress("DEPRECATION")
    private fun setWifiName(mContext : Context) {
        val wifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo =
            wifiManager.connectionInfo

        if (wifiInfo?.supplicantState == SupplicantState.COMPLETED) {
            setUpWifiName(wifiInfo)
        }
    }

    /**
     * Setting exact wifi name from here ..
     * @param wifiInfo is instance of WifiInfo ..
     */
    private fun setUpWifiName(wifiInfo: WifiInfo){
        connectedWifiName = wifiInfo.ssid.replace("\"", "")
    }





}