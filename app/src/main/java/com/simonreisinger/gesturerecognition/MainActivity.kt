package com.simonreisinger.gesturerecognition

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import java.text.SimpleDateFormat
import java.util.*


open class MainActivity : Activity(), OnTouchListener, CvCameraViewListener2 {
    private var utilities: Utilities = Utilities()
    private var findHand: FindHand? = null
    private var websiteComunicator: WebsiteComunicator = WebsiteComunicator()
    private var webView: WebView? = null
    private var lowerThreshold: EditText? = null
    private var upperThreshold: EditText? = null
    private var mIsColorSelected = false
    private var mRgba: Mat? = null
    private var mBlobColorRgba: Scalar? = null
    private var mBlobColorHsv: Scalar? = null
    private var mSpectrum: Mat? = null
    private var SPECTRUM_SIZE: Size? = null
    private var CONTOUR_COLOR: Scalar? = null
    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")
                    mOpenCvCameraView!!.enableView()
                    mOpenCvCameraView!!.setOnTouchListener(this@MainActivity)
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }


    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        webView = websiteComunicator.initWebView(findViewById<WebView>(R.id.Web_View))

        mOpenCvCameraView =
            findViewById(R.id.color_blob_detection_activity_surface_view) as CameraBridgeViewBase?
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
        lowerThreshold = findViewById<EditText>(R.id.lowerThreshold)
        lowerThreshold!!.setText("150.0")
        upperThreshold = findViewById<EditText>(R.id.upperThreshold)
        upperThreshold!!.setText("205.0")

        val buttonClick: Button = findViewById<Button>(R.id.playButton)
        buttonClick.setOnClickListener {
            buttonClick.setBackgroundColor(Color.BLUE)
            findHand = FindHand()

            val imgMat = utilities.openingImages("/sdcard/DCIM/depth.png")
            val lT = lowerThreshold!!.getText().toString().toFloat()
            val uT = upperThreshold!!.getText().toString().toFloat()
            //val imgMatProcessed = imgMat, // works
            //val imgMatProcessed = findHand.identifyContour(imgMat, lT.toDouble(), uT.toDouble()), // works
            val imgMatProcessed = findHand!!.identifyContour(
                imgMat,
                lT.toDouble(),
                uT.toDouble()
            )
            val biggestContour = findHand!!.getHandContour(imgMatProcessed)
            if (imgMat != null && biggestContour != null && biggestContour != MatOfPoint()) {
                findHand!!.getRoughHull(biggestContour);
                utilities.MatToImgView(
                    findHand!!.matToView!!,
                    //utilities.iterateOverPixels(imgMat, lT, uT),
                    findViewById<ImageView>(R.id.imgView)
                )
                val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                val currentDateandTime: String = sdf.format(Date())
                val fileName: String = "Image_$currentDateandTime.jpg"

                //utilities.saveMatAsImageToDevice(this, fileName, mRgba)

                if (false && webView != null) {
                    websiteComunicator.run(webView!!, "101")
                }
            } else {
                Log.ERROR
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                TAG,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
        mSpectrum = Mat()
        mBlobColorRgba = Scalar(255.0)
        mBlobColorHsv = Scalar(255.0)
        SPECTRUM_SIZE = Size(200.0, 64.0)
        CONTOUR_COLOR = Scalar(255.0, 0.0, 0.0, 255.0)
    }

    override fun onCameraViewStopped() {
        if (mRgba != null) {
            mRgba!!.release()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false // don't need subsequent touch events
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        mRgba = inputFrame.rgba() //.gray();
        return mRgba
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
    }

    @Override
    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

}