package com.simonreisinger.gesturerecognition

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.function.BiPredicate
import kotlin.math.sqrt


// Source: https://medium.com/@muehler.v/simple-hand-gesture-recognition-using-opencv-and-javascript-eb3d6ced28a0
// https://github.com/justadudewhohacks/opencv-express
class FindHand {
    var matToView: Mat? = null

    // https://stackoverflow.com/questions/28570088/opencv-java-inrange-function
    fun identifyContour(imMa: Mat?, lowerThreshold: Double, higherThreshold: Double): Mat {

        // filter by closeness to camera
        val rangeMask = Mat()
        val lowerThresholdTEST = lowerThreshold //
        val higherThresholdTEST = higherThreshold //
        Core.inRange(
            imMa,
            Scalar(lowerThresholdTEST, lowerThresholdTEST, lowerThresholdTEST),
            Scalar(higherThresholdTEST, higherThresholdTEST, higherThresholdTEST),
            rangeMask
        )

        // remove noise
        val blurred = Mat()
        Imgproc.GaussianBlur(rangeMask, blurred, Size(1.0, 1.0), 1.0);
        matToView = imMa//blurred
        return blurred
        //val thresholded = Mat()
        //Imgproc.threshold(blurred, thresholded, 100.0, 150.0, Imgproc.THRESH_BINARY);
        //return thresholded
    }

    // Source: https://answers.opencv.org/question/103377/android-findcontours-returning-too-many-contours/
    // Source: https://docs.opencv.org/3.4/d9/d8b/tutorial_py_contours_hierarchy.html
    fun getHandContour(src: Mat?): /*Mat*/ MatOfPoint {
        if (src == null) {
            print("Source image is null")
            return MatOfPoint()
        }
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(
            src, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE
        )
        val selectedContour = 5; // TODO make this dynamic and include // find biggest hull

        if (true) {
            drawHandContour(contours, hierarchy, Mat(src.size(), src.type()))
        } else {
            matToView = Mat(src.size(), src.type())
            Imgproc.drawContours(
                matToView,
                contours,
                selectedContour,
                Scalar(255.0, 255.0, 255.0),
                5,
                Imgproc.LINE_8,
                hierarchy,
                100
            )
        }
        //drawHandContour(contours, hierarchy, matToView!!)
        return contours[selectedContour] // WORKS CHECKED
    }

    fun drawHandContour(contours: List<MatOfPoint>, hierarchy: Mat, contourImg: Mat) {
        for (i in contours.indices) {
            val color = Scalar(255.0, 255.0, 255.0)
            Imgproc.drawContours(contourImg, contours, i, color, 1, Imgproc.LINE_8, hierarchy, 100)
        }
        //matToView = contourImg

    }

    // https://stackoverflow.com/questions/29316117/draw-convex-hull-on-android
    // TODO edit so it accepcts multiple
    fun getRoughHull(contour: MatOfPoint): MutableList<Point> { /*originalImage: Mat, */
        val maxDist = 25.0
        val hulls: MutableList<MatOfInt> = ArrayList()
        val defects: MutableList<MatOfInt4> = ArrayList()
        hulls.add(MatOfInt())
        defects.add(MatOfInt4())
        Imgproc.convexHull(contour, hulls[0], false)
        hulls[0] = simplifyHull(contour, hulls[0], maxDist)
        Imgproc.convexityDefects(contour, hulls[0], defects[0])

        val oImage = matToView // Mat(originalImage.size(), 16) //
        val data: Array<Point> = contour.toArray()

        val points: MutableList<Point> = ArrayList()

        drawdefectsOnMat(defects, data, points, oImage)

        return points
    }

    // TODO maybe remove the sqrt to make it mor efficient
    private fun ptDist(pt1: Point, pt2: Point): Double {
        val xDiff: Double = pt1.x - pt2.x
        val yDiff: Double = pt1.y - pt2.y
        val distanceBtwPoints = sqrt((xDiff * xDiff) + (yDiff * yDiff));
        return distanceBtwPoints
    }

    private fun getCenterPt(pts: ArrayList<Point>){

    }

    private fun simplifyHull(contour: MatOfPoint, hull: MatOfInt, maxDist: Double): MatOfInt {

        val hullPoints: MutableList<Point> = java.util.ArrayList()
        val data: Array<Point> = contour.toArray()
        for (item in hull.toList()) {
            hullPoints.add(data[item])
        }

        // group all points in local neighborhood
        val labels: MutableList<Int> = java.util.ArrayList()

        // Simple predicate for checking equality
        val predicate =
            label@ BiPredicate { pt1: Point, pt2: Point ->
                if (ptDist(pt1, pt2) >= maxDist) return@label true
                false
            }

        Partition.partition(hullPoints, labels, predicate)

        val pointsByLabel: Map<Int, Point> = labels.zip(hullPoints).toMap()

        // map points in local neighborhood to most central point


        // const pointGroups = Array.from(pointsByLabel.values());
        //var pointGroups
        val pointGroup = Point()
        var getMostCentralPoint = (pointGroup.) ->

        var pointGroups = pointsByLabel.values.toTypedArray()
        pointGroups.map { pointGroup.x }


        val peopleToAge = mapOf("Alice" to 20, "Bob" to 21)
        println(peopleToAge.map { (name, age) -> "$name is $age years old" }) // [Alice is 20 years old, Bob is 21 years old]
        println(peopleToAge.map { it.value }) // [20, 21]

        return MatOfInt() // TODO change this line
    }

    fun drawdefectsOnMat(
        defects: MutableList<MatOfInt4>,
        data: Array<Point>,
        points: MutableList<Point>,
        oImage: Mat?
    ) {
        var j = 0
        while (j < defects[0].rows()) {
            val fixptDepth = defects[0][j, 0][3]
            if (fixptDepth > 0.0) {
                val defectSP = data[defects[0][j, 0][0].toInt()]
                points.add(defectSP)
                val defectEP = data[defects[0][j, 0][1].toInt()]
                points.add(defectEP)
                val defectFP = data[defects[0][j, 0][2].toInt()]
                points.add(defectFP)

                Imgproc.circle(oImage, defectSP, 3, Scalar(255.0, 0.0, 0.0), -1)
                Imgproc.line(
                    oImage,
                    defectSP,
                    defectEP,
                    Scalar(255.0, 255.0, 0.0),
                    2,
                    Imgproc.LINE_AA,
                    0
                )
                Imgproc.circle(oImage, defectEP, 3, Scalar(0.0, 255.0, 0.0), -1)
                Imgproc.line(
                    oImage,
                    defectEP,
                    defectFP,
                    Scalar(255.0, 255.0, 0.0),
                    2,
                    Imgproc.LINE_AA,
                    0
                )
                Imgproc.circle(oImage, defectFP, 3, Scalar(0.0, 0.0, 255.0), -1)

            }
            j += 1
        }
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