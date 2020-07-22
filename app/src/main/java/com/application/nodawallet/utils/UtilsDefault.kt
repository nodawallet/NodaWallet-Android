package com.application.nodawallet.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import com.application.nodawallet.R
import com.application.nodawallet.activity.MainActivity
import com.application.nodawallet.application.NodaApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object UtilsDefault {

    val STATUS_SUCCESS = true
    val STATUS_FAILURE = false
    val STATUS_TOKENEXPIRE = 401
    val STATUS_ERROR = 402
    val NORESULT = 400
    val TOKEN_NOT_PROVIDED = 404

    internal var letter = Pattern.compile("[a-zA-z]")
    internal var digit = Pattern.compile("[0-9]")

    val SHOULD_PRINT_LOG = false

    val sBuildType = BuildType.PROD

    fun debugLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            Log.d(tag, message)
        }
    }

    fun errorLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            //
        }
        Log.e(tag, message)
    }

    fun infoLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            Log.i(tag, message)
        }
    }

    fun mnemonicToAddress(text:String):String{
        val mnemonic = text
        val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
        val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
        val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)
        val credentials =  Credentials.create(derivedKeyPair)
        return credentials.address
    }

    fun sendNotification(context: Context,title:String,message:String) {
        val channelId = "NODA_CHANNEL_ID"
        val channelName = "NODAWALLET"


        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(checkNull(title))
            .setContentText(checkNull(message))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = "nodawalletchannel"
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val notificationID = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
        notificationManager.notify(notificationID, notificationBuilder.build())


    }


    fun mnemonicToPrivateKey(text:String):String{
        val mnemonic = text
        val derivationPath = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0,0)
        val masterkeypair =  Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic,""))
        val derivedKeyPair =  Bip32ECKeyPair.deriveKeyPair(masterkeypair,derivationPath)
        val credentials =  Credentials.create(derivedKeyPair)

        return Numeric.toHexStringNoPrefix(credentials.ecKeyPair.privateKey)
    }

    fun formatDecimal(text: String): String {
        /* val format = DecimalFormat("#0.000")
         return format.format(java.lang.Double.parseDouble(text.trim { it <= ' ' }))
      */
        val format = DecimalFormat("#0.00")
        var bd = BigDecimal(text.toDouble())
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP)
        return format.format(bd.toDouble())
    }
    fun formatToken(text: String): String {
        /* val format = DecimalFormat("#0.000")
         return format.format(java.lang.Double.parseDouble(text.trim { it <= ' ' }))
      */
        val format = DecimalFormat("#0.000")
        var bd = BigDecimal(text.toDouble())
        bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP)
        return format.format(bd.toDouble())
    }

    fun formatMarket(text: String): String {
        /* val format = DecimalFormat("#0.000")
         return format.format(java.lang.Double.parseDouble(text.trim { it <= ' ' }))
      */
        val format = DecimalFormat("#0.0000")
        var bd = BigDecimal(text.toDouble())
        bd = bd.setScale(5, BigDecimal.ROUND_HALF_UP)
        return format.format(bd.toDouble())
    }


    fun formatCryptoCurrency(text: String): String {

        if (text == "" || text == null){
            return  ""
        }

        val format = DecimalFormat("#0.00000000")
        var bd = BigDecimal(text.replace(",",".").toDouble())
        bd = bd.setScale(9, BigDecimal.ROUND_HALF_UP)
        return format.format(bd.toDouble())
    }

    fun copyToClipboard(context: Context,text: CharSequence) {
        var clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    fun getJsonParser():Gson{

        val builder = GsonBuilder()
        return builder.create()

    }



    private val SHARED_PREFERENCE_UTILS = "fgfdg"
    private val FCMKEYSHAREDPERFRENCES = "gfdgffdgfd"

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesfcm: SharedPreferences? = null

    private fun initializeSharedPreference() {

        sharedPreferences = NodaApplication.instance.getSharedPreferences(
            SHARED_PREFERENCE_UTILS,
            Context.MODE_PRIVATE
        )
    }

    private fun initializeSharedPreferencefcm() {

        sharedPreferencesfcm = NodaApplication.instance.getSharedPreferences(
            FCMKEYSHAREDPERFRENCES,
            Context.MODE_PRIVATE
        )
    }

    fun updateSharedPreferenceFCM(key: String, value: String) {
        if (sharedPreferencesfcm == null) {
            initializeSharedPreferencefcm()
        }

        val editor = sharedPreferencesfcm!!.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun updateSharedPreferenceString(key: String, value: String) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putString(key, value)
        editor.commit()
    }


    fun updateSharedPreferenceInt(key: String, value: Int) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }


    fun updateSharedPreferenceBoolean(key: String, value: Boolean) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }


    fun setLoggedIn(context: Context, value: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val ed = sp.edit()
        ed.putBoolean(Constants.LOGIN_STATUS, value)
        ed.commit()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(Constants.LOGIN_STATUS, false)
    }

    fun getSharedPreferenceValuefcm(key: String?): String? {
        if (sharedPreferencesfcm == null) {
            initializeSharedPreferencefcm()
        }

        return if (key != null) {
            sharedPreferencesfcm!!.getString(key, null)
        } else {
            null
        }
    }

    fun getSharedPreferenceString(key: String?): String? {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        return if (key != null) {
            sharedPreferences!!.getString(key, null)
        } else {
            null
        }
    }

    fun isAppInstalled(context: Context,packagename:String):Boolean {

        val pm = context.packageManager
        try {
            pm.getPackageInfo(packagename,0)
            return true
        }
        catch (e:Exception){
            e.printStackTrace()
            return false
        }
    }

    fun getSharedPreferenceInt(key: String?): Int {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        return if (key != null) {
            sharedPreferences!!.getInt(key, -1)
        } else {
            0
        }
    }

    fun getSharedPreferenceBoolean(key: String?): Boolean {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        return key != null && sharedPreferences!!.getBoolean(key, false)
    }


    fun checkNull(data: String?): String {
        return data ?: ""
    }

