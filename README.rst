Type System for Hybrid Android Apps; Detecting Interoperation Bugs
===========

Introduction
============
Copyright (c) 2019, KAIST

This type system for hybrid Android apps is designed to detect interoperation bugs, and implemented on top of `SAFE 2.0`_.

.. _SAFE 2.0: https://github.com/kaist-plrg/safe

Requirements
============

* J2SDK 1.8.  See http://java.sun.com/javase/downloads/index.jsp
* Scala 2.12.  See http://scala-lang.org/download
* sbt version 0.13 or later.  See http://www.scala-sbt.org
* Bash version 2.5 or later, installed at /bin/bash.  See http://www.gnu.org/software/bash/

Instructions
============
Install: ::

    sbt compile

Extract JavaScript code and its bridges from Android apps via the front-end of `HybriDroid`_: ::
    
    java -jar lib/hybridroid_types.jar <apk-file> wala.properties <bridge-output-file>

.. _HybriDroid: https://github.com/SunghoLee/HybriDroid

Type-check the extracted JavaScript code: ::

    export SAFE_HOME=`pwd`
    ./bin/safe androidCheck <bridge-output-file>

Authors
============

* `Sora Bae`_ 
* `Sungho Lee`_
* `Sukyoung Ryu`_

.. _Sora Bae: https://github.com/sorabae
.. _Sungho Lee: https://github.com/SunghoLee
.. _Sukyoung Ryu:  https://github.com/sukyoung

