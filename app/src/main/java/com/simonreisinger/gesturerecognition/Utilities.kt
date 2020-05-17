package com.simonreisinger.gesturerecognition

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.CvException
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class Utilities {

    // https://stackoverflow.com/questions/52146667/how-to-read-an-image-into-a-mat-object-using-opencv-c-and-android-studio-ndk
    fun openingImages(imagePath: String): Mat {
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

    fun converScalarHsv2Rgba(hsvColor: Scalar?): Scalar {
        val pointMatRgba = Mat()
        val pointMatHsv = Mat(1, 1, CvType.CV_8UC3, hsvColor)
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4)
        return Scalar(pointMatRgba[0, 0])
    }

    // https://stackoverflow.com/questions/44579822/convert-opencv-mat-to-android-bitmap
    fun MatToImgView(imgMat: Mat, imgView: ImageView) {
        val resultBitmap =
            Bitmap.createBitmap(imgMat.cols(), imgMat.rows(), Bitmap.Config.ARGB_8888)
        imgMat.convertTo(imgMat, 16);
        Utils.matToBitmap(imgMat, resultBitmap)
        val mResult: Bitmap = resultBitmap
        imgView.setImageBitmap(mResult);
    }

    fun saveMatAsImageToDevice(context: Context, fileName: String, mByte: Mat?) {
        val name = Environment.getExternalStorageDirectory().getPath().toString() + "/" + fileName
        Toast.makeText(context, "$fileName saved", Toast.LENGTH_SHORT).show()
        Imgcodecs.imwrite(fileName, mByte)
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()
        //Imgproc.cvtColor(input, rgb, Imgproc.COLOR_RGB2GRAY)
        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(rgb, bmp)
        } catch (e: CvException) {
            Log.d("Exception", e.message)
        }
        return bmp
    }


    fun iterateOverPixels(mat: Mat, lowerThreshold: Float, upperThreshold: Float): Mat {
        var newC: Mat = mat.clone()
        Imgproc.cvtColor(newC, newC, Imgproc.COLOR_BGR2GRAY);
        newC.convertTo(newC, CvType.CV_32F);
        val rows: Int = newC.rows() //Calculates number of rows
        val cols: Int = newC.cols() //Calculates number of columns
        val xxx = mat.type()
        val yyy = newC.type()
        val ch: Int =
            newC.channels() //Calculates number of channels (Grayscale: 1, RGB: 3, etc.)

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val data: DoubleArray = newC.get(i, j) //Stores element in an array
                for (k in 0 until ch)  //Runs for the available number of channels
                {
                    val oldvalue = data[k]
                    if (oldvalue < lowerThreshold || oldvalue > upperThreshold) {
                        data[k] = 0.0
                    } else {
                        data[k] = oldvalue //  * (-1) //Pixel modification done here
                    }
                }
                newC.put(i, j, doubeleArraytoFloatArray(data))
                // newC.put(i, j, doubeleArraytoFloatArray(data)) //Puts element back into matrix
            }
        }
        return newC
    }

    // https://stackoverflow.com/questions/7513434/convert-a-double-array-to-a-float-array
    fun doubeleArraytoFloatArray(arr: DoubleArray?): FloatArray {
        if (arr == null) return FloatArray(0)
        val n = arr.size
        val ret = FloatArray(n)
        for (i in 0 until n) {
            ret[0] = arr[i].toFloat()
        }
        return ret
    }
}