package com.simonreisinger.gesturerecognition

import android.R
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import org.opencv.android.Utils
import org.opencv.core.CvException
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


class Utilities {

    // https://stackoverflow.com/questions/52146667/how-to-read-an-image-into-a-mat-object-using-opencv-c-and-android-studio-ndk
    fun opening_images(imagePath: String): Mat {
        val image: Mat
        var sillyString = ""

        image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR)
        sillyString = if (image.empty()) {
            "Image not loaded"
        } else {
            "Image loaded"
        }
        Log.d("imageLodad", sillyString)

        return image
    }

    private fun converScalarHsv2Rgba(hsvColor: Scalar?): Scalar {
        val pointMatRgba = Mat()
        val pointMatHsv = Mat(1, 1, CvType.CV_8UC3, hsvColor)
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4)
        return Scalar(pointMatRgba[0, 0])
    }

    // https://stackoverflow.com/questions/44579822/convert-opencv-mat-to-android-bitmap
    fun MatToImgView(imgMat: Mat, imgView: ImageView) {
        var resultBitmap =
            Bitmap.createBitmap(imgMat.cols(), imgMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(imgMat, resultBitmap)
        val mResult: Bitmap = resultBitmap
        imgView.setImageBitmap(mResult);
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB)
        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(rgb, bmp)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }
        return bmp
    }


}