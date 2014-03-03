package com.johnathangilday.autograder

import boofcv.alg.feature.shapes.ShapeFittingOps
import boofcv.alg.filter.binary.Contour
import boofcv.gui.binary.VisualizeBinaryData
import boofcv.gui.feature.VisualizeShapes
import boofcv.struct.image.ImageUInt8
import com.johnathangilday.autograder.model.Row
import com.johnathangilday.autograder.testutils.TestImgFactory
import com.johnathangilday.autograder.utils.Files2
import java.awt.image.BufferedImage
import java.awt.{Color, BasicStroke}
import java.io.File
import org.scalatest.{FunSpec, Matchers}

class ProcessSheetJobSpec extends FunSpec with Matchers {

  val logger = {
    val logDir = new File("target/test-log")
    Files2.mkdirs(logDir)
    new ImgLogger(logDir)
  }
  val processor = new SheetProcessor

  describe("process one sheet of marked answers") {

    it("should detect circles") {
      // GIVEN happy path marked-test-sample.jpg in test resources
      val img = TestImgFactory.markedTestSample

      // WHEN detect circles in marked-test-sample
      val circles = testFindCircles(img)

      // THEN there are 4 x 10 circles
      circles should have size (4 * 10) // 4 columns, 10 rows
    }
  }

  it("should sort circles into rows and columns") {
    // GIVEN marked-test-sample.jpg in test resources
    val img = TestImgFactory.markedTestSample

    // WHEN detect circles then sort the circles into Rows
    val rows = testFindRows(img)

    // THEN returns a sequence of 10 Rows each with 4 choices
    rows should have size 10
    val problemNumbers = rows.map(_.problem)
    isIncreasingByOne(problemNumbers) should be(true)
    rows.foreach(r => {
      r.circles should have size 4
      val ellipses = r.circles.map(c => ShapeFittingOps.fitEllipse_I32(c.external, 0, false, null).shape)
      val xValues = ellipses.map(_.getCenter.getX)
      isMonotonicallyIncreasing(xValues) should be(true)
    })
  }

  /**
   * Steps 1 - 3 only (for testing steps 1 through 3)
   */
  private def testFindCircles(bufImg: BufferedImage): List[Contour] = {
    val binary = processor.convertToBinary(bufImg)
    val noNoise = processor.removeNoise(binary)
    val circles = processor.findCircles(noNoise)
    logger.debug(drawCirclesOnImage(noNoise, circles), "detect-circles-test")
    circles
  }

  /**
   * Steps 1 - 4 only (for testing steps 1 through 4)
   */
  private def testFindRows(bufImg: BufferedImage): Seq[Row] = {
    val binary = processor.convertToBinary(bufImg)
    val noNoise = processor.removeNoise(binary)
    val circles = processor.findCircles(noNoise)
    processor.findRows(noNoise, circles)
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

  private def isIncreasingByOne(ints: Seq[Int]): Boolean = {
    var previous = ints.min - 1
    ints.foreach(i => {
      if (i != previous + 1)
        return false
      previous = i
    })
    true
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
