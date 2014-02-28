package com.johnathangilday.autograder

import boofcv.alg.feature.shapes.ShapeFittingOps
import boofcv.alg.filter.binary.{Contour, BinaryImageOps, ThresholdImageOps}
import boofcv.alg.misc.ImageStatistics
import boofcv.core.image.ConvertBufferedImage
import boofcv.gui.binary.VisualizeBinaryData
import boofcv.gui.feature.VisualizeShapes
import boofcv.io.image.UtilImageIO
import boofcv.struct.image.{ImageFloat32, ImageUInt8}
import com.typesafe.scalalogging.slf4j.Logging
import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color}
import java.io.File
import scala.collection.JavaConversions._

/**
 * Processes one marked exam sheet
 */
class SheetProcessor extends Logging {

  def processFile(file: File) {
    val bufImg = loadBufferedImage(file)
    processImage(bufImg)
  }

  def processImage(bufImg: BufferedImage) {
    ImgLogger.debug(bufImg, "original")
    val binary = convertToBinary(bufImg)
    val binaryNoNoise = removeNoise(binary)
    ImgLogger.debug(VisualizeBinaryData.renderBinary(binary, null), "binary")
    ImgLogger.debug(VisualizeBinaryData.renderBinary(binaryNoNoise, null), "binary-no-noise")
    val circles = findCircles(binaryNoNoise)
    val debugImage = drawCirclesOnImage(binaryNoNoise, circles)
    ImgLogger.debug(debugImage, "circles")
  }

  def loadBufferedImage(file: File): BufferedImage = UtilImageIO.loadImage(file.getAbsolutePath)

  def findCircles(binary: ImageUInt8): List[Contour] = {
    val contours = BinaryImageOps.contour(binary, 8, null)
    contours.toList
  }


  def convertToBinary(img: BufferedImage): ImageUInt8 = {
    val input = ConvertBufferedImage.convertFromSingle(img, null, classOf[ImageUInt8])
    val binary = new ImageUInt8(input.width, input.height)
//    val label = new ImageSInt32(input.width,input.height)
    // create a binary image by thresholding
    ThresholdImageOps.threshold(input, binary, 20, true)
    binary
  }

  // the mean pixel value is often a reasonable threshold when creating a binary image
  def calculateMeanPixel(img: ImageFloat32): Double = ImageStatistics.mean(img)

  def removeNoise(binary: ImageUInt8): ImageUInt8 = {
    // remove small blobs through erosion and dilation
    // The null in the input indicates that it should internally declare the work image it needs
    // this is less efficient, but easier to code.
    val filtered = BinaryImageOps.erode8(binary, null)
    BinaryImageOps.dilate8(filtered, null)
  }

  def drawCirclesOnImage(image: ImageUInt8, contours: List[Contour]): BufferedImage = {
    val bufImg = VisualizeBinaryData.renderBinary(image, null)
    // Fit an ellipse to each external contour and draw the results
    val g2 = bufImg.createGraphics()
    g2.setStroke(new BasicStroke(3))
    g2.setColor(Color.RED)

    contours.foreach(c => {
      val ellipse = ShapeFittingOps.fitEllipse_I32(c.external, 0, false, null)
      VisualizeShapes.drawEllipse(ellipse.shape, g2)
    })
    bufImg
  }
}
