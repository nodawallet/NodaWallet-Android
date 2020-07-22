package com.application.nodawallet.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.application.nodawallet.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import kotlinx.android.synthetic.main.activity_scan.*
import java.io.IOException
import java.lang.reflect.Field
import kotlin.Result

class ScanActivity : BaseActivity() {

    var barcodeDetector: BarcodeDetector? = null
    var cameraSource: CameraSource? = null
    var cameraid = 0
    private val CAMERA = 0x5
    private val TAG = ScanActivity::class.java.simpleName
    var isFlashOn = false
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
    private val RESULT_LOAD_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)


        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askForPermission(Manifest.permission.CAMERA, CAMERA)

            }

        }

        myscan.startAnimation()

        cameraView.setZOrderMediaOverlay(true)

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ActivityCompat.checkSelfPermission(
                        this@ScanActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                try {
                    if (cameraSource != null)
                        cameraSource!!.start(cameraView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
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
                if (cameraSource != null) {
                    cameraSource!!.release()
                    cameraSource = null
                }
            }
        })


        imgFlash.setOnClickListener {

            if (isFlashOn) {

                turnOffFlashLight()
                isFlashOn = false
                imgFlash.setImageResource(R.drawable.ic_flash_on)

            } else {

                turnOnFlashLight()
                isFlashOn = true
                imgFlash.setImageResource(R.drawable.ic_flash_off)
            }

        }



        imgGalleryQr.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                insertDummyContactWrapper()
            } else {
                callcoverphot()
            }
        }

        /*frontback_camera.setOnClickListener(View.OnClickListener {
            if (cameraSource != null) {
                cameraSource!!.release()
                cameraSource = null
            }
            if (cameraid == CameraSource.CAMERA_FACING_BACK) {
                cameraid = CameraSource.CAMERA_FACING_FRONT
                cameraSource = CameraSource.Builder(this@ScanActivity, barcodeDetector)
                    .setFacing(cameraid)
                    .setRequestedFps(24f)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(1920, 1024)
                    .build()
            } else if (cameraid == CameraSource.CAMERA_FACING_FRONT) {
                cameraid = CameraSource.CAMERA_FACING_BACK
                cameraSource = CameraSource.Builder(this@ScanActivity, barcodeDetector)
                    .setFacing(cameraid)
                    .setRequestedFps(24f)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(1920, 1024)
                    .build()
            }


            try {

                if (ActivityCompat.checkSelfPermission(this@ScanActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@OnClickListener
                }
                cameraSource!!.start(cameraView.holder)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })*/

    }

    override fun onResume() {
        super.onResume()

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()
        if (!barcodeDetector!!.isOperational) {
            return
        }
        cameraSource = if (cameraSource != null) {
            cameraSource!!.release()
            null
        } else {

            CameraSource.Builder(this@ScanActivity, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24f)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1024)
                .build()

        }

        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                //   Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    val intent = Intent()
                    intent.putExtra("barcode", barcodes.valueAt(0).displayValue)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
    }

    @SuppressLint("NewApi")
    private fun insertDummyContactWrapper() {
        val permissionsNeeded = ArrayList<String>()

        val permissionsList = ArrayList<String>()
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read")
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write")
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Access Camera")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                // Need Rationale
                var message = "Grant Access" + permissionsNeeded[0]
                for (i in 1 until permissionsNeeded.size)
                    message = message + ", " + permissionsNeeded[i]
                /*showMessageOKCancel(message,
                    DialogInterface.OnClickListener { dialog, which ->
                        requestPermissions(
                            permissionsList.toTypedArray(),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                        )
                    })*/
                requestPermissions(
                    permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
                return
            }
            requestPermissions(
                permissionsList.toTypedArray(),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
            )
            return
        }

        callcoverphot()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false
        }
        return true
    }

    fun callcoverphot() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE)
    }

    var mCamera: Camera? = null

    private fun findCameraObject(): Boolean {
        if (cameraSource == null) {
            return false
        }

        var declaredFields: Array<Field>? = null
        try {
            declaredFields = CameraSource::class.java.declaredFields
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        if (declaredFields == null) {
            return false
        }

        for (field in declaredFields) {
            if (field.getType() === Camera::class.java) {
                field.setAccessible(true)
                try {
                    mCamera = field.get(this.cameraSource) as Camera
                    if (mCamera != null) {
                        val params = mCamera!!.parameters
                        params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                        mCamera!!.parameters = params
                        //  setCamera(camera)
                        return true
                    }

                    return false
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                break
            }
        }
        return false
    }

    fun turnOnFlashLight() {
        try {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                findCameraObject()


                val params = mCamera!!.getParameters()
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                mCamera!!.setParameters(params)

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                baseContext,
                "Exception throws in turning on flashlight.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun turnOffFlashLight() {
        try {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                findCameraObject()

                val params = mCamera!!.getParameters()
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                mCamera!!.setParameters(params)

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                baseContext,
                "Exception throws in turning off flashlight.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            try {
                val selectedImage = data.data
                var photoURIs = getImagePath(selectedImage!!)
                Log.e(TAG, "Bitmap=" + photoURIs)

                val bitmap =  BitmapFactory.decodeFile(photoURIs)
                val scaled= Bitmap.createScaledBitmap(bitmap,400,400,false)

                Log.e(TAG, "Bitmap=" + scaled)
                if (scaled != null){
                    galleyimage(scaled)
                }
            }
            catch (e:Exception){
                Toast.makeText(this,getString(R.string.qrcode_notclear),Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

        }
    }

    fun getImagePath(uri: Uri): String {
        var cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }

    fun StringToBitMap(encodedString: String): Bitmap? {
        try {
            var options = BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            var bitmap = BitmapFactory.decodeFile(encodedString, options);


            /*  val imageBytes = Base64.decode(encodedString, 0)
              val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)*/
            /*  var encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                  Log.e(TAG,"Bitmapstring="+encodeByte+encodedString)

                  val inputStream = ByteArrayInputStream(encodeByte)
                  val bitmap = BitmapFactory.decodeStream(inputStream)*/
            return bitmap
        } catch (e: Exception) {
            e.message
            return null
        }

    }

    fun decode(bMap: Bitmap){
        var contents: String? = null

        val intArray = IntArray(bMap.getWidth() * bMap.getHeight())
        //copy pixel data from the Bitmap into the 'intArray' array
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight())

        val source: LuminanceSource =
            RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
            Log.e("qrcontent",contents)

        } catch (e: java.lang.Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }


    }

    fun galleyimage(bitmap: Bitmap) {
        val frame = Frame.Builder().setBitmap(bitmap).build()
        Log.e(TAG, "Bitmapss=" + bitmap)

        val width = bitmap.width
        val height = bitmap.height

        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels,0,width,0,0,width,height)

        val source =  RGBLuminanceSource(width, height, pixels)
        val binary =  BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        var result:com.google.zxing.Result? = null

        try {
            result = reader.decode(binary)
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        val text = result?.text

        if (text == null){

            if (barcodeDetector!!.isOperational) {
                val sparseArray = barcodeDetector!!.detect(frame)
                if (sparseArray != null && sparseArray.size() > 0) {
                    for (i in 0 until sparseArray.size()) {
                        Log.e(TAG, "Value: " + sparseArray.valueAt(i).rawValue + "----" + sparseArray.valueAt(i).displayValue)
                        val intent = Intent()
                        intent.putExtra("barcode", sparseArray.valueAt(i).rawValue)
                        intent.putExtra("type", "gallery")
                        setResult(RESULT_OK, intent)
                        finish()

                    }
                } else {
                    Log.e(TAG, "SparseArray null or empty")

                }

            } else {
                Log.e(TAG, "Detector dependencies are not yet downloaded")
            }
        }
        else {
            val intent = Intent()
            intent.putExtra("barcode",text)
            intent.putExtra("type", "gallery")
            setResult(RESULT_OK, intent)
            finish()
        }

        Log.d("text",""+text)

       /* if (barcodeDetector!!.isOperational) {
            val sparseArray = barcodeDetector!!.detect(frame)
            if (sparseArray != null && sparseArray.size() > 0) {
                for (i in 0 until sparseArray.size()) {
                    Log.e(TAG, "Value: " + sparseArray.valueAt(i).rawValue + "----" + sparseArray.valueAt(i).displayValue)
                    val intent = Intent()
                    intent.putExtra("barcode", sparseArray.valueAt(i).rawValue)
                    intent.putExtra("type", "gallery")
                    setResult(RESULT_OK, intent)
                    finish()

                }
            } else {
                Log.e(TAG, "SparseArray null or empty")

            }

        } else {
            Log.e(TAG, "Detector dependencies are not yet downloaded")
        }*/
    }

    private fun askForPermission(permission: String, requestCode: Int?) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode!!)

            } else {

                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode!!)
            }

        } else {

        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}
