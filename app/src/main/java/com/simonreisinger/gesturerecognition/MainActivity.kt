package com.simonreisinger.gesturerecognition

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import org.opencv.imgcodecs.Imgcodecs.imread
import org.opencv.imgproc.Imgproc
import java.text.SimpleDateFormat
import java.util.*


open class MainActivity : Activity(), OnTouchListener, CvCameraViewListener2 {
    private var utilities: Utilities = Utilities()
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
        mOpenCvCameraView =
            findViewById(R.id.color_blob_detection_activity_surface_view) as CameraBridgeViewBase?
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)

        val buttonClick: Button = findViewById<Button>(R.id.playButton)
        buttonClick.setOnClickListener {
            val imgMat = utilities.opening_images("/sdcard/DCIM/depth.png")
            utilities.MatToImgView(imgMat, findViewById<ImageView>(R.id.imgView))
            buttonClick.setBackgroundColor(Color.BLUE)
            val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
            val currentDateandTime: String = sdf.format(Date())
            val fileName: String =
                Environment.getExternalStorageDirectory().getPath()
                    .toString() + "/TODO_" + currentDateandTime + ".jpg"
            Toast.makeText(this, "$fileName saved", Toast.LENGTH_SHORT).show()
            Imgcodecs.imwrite(fileName, mRgba)
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
        mRgba = inputFrame.gray(); //.rgba()
        val im = Mat()
        // https://stackoverflow.com/questions/22240220/thresholding-image-in-opencv-for-a-range-of-max-and-min-values
        Imgproc.threshold(mRgba, im, 100.0, 150.0, Imgproc.THRESH_BINARY);


        return im
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
    }
}