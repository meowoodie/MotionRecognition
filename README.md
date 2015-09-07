MotionRecognition
===
It is an sdk (.jar) of motion recognition with weka in Java.
The major goal of this code is training and classifying motion raw data in Android code. (or other Java project)
- The project was orgnized in basic Maven structure in folder "***motion-recognition-sdk***".
- Folder "***dependencies***" stores all dependent jar including *weka.jar* and *libsvm.jar*. You should either add them into the classpath or install them into local maven repositery.
- Folder "***resources***" provides some files which you may need in a demo running.

Usage
---


Todo
---
The project is just a proto-type used in Android program (or other Java project). 
There are still a lot of problems which deserve to be promoted in this project.
I will improve the details of this code continuously.
- All involved definition of FastVectors could have assembled in an additional class.(I have left a empty class named "MotionVectors" at root directory for further modification)
- Input of interface of classification and training should have converted from file directory to file object. It might be more friendly for developer because of permission of file writting & reading.
- It needs its own exception class.(at present, I have cought all possible exception).
- heretofore, it contains only one classifier which is libSVM. And we need more classifier for option. And a mechanism for switching classifier easily is necessary.
- We should add a new interface for providing option to the weka classifiers.

