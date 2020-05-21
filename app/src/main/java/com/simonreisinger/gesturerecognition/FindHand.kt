package com.simonreisinger.gesturerecognition

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


// Source: https://medium.com/@muehler.v/simple-hand-gesture-recognition-using-opencv-and-javascript-eb3d6ced28a0
// https://github.com/justadudewhohacks/opencv-express
class FindHand {

    // https://stackoverflow.com/questions/28570088/opencv-java-inrange-function
    fun identifyContour(imMa: Mat?, lowerThreshold: Double, higherThreshold: Double): Mat {

        // filter by closeness to camera
        val rangeMask = Mat()
        val lowerThresholdTEST = 50.0
        val higherThresholdTEST = 205.0
        Core.inRange(
            imMa,
            Scalar(lowerThresholdTEST, lowerThresholdTEST, lowerThresholdTEST),
            Scalar(higherThresholdTEST, higherThresholdTEST, higherThresholdTEST),
            rangeMask
        )

        // remove noise
        val blurred = Mat()
        Imgproc.GaussianBlur(rangeMask, blurred, Size(1.0, 1.0), 1.0);
        return blurred
        //val thresholded = Mat()
        //Imgproc.threshold(blurred, thresholded, 100.0, 150.0, Imgproc.THRESH_BINARY);
        //return thresholded
    }

    // Source: https://answers.opencv.org/question/103377/android-findcontours-returning-too-many-contours/
    fun getHandContour(src: Mat?): MatOfPoint {
        if (src == null) {
            print("Source image is null")
            return MatOfPoint()
        }
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(
            src, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        )

        val contourImg = Mat(src.size(), src.type())
        val biggestContour = 5; // TODO make this dynamic and include // find biggest hull
        //for (i in contours.indices) {
        Imgproc.drawContours(contourImg, contours, biggestContour, Scalar(255.0, 255.0, 255.0), -1)
        //}

        //return contourImg
        return contours[biggestContour]
    }

    // https://stackoverflow.com/questions/29316117/draw-convex-hull-on-android
    // TODO edit so it accepcts multiple
    fun getRoughHull(originalImage: Mat, contour: MatOfPoint): Mat {
        val maxDist = 25
        val hulls: MutableList<MatOfInt> = ArrayList()
        val defects: MutableList<MatOfInt4> = ArrayList()
        hulls.add(MatOfInt())
        defects.add(MatOfInt4())
        Imgproc.convexHull(contour, hulls[0]);
        Imgproc.convexityDefects(contour, hulls[0], defects[0])

        val defectSP = Point()
        val defectEP = Point()
        val defectFP = Point()

        for (j in 0 until defects[0].rows()) {
            val currentDefect = defects[0]
            val xxxx = defects[0][j, 0]
            val startId = defects[0][j, 0][0]
            val endId = defects[0][j, 0][1]
            val farId = defects[0][j, 0][2]
            defectSP.x = startId
            defectSP.y = startId
            defectEP.x = endId
            defectEP.y = endId
            defectFP.x = farId
            defectFP.y = farId
            Imgproc.line(
                originalImage,
                defectSP,
                defectEP,
                Scalar(255.0, 255.0, 0.0),
                2,
                Imgproc.LINE_AA,
                0
            )
            Imgproc.circle(originalImage, defectSP, 3, Scalar(255.0, 255.0, 0.0), -1)
            Imgproc.circle(originalImage, defectEP, 3, Scalar(255.0, 0.0, 255.0), -1)
            Imgproc.circle(originalImage, defectFP, 3, Scalar(0.0, 255.0, 255.0), -1)
        }
        return originalImage
    }

    fun DrawHandContour() {

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