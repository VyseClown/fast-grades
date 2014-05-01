# Auto Grader

![TravisCI status](https://travis-ci.org/gilday/fast-grades.png?branch=master)

[Production Instance](http://gilday.github.io/fast-grades)

Automated multiple choice grading application based on BoofCV (computer vision).

Developed with agility for JHU 605.407 - Agile Software Development Methods with Professor Will Menner

## Project Proposal

Will use agile methods and practices to develop a multiple choice grading system that uses computer vision to grade scanned multiple choice exams.

My friends asked me if I could make such a tool. They are public school teachers and they dislike their current grading system.

Their current system is specialized hardware that scans proprietary exam sheets. The biggest problem with this system is the manual scanning process: the exams are fed through the scanning hardware sequentially. The second problem with this system is its cost. Newer hardware models can scan batches of exams, but the are very expensive. Furthermore, the exam sheets that students mark their answers on is a proprietary format and it costs significantly more than the paper it's printed on.

My friends want to use a combination of software and the school's existing Xerox machine to implement an affordable, automated grading system. Their Xerox machine is capable of scanning stacks of papers and it emails the digitized pages to teachers' inboxes. Once the exams have been digitized, teachers will use the proposed computer vision software to process the exams. Furthermore, the Xerox machine is also a printer so teachers will use it to print exam sheets instead of buying the costly, proprietary sheets used in the current system.

I am proposing to build the exam processing software and the exam sheet format for this system. My friends have agreed to play the role of the Product Owner. I will meet with the Product Owner to create a prioritized backlog for a minimally operational system. I intend to build on FOSS computer vision software to deliver the minimally functional first release. After the first release of this system, we can start to incorporate teacher feedback to grow the backlog and determine the features and enhancements we would like to have in subsequent releases.


## Simple Model

I created this simple model prior to the story writing workshop to drive discussion

![simple model](https://raw.github.com/gilday/fast-grades/master/docs/simple-model-small.jpeg)

## Agile Practices

**Automated Build** Using Scala Build Tool ([sbt](http://www.scala-sbt.org/)) to automatically build project artifacts from source

**Unit Tests** Using [ScalaTest](fhttp://www.scalatest.org/) for unit testing

**TDD** I'm a big fan of test driven development so I will write my tests at the same time (or before) I write new code. To change existing functionality, I will first change the test to verify the new functionality. 
Currently, the automated testing is done in ScalaTest for the backend code. The front-end JavaScript is not unit tested because I am unfamiliar with the tricky world of JavaScript automated testing and so far the front-end is relatively simple in comparison to the complexity of the backend. 

**Continuous Integration** See [Fast Grades on Travis CI](https://travis-ci.org/gilday/fast-grades). With every commit to the `master` branch, a Github post-commit hook tells Travis CI to build the project and run the automated tests.

**Continuous Deployment** The [Travis CI yaml file](https://github.com/gilday/fast-grades/blob/master/.travis.yml) configures Travis CI to push the both the front-end and back-end code to their respective hosting platforms. The front-end web code is hosted on Github pages. The backend scala code is hosted on Heroku at http://fast-grades.herokuapp.com/

## Definition of Done

In general, I will define the definition of done to be the following. 

All commits should be merged into the master branch. By committing the master branch on GitHub, the continuous integration system will run on all the automated tests.
Since the master branch automatically deploys the front and back end, I will quickly verify that the production system behaves as expected.

## Product Backlog

[The backlog is currently implemented as a Google Spreadsheet](https://docs.google.com/spreadsheet/ccc?key=0AteH1qcLQxi-dGxkRHR2YjdQWEMxLWx2WWVLZ05HZlE&usp=sharing). The product owners have the ability to add comments to the spreadsheet. 
The spread sheet is influenced by the template in the class resources. The sheet the following worksheets

1. Release Plan: lists the planned sprints in chronological order with dates
2. Product Backlog: All tickets prioritized
3. Sprint Backlogs: one worksheet for each Sprint which lists the planned and completed stories for that sprint.

Before I implemented the product backlog spreadsheet, I tried to implement the backlog just using Github Milestones and Issues. These Github features were nice because they were well integrated with the Github platform but the Issues and Milestones were not easy to organize in a manner that is consistent with class discussions. 
Instead, I now keep the product backlog in the aforementioned product backlog spreadsheet but I also input the stories for the current sprint into Github Issues. I group the [Issues](https://github.com/gilday/fast-grades/issues?state=open) into [Milestones](https://github.com/gilday/fast-grades/issues/milestones) (which correspond to each sprint). By keeping the current sprint's stories as Issues in Github, I can associate each code commit with the current issue. These Issues are also more comfortable to work with while coding.

### Story Writing Workshop

![Story Writing Workshop](https://raw.github.com/gilday/fast-grades/master/docs/story-workshop.png)

The first tasks for this project were part of the feasability study. After concluding that the project is feasible, I conducted a story writing workshop with the product owners. The stories that came out of the workshop were very large. Many of the stories depend on a core set of stories; the core set is "Optical Grading" theme. 
The stories in the "Optical Grading" theme are also large and I will focus on this theme first. I will defer estimating stories in other themes until this core theme is finished. 

## References

Bookmarks and references

* [Yo + Travis + Github Pages](https://coderwall.com/p/ndaemg) auto deploy a built yeoman project to gh-pages
* [sbt + Grunt](https://github.com/parkotron/sbt-grunt-task) to call Grunt build from sbt
* [Convert PDF](http://www.icesoft.org/wiki/display/PDF/Converting+PDF+Page+Renderings) apache licensed solution for PDF -> BufferedImage
* [JHU VMs](https://support.cs.jhu.edu/wiki/Requesting_A_Virtual_Machine) potential deployment platform