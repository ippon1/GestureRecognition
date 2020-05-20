package com.simonreisinger.gesturerecognition

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


// Source: https://medium.com/@muehler.v/simple-hand-gesture-recognition-using-opencv-and-javascript-eb3d6ced28a0
// https://github.com/justadudewhohacks/opencv-express
class FindHand {

    // https://stackoverflow.com/questions/28570088/opencv-java-inrange-function
    fun identifyContour(imMa: Mat?, lowerThreshold: Double, higherThreshold: Double): Mat {

        // filter by closeness to camera
        val rangeMask = Mat()
        Core.inRange(
            imMa,
            Scalar(lowerThreshold, lowerThreshold, lowerThreshold),
            Scalar(higherThreshold, higherThreshold, higherThreshold),
            rangeMask
        )

        // remove noise
        val blurred = Mat()
        Imgproc.GaussianBlur(rangeMask, blurred, Size(10.0, 10.0), 2.0);
        // Imgproc.threshold(imMa, im, 100.0, 150.0, Imgproc.THRESH_BINARY);

        return blurred
    }


    fun getHandContour() {

    }

    fun DrawHandContour() {

    }

    fun getRoughHull() {

    }

    fun thresholdAdaption(lowerThreshold: EditText?, upperThreshold: EditText?) {

        lowerThreshold?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })
        upperThreshold?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })
    }
}