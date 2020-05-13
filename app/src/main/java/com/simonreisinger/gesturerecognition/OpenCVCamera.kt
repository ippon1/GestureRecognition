package com.simonreisinger.gesturerecognition

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.File


class OpenCVCamera : AppCompatActivity(), CvCameraViewListener2 {
    private var cameraBridgeViewBase: OpenCameraView? = null
    private var colorRgba: Mat? = null
    private var colorGray: Mat? = null
    private var des: Mat? = null
    private var forward: Mat? = null
    private var preprocessor: ImagePreprocessor? = null
    private val baseLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> cameraBridgeViewBase.enableView()
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_open_cvcamera)
        preprocessor = ImagePreprocessor()
        cameraBridgeViewBase = findViewById<View>(R.id.camera_view) as OpenCameraView
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE)
        cameraBridgeViewBase.setCvCameraViewListener(this)
        cameraBridgeViewBase.disableFpsMeter()
        val takePictureBtn =
            findViewById<View>(R.id.take_picture) as ImageView
        takePictureBtn.setOnClickListener {
            val outPicture: String =
                Constants.SCAN_IMAGE_LOCATION.toString() + File.separator + Utilities.generateFilename()
            FolderUtil.createDefaultFolder(Constants.SCAN_IMAGE_LOCATION)
            cameraBridgeViewBase.takePicture(outPicture)
            Toast.makeText(this@OpenCVCamera, "Picture has been taken ", Toast.LENGTH_LONG)
                .show()
            Log.d(TAG, "Path $outPicture")
        }
    }

    public override fun onPause() {
        super.onPause()
        if (cameraBridgeViewBase != null) cameraBridgeViewBase.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                TAG,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, baseLoaderCallback)
        } else {
            Log.d(
                TAG,
                "OpenCV library found inside package. Using it!"
            )
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraBridgeViewBase != null) cameraBridgeViewBase.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        colorRgba = Mat(height, width, CvType.CV_8UC4)
        colorGray = Mat(height, width, CvType.CV_8UC1)
        des = Mat(height, width, CvType.CV_8UC4)
        forward = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        colorRgba!!.release()
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        colorRgba = inputFrame.rgba()
        preprocessor.changeImagePreviewOrientation(colorRgba, des, forward)
        return colorRgba
    }

    companion object {
        private val TAG = OpenCVCamera::class.java.simpleName
    }
}
