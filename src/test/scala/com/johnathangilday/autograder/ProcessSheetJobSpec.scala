package com.johnathangilday.autograder

import boofcv.alg.feature.shapes.ShapeFittingOps
import boofcv.alg.filter.binary.Contour
import boofcv.gui.binary.VisualizeBinaryData
import boofcv.gui.feature.VisualizeShapes
import boofcv.struct.image.ImageUInt8
import com.johnathangilday.autograder.testutils.TestImgFactory
import com.johnathangilday.autograder.utils.Files2
import georegression.struct.shapes.EllipseRotated_F64
import java.awt.image.BufferedImage
import java.awt.{Color, BasicStroke}
import java.io.File
import org.scalatest.{FlatSpec, FunSpec, Matchers}

class ProcessSheetJobSpec extends FlatSpec with Matchers {

  val logger = {
    val logDir = new File("target/test-log")
    Files2.mkdirs(logDir)
    new ImgLogger(logDir)
  }
  val processor = new SheetProcessor

  def markedTestImage(img: BufferedImage) {

    it should "detect circles" in {
      // WHEN detect circles in marked-test-sample
      val circles = testFindCircles(img)

      // THEN there are 4 x 10 circles
      circles should have size (4 * 10) // 4 columns, 10 rows
    }

    it should "sort circles into rows and columns" in {
      // WHEN detect circles then sort the circles into Rows
      val rows = testFindRows(img)

      // THEN returns a sequence of 10 Rows each with 4 choices
      rows should have size 10
      rows.foreach(r => {
        r should have size 4
        val xValues = r.map(_.getCenter.getX)
        isMonotonicallyIncreasing(xValues) should be(true)
      })
    }

    it should "detect which circles are marked" in {
      // WHEN detect marks
      val rows = testFindMarks(img).toList

      // THEN return a sequence of 10 rowseach with one answer marked true
      assert (rows match {
        case Seq(
          Seq(true, false, false, false),
          Seq(false, true, false, false),
          Seq(false, false, true, false),
          Seq(false, false, false, true),
          Seq(false, true, false, false),
          Seq(false, false, true, false),
          Seq(true, false, false, false),
          Seq(false, true, false, false),
          Seq(false, false, true, false),
          Seq(false, false, false, true)
        ) => true
        case _ => false
      })
    }
  }

  "A sample exam (generated by a computer)" should behave like markedTestImage(TestImgFactory.markedTestSample)

  "A sample exam (marked with pencil)" should behave like markedTestImage(TestImgFactory.pencilTestSample)





  /**
   * Steps 1 - 3 only (for testing steps 1 through 3)
   */
  private def testFindCircles(bufImg: BufferedImage): List[Contour] = {
    val binary = processor.convertToBinary(bufImg)
    val noNoise = processor.removeNoise(binary)
    val circles = processor.findCircles(noNoise)
    logger.debug(bufImg, "original")
    logger.debugBinary(binary, "binary")
    logger.debugBinary(noNoise, "no-noise")
    logger.debug(drawCirclesOnImage(noNoise, circles), "detect-circles-test")
    circles
  }

  /**
   * Steps 1 - 4 only (for testing steps 1 through 4)
   */
  private def testFindRows(bufImg: BufferedImage): Seq[Seq[EllipseRotated_F64]] = {
    val binary = processor.convertToBinary(bufImg)
    val noNoise = processor.removeNoise(binary)
    val circles = processor.findCircles(noNoise)
    processor.findRows(noNoise, circles)
  }

  /**
   * Steps 1 - 5 only (for testing steps 1 through 5)
   */
  private def testFindMarks(bufImg: BufferedImage): Seq[Seq[Boolean]] = {
    val binary = processor.convertToBinary(bufImg)
    val noNoise = processor.removeNoise(binary)
    val circles = processor.findCircles(noNoise)
    val rows = processor.findRows(noNoise, circles)
    processor.findMarks(noNoise, rows)
  }

  private def drawCirclesOnImage(image: ImageUInt8, contours: List[Contour]): BufferedImage = {
    val bufImg = convertBinaryToColor(image)
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

  private def convertBinaryToColor(image: ImageUInt8): BufferedImage = {
    val bufImg = VisualizeBinaryData.renderBinary(image, null)
    val color = new BufferedImage(bufImg.getWidth, bufImg.getHeight, BufferedImage.TYPE_INT_RGB)
    val g = color.getGraphics
    g.drawImage(bufImg, 0, 0, null)
    g.dispose()
    color
  }

  private def isMonotonicallyIncreasing(doubles: Seq[Double]): Boolean = {
    var previous = Double.MinValue
    !doubles.exists(d => {
      val ret = d <= previous
      previous = d
      ret
    })
  }
}
