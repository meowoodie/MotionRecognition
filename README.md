MotionRecognition
===
It is an sdk (.jar) of motion recognition with weka in Java.
The major goal of this code is training and classifying motion raw data in Android code (or other Java project).
It used to recognize the most possible motion from a series of sensor data.
- The project was orgnized in basic Maven structure in folder "***motion-recognition-sdk***".
- Folder "***dependencies***" stores all dependent jar including *weka.jar* and *libsvm.jar*. You should either add them into the classpath or install them into local maven repositery.
- Folder "***resources***" provides some files which you may need in a demo running.

Usage
---
First of all you need add jar file "motion-recognition-sdk-1.0-SNAPSHOT" and related dependent jar (weka.jar & libsvm.jar) into your project dependencies.
Following is a demo for training and classification.

# Training

```java
import org.senz.MotionRecognition.*
// Instantiation of Motion recognition
MotionRecognition mr;
mr = new MotionRecognition();
// Training
mr.train("train6.txt", "/");
```

the first parameter of method "train" is a file directory of training sample. Every tuple in this file is a json which contains informations including timestamp, acc data, accuracy and status(etc. labeled type).
the second parameter is a prospective directory for generated model file after training.

# Classification

```java
import org.senz.MotionRecognition.*
// Instantiation of Motion recognition
MotionRecognition mr;
mr = new MotionRecognition();
// Classification
String motion_type;
motion_type = mr.classify("train6.txt", "LibSVM.model");
```

the first parameter of method "classify" is a file directory of unlabeld sensor data sample. Every tuple in this file is a json which contains informations including timestamp, acc data, accuracy and so on("status" attribute is optional).
the second parameter is a file directory of model file which used to classify motion type.

To do
---
The project is just a proto-type used in Android program (or other Java project). 
There are still a lot of problems which deserve to be promoted in this project.
I will improve the details of this code continuously.

- All involved definition of FastVectors could have assembled in an additional class.(I have left a empty class named "MotionVectors" at root directory for further modification)
- Input of interface of classification and training should have converted from file directory to file object. It might be more friendly for developer because of permission of file writting & reading.
- It needs its own exception class.(at present, I have cought all possible exception).
- heretofore, it contains only one classifier which is libSVM. And we need more classifier for option. And a mechanism for switching classifier easily is necessary.
- We should add a new interface for providing option to the weka classifiers.

