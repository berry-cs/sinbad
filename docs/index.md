<div style="float: right;"><img src="http://cs.berry.edu/sinbad/sinbad-logo-thumbnail.png" alt="Sinbad logo" /></div>

**Sinbad** is a software library for [Java](http://java.com), [Python](http://www.python.org), and [Racket](http://racket-lang.org/) that makes it very easy to uniformly access online data sources provided in standard formats (CSV, JSON, XML).

If you are a novice programmer, you probably should go through the [tutorials](#tutorials) below after installing Sinbad. Experienced programmers may benefit more from the [quick start guides](#quick-start). You will also find links to a [quick reference](#quick-reference) and detailed [API documentation](#api-reference) below.

For a list of data sources, visit the [Bazaar <img src="bazaar.png" alt="bazaar" />](bazaar).



----

## Installation
Instructions for:
* [Java/Processing](install-java)
* [Python](install-python)
* [Racket](install-racket)

## Quick Start
These links provide a concise introduction to the most prominent features of the Sinbad library. Use these if you are experienced in the programming language that you are using:

* [Java](quick-start-java)
* [Python](quick-start-python)
* [Racket](quick-start-racket)

## Tutorials
These pages provide a more leisurely and explanatory coverage of the features of the Sinbad library in the respective language. These are good if you are learning to program for the first time and are interested in using Sinbad to access data.

* **Java/Processing**
  + [Introduction](https://github.com/berry-cs/sinbad/blob/master/tutorials/java/welcome01.md)
  + [Fetching Primitive Type Arrays](https://github.com/berry-cs/sinbad/blob/master/tutorials/java/welcome02-arr.md)
  + [Fetching Objects](https://github.com/berry-cs/sinbad/blob/master/tutorials/java/welcome02-obj.md)
  + [Fetching Arrays of Objects](https://github.com/berry-cs/sinbad/blob/master/tutorials/java/welcome03-objs.md)
  
* **Python**
  + [Introduction](https://github.com/berry-cs/sinbad/blob/master/tutorials/python/welcome01.md)
  + [Fetching Simple Lists](https://github.com/berry-cs/sinbad/blob/master/tutorials/python/welcome02_list.md)
  + [Fetching Dictionary Objects](https://github.com/berry-cs/sinbad/blob/master/tutorials/python/welcome02-dict.md)
  + [Fetching Lists of Dictionary Objects](https://github.com/berry-cs/sinbad/blob/master/tutorials/python/welcome03-objs.md)

* **Racket**

  This set of tutorials/guided activities roughly follows the sequence of topics in [How to Design Programs, 2nd edition](http://www.ccs.neu.edu/home/matthias/HtDP2e/).
  + [Expressions & Weather Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather1-expr.md) (simple expressions/data)
  + [Functions on Weather Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather2-func.md) (atomic data)
  + [Describing Differentials](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather3-interv.md) (intervals)
  + [Bike Share Usage Fees](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/bikeshare1-usagefees.md) (enumerations)
  + [Structures with Weather Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather4-struct.md) (simple structures)
  + [Latitude/Longitude Functions](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/latlong-utils.md) (simple structures)
  + [Nested Structures with Weather Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather5-nested.md) (nested structures)
  + [Bike Share Trips and Users](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/bikeshare2-trips.md) (itemizations/nested structures)
  + [Simple Lists with Earthquake Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/quakes1-atomlist.md) (lists of strings/numbers)
  + [Bike Share User Ages](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/bikeshare3-ages.md) (lists of atomic data/non-empty lists)
    + [Bike Share: Handling Large Amounts of Data](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/bikeshare-sampling.md)
  + [Mapping Quakes](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/quakes2-liststruct.md) (lists of structures)
  + [Weather Stations and Observations](https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/weather6-liststruct.md) (lists of structures)
  


## Quick Reference
These may be used as "cheatsheets".

* [Java](quick-java)
* [Python](quick-python)
* [Racket](quick-racket)


## API Reference

* [Java](api-datasource-java)
* [Python](api-datasource-python)
* [Racket](api-datasource-racket)

-----

## Publications & Presentations

* [ITiCSE '16 paper](https://doi.org/10.1145/2899415.2899437): "A Generic Framework for Engaging Online Data Sources in
Introductory Programming Courses" [ [local copy](http://cs.berry.edu/sinbad/iticse16-paper.pdf)] [ [slides](http://cs.berry.edu/sinbad/iticse16-slides.pdf)]
* [SPLASH-E '15](http://2015.splashcon.org/track/splash2015-splash-e#event-overview) workshop presentation: "A Generic Framework for Engaging Online Data Sources in Introductory Programming Courses"
* [SIGCSE '14](https://dl.acm.org/citation.cfm?id=2544280&CFID=633189652&CFTOKEN=58804699) poster: "Towards engaging big data for CS1/2" [ [local copy](http://cs.berry.edu/sinbad/bigdata-poster.pdf)]

### About the name

*SINBAD* was selected as a rough acronym for **S**tructure **IN**ference and **B**inding **A**utomatically to **D**ata -- the main idea behind the library initially developed in Java. The name was also selected to invoke the sense of adventure, magical experience, and fantastic achievement reminiscent of the voyages of Sinbad the Sailor in the stories of the Arabian Nights. Through this library, we hope to help students experience some of the adventure and magic of Computer Science early on in their programming courses.


## Contact

Bug reports, suggestions for improvement, code contributions, or requests for help using the library are very welcome.

Use [this form](feedback) or email: [sinbad.data@gmail.com](mailto:sinbad.data@gmail.com).



