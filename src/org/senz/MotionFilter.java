package org.senz;

import weka.core.*;
import weka.core.Capabilities.*;
import weka.filters.*;

class ClassificationFilter extends SimpleBatchFilter {

    private FastVector classificationVector;

    public ClassificationFilter(){
        this.createClassificationVector();
//        this.setOutputFormat(new Instances("classification", this.classificationVector, 0));
    }

    private void createClassificationVector(){
        this.classificationVector = new FastVector();
        this.classificationVector.addElement(new Attribute("average"));
        this.classificationVector.addElement(new Attribute("std"));
        this.classificationVector.addElement(new Attribute("max"));
        this.classificationVector.addElement(new Attribute("min"));
    }

    private Instance motionFeatureFormatter(double acc_x, double acc_y, double acc_z) {
        double[] vals = new double[new Instances("classification", this.classificationVector, 0).numAttributes()];

    }

	public String globalInfo() {
		return "A batch filter that convert the format of log file to which classifier can process.";
	}

	protected Instances determineOutputFormat(Instances inputFormat) {
		Instances result = new Instances("classification", this.classificationVector, 0);
		return result;
	}

	protected Instances process(Instances inst) {
		Instances result = new Instances(determineOutputFormat(inst), 0);
        double average = 0;
        double max     = -999999999;
        double min     = 999999999;
        double std     = 0;
        // Process every instance one by one.
		for (int i = 0; i < inst.numInstances(); i++) {
            double gravity = inst.instance(i).value(1)^2 + inst.instance(i).value(2)^2 + inst.instance(i).value(3)^2;
            // TODO: convert data struct to array.
            average += gravity;
            if (gravity >= max){
                max = gravity;
            }
            if (gravity <= min){
                min = gravity;
            }
            // Initiation of Instance of result.

//			for (int n = 0; n < result.numAttributes(); n++) {
//                values[n] = inst.instance(i).value(n);
//            }

//			values[values.length - 1] = i;
//			result.add(new Instance(1, values));
		}
        double[] values = new double[result.numAttributes()];
        values[0] = average/inst.numInstances();
        values[1] = max;
        values[2] = min;
        values[3] = std;
        result.add(new Instance(1, values));
		return result;
	}

	public static void main(String[] args) {

	}

}
