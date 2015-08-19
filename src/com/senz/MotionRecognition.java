package com.senz;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MotionRecognition {
	public static void main(String[] args) {
		FastVector atts;
		Instances  data;
		
		// Set up Motion basic attributes
		atts = new FastVector();
		// average value of motion wave
		atts.addElement(new Attribute("average"));
		// standard deviation along the specified axis
		atts.addElement(new Attribute("standardDeviation"));
		// minimum value of motion wave
		atts.addElement(new Attribute("minimum"));
		// maximum value of motion wave
		atts.addElement(new Attribute("maximum"));
		// labels of motion types
		FastVector labels = new FastVector();
		labels.addElement("Walking");
		labels.addElement("Running");
		labels.addElement("Sitting");
		labels.addElement("Driving");
		labels.addElement("Riding");
		atts.addElement(new Attribute("motionType", labels));
		// Create Instance Object
		data = new Instances("motion", atts, 0);
		System.out.println(System.getProperty("java.class.path"));
		System.out.println("Hello World!");
	}
}