//    public static int checkIntNull(int data){
//        if (data==null){
//            return 0;
//        }else {
//            return data;
//        }
//    }

    fun ok(password: String): Boolean {
        val hasLetter = letter.matcher(password)
        val hasDigit = digit.matcher(password)
        return hasLetter.find() && hasDigit.find()
    }

    fun printException(exception: Exception) {
        exception.printStackTrace()
    }

    fun isNetworkConnected(): Boolean {

        val manager =
            NodaApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val allNetworks = manager?.allNetworks?.let { it } ?: return false
            allNetworks.forEach { network ->
                val info = manager.getNetworkInfo(network)
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        } else {
            val allNetworkInfo = manager?.allNetworkInfo?.let { it } ?: return false
            allNetworkInfo.forEach { info ->
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        }
        return false
    }

    fun isOnline(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =
            NodaApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val netInfo = cm.activeNetworkInfo
                if (netInfo != null) {
                    return (netInfo.isConnected() && (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                val netInfo = cm.activeNetwork
                if (netInfo != null) {
                    val nc = cm.getNetworkCapabilities(netInfo);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ));
                }
            }
        }

        return haveConnectedWifi || haveConnectedMobile
    }

    /*fun isOnline(): Boolean {
        val cm = RewardApplication.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }*/

    enum class BuildType {
        QA, PROD
    }

    fun convertDecimal(decimal:String) : String{

        val numner = decimal
        val add = numner.toDouble()
        val add2= add.toInt()
        val nu = add2+1
        val formatted = String.format("%1$-" + nu + "s", "1").replace(' ', '0')

        return formatted
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun isEmailValid(email: String): Boolean {

        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()

    }

    fun mAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun clearSession() {

        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        sharedPreferences!!.edit().clear().apply()
    }

    fun validate(password: String): Boolean {
        return password
            .matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%&*()_+=|<>?{}\\[\\]~-])".toRegex())
    }

    fun validPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher

        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
       // val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z][a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()

    }

    fun validateDOB(newDate: Calendar): Boolean {
        val minAdultAge = GregorianCalendar()
        minAdultAge.add(Calendar.YEAR, -18)
        return !minAdultAge.before(newDate)
    }

    fun checkUsername(str: String): Boolean {

        var pattern = Pattern.compile("^[a-zA-Z]+$")
        var matcher = pattern.matcher(str)
        return matcher.matches()
    }

    fun validUrl(str: String): Boolean {

        return Patterns.WEB_URL.matcher(str).matches()

    }

    fun Capsfistletter(text: String): String {

        val upperString = text.substring(0, 1).toUpperCase() + text.substring(1)

        return upperString
    }


    fun isEmailPassword(email: String): Boolean {

        val flag: Boolean
        val expression = "(?=.*[a-z])(?=.*\\d)"

        val inputStr = email.trim { it <= ' ' }
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(inputStr)

        flag = matcher.matches()
        return flag

    }

    fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                Log.e("msg", "==$aMessageDigest")
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun convertToHex(data: ByteArray): String {
        val buf = StringBuilder()
        for (b in data) {
            var halfbyte = b.toInt().ushr(4) and 0x0F
            var two_halfs = 0
            do {
                buf.append(if (0 <= halfbyte && halfbyte <= 9) ('0'.toInt() + halfbyte).toChar() else ('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = b.toInt() and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }


    fun SHA1(str: String): String {
        var digest: MessageDigest? = null
        var input: ByteArray? = null

        try {
            digest = MessageDigest.getInstance("SHA-1")
            digest!!.reset()
            input = digest.digest(str.toByteArray(charset("UTF-8")))

        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return convertToHex(input!!)
    }


    fun checkScreensize(context: Activity): Int {
        val dm = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dens = dm.densityDpi
        val wi = width.toDouble() / dens.toDouble()
        val hi = height.toDouble() / dens.toDouble()
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        val screenInches = Math.sqrt(x + y)
        val finalVal = Math.round(screenInches).toDouble()

        return finalVal.toInt()
    }


    fun hideKeyboardForFocusedView(activity: Activity) {
        val inputManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun currentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("dd MMM yyyy")
        return df.format(c.time)
    }
}