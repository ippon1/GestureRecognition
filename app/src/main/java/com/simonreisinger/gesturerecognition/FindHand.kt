package com.simonreisinger.gesturerecognition

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

// Source: https://medium.com/@muehler.v/simple-hand-gesture-recognition-using-opencv-and-javascript-eb3d6ced28a0
class FindHand{

    // https://stackoverflow.com/questions/22240220/thresholding-image-in-opencv-for-a-range-of-max-and-min-values
    fun identifyCertainDepth(imMa: Mat?): Mat{
        val im = Mat()
        Imgproc.threshold(imMa, im, 100.0, 150.0, Imgproc.THRESH_BINARY);
        return im
    }

    fun selectDepth(){

    }

    fun getHandContour(){

    }

    fun DrawHandContour(){

    }

    fun getRoughHull(){

    }


}