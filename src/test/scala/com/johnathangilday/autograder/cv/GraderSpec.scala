package com.johnathangilday.autograder.cv

import org.scalatest.{FlatSpec, Matchers}
import com.johnathangilday.autograder.testutils.TestImgFactory

class GraderSpec extends FlatSpec with Matchers with GraderComponentImpl with SheetProcessorComponentImpl {

  it should "use the sheet processor to grade an exam" in {
    val img = TestImgFactory.markedFile

    val rows = grader.grade(img)
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
