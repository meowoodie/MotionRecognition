package org.senz.MotionRecognition.filter;

import weka.core.*;
import weka.core.Capabilities.*;
import weka.filters.*;
import org.senz.MotionRecognition.utils.StatisticFeature;
import java.lang.reflect.Array;
import java.util.*;

public class ClassificationFilter extends SimpleBatchFilter {

    private FastVector classificationVector;
    private FastVector statusVector;

    public ClassificationFilter(){
        this.createStatusVector();
        this.createClassificationVector();
//        this.setOutputFormat(new Instances("classification", this.classificationVector, 0));
    }

    private void createStatusVector() {
        this.statusVector = new FastVector();
        this.statusVector.addElement("Walking");
        this.statusVector.addElement("Running");
        this.statusVector.addElement("Sitting");
        this.statusVector.addElement("Driving");
        this.statusVector.addElement("Riding");
        this.statusVector.addElement("Unknown");
    }

    private void createClassificationVector(){
        this.classificationVector = new FastVector();
        this.classificationVector.addElement(new Attribute("mean"));
        this.classificationVector.addElement(new Attribute("std"));
        this.classificationVector.addElement(new Attribute("max"));
        this.classificationVector.addElement(new Attribute("min"));
        this.classificationVector.addElement(new Attribute("status", this.statusVector));
    }

//    private Instance motionFeatureFormatter(double acc_x, double acc_y, double acc_z) {
//        double[] vals = new double[new Instances("classification", this.classificationVector, 0).numAttributes()];
//        return
//    }

	public String globalInfo() {
		return "A batch filter that convert the format of log file to which classifier can process.";
	}

	protected Instances determineOutputFormat(Instances inputFormat) {
        return new Instances("classification", this.classificationVector, 0);
	}

	protected Instances process(Instances inst) {
		Instances result = new Instances("classification", this.classificationVector, 0);
//        result.setClassIndex(result.numAttributes() - 1);
        double[] gravity = new double[inst.numInstances()];
        // Process every instance one by one.
		for (int i = 0; i < inst.numInstances(); i++) {
            gravity[i] = Math.pow(inst.instance(i).value(1), 2)
                    + Math.pow(inst.instance(i).value(2), 2)
                    + Math.pow(inst.instance(i).value(3), 2);
		}
        double[] values = new double[result.numAttributes()];
        values[0] = StatisticFeature.mean(gravity);
        values[1] = StatisticFeature.sdev(gravity);
        values[2] = StatisticFeature.max(gravity);
        values[3] = StatisticFeature.min(gravity);
        result.add(new Instance(1.0, values));
		return result;
	}

}
