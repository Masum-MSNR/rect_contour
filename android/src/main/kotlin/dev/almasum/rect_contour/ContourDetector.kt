package dev.almasum.rect_contour

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.InputStream
import android.media.ExifInterface
import android.util.Log

class ContourDetector {
    fun onImageSelected(byteArray: ByteArray): String? {
        try {
            //input stream from byte array
            val iStream: InputStream = byteArray.inputStream()
            Log.v("TAG", byteArray.size.toString())
            val exif = ExifInterface(iStream)
            var rotation = -1
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotation = Core.ROTATE_90_CLOCKWISE
                ExifInterface.ORIENTATION_ROTATE_180 -> rotation = Core.ROTATE_180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotation = Core.ROTATE_90_COUNTERCLOCKWISE
            }
            var imageWidth: Double
            var imageHeight: Double

            imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0).toDouble()
            imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0).toDouble()
            if (rotation == Core.ROTATE_90_CLOCKWISE || rotation == Core.ROTATE_90_COUNTERCLOCKWISE) {
                imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0).toDouble()
                imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0).toDouble()
            }


            val inputData: ByteArray? = byteArray
            val mat = Mat(Size(imageWidth, imageHeight), CvType.CV_8U)
            mat.put(0, 0, inputData)
            val pic = Imgcodecs.imdecode(mat, Imgcodecs.CV_LOAD_IMAGE_UNCHANGED)
            if (rotation > -1) Core.rotate(pic, pic, rotation)
            mat.release()
            return processPicture(pic)
        } catch (error: Exception) {
            return null
        }

    }

    private fun processPicture(previewFrame: Mat): String? {
        val contours = findContours(previewFrame)
        return getCorners(contours, previewFrame.size())
    }

    private fun getCorners(contours: List<MatOfPoint>, size: Size): String? {
        val indexTo: Int = when (contours.size) {
            in 0..5 -> contours.size - 1
            else -> 4
        }
        for (index in 0..contours.size) {
            if (index in 0..indexTo) {
                val c2f = MatOfPoint2f(*contours[index].toArray())
                val peri = Imgproc.arcLength(c2f, true)
                val approx = MatOfPoint2f()
                Imgproc.approxPolyDP(c2f, approx, 0.03 * peri, true)
                val points = approx.toArray().asList()
                val convex = MatOfPoint()
                approx.convertTo(convex, CvType.CV_32S)
                // select biggest 4 angles polygon
                if (points.size == 4 && Imgproc.isContourConvex(convex)) {
                    val foundPoints = sortPoints(points)
                    val tl = foundPoints[0]
                    val tr = foundPoints[1]
                    val br = foundPoints[2]
                    val bl = foundPoints[3]

                    if (tl.x < tr.x && bl.x < br.x && tl.y < bl.y && tr.y < br.y) {
                        return "{\"tl\":\"$tl\",\"tr\":\"$tr\",\"br\":\"$br\",\"bl\":\"$bl\"}"
                    }
                }

                Log.v("TAG", "getCorners" + points.size.toString())
            } else {
                return null
            }
        }

        return null
    }

    private fun sortPoints(points: List<Point>): List<Point> {
        val p0 = points.minByOrNull { point -> point.x + point.y } ?: Point()
        val p1 = points.minByOrNull { point: Point -> point.y - point.x } ?: Point()
        val p2 = points.maxByOrNull { point: Point -> point.x + point.y } ?: Point()
        val p3 = points.maxByOrNull { point: Point -> point.y - point.x } ?: Point()
        return listOf(p0, p1, p2, p3)
    }

    private fun findContours(src: Mat): List<MatOfPoint> {

        val grayImage: Mat
        val cannedImage: Mat
        val kernel: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(9.0, 9.0))
        val dilate: Mat
        val size = Size(src.size().width, src.size().height)
        grayImage = Mat(size, CvType.CV_8UC4)
        cannedImage = Mat(size, CvType.CV_8UC1)
        dilate = Mat(size, CvType.CV_8UC1)

        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(grayImage, grayImage, Size(5.0, 5.0), 0.0)
        Imgproc.threshold(grayImage, grayImage, 20.0, 255.0, Imgproc.THRESH_TRIANGLE)
        Imgproc.Canny(grayImage, cannedImage, 75.0, 200.0)
        Imgproc.dilate(cannedImage, dilate, kernel)
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            dilate,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val filteredContours = contours
            .filter { p: MatOfPoint -> Imgproc.contourArea(p) > 100e2 }
            .sortedByDescending { p: MatOfPoint -> Imgproc.contourArea(p) }
            .take(25)

        hierarchy.release()
        grayImage.release()
        cannedImage.release()
        kernel.release()
        dilate.release()

        Log.v("TAG", "findContours" + filteredContours.size.toString())

        return filteredContours
    }
}