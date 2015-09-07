package org.senz.MotionRecognition.filter;

import org.senz.MotionRecognition.utils.StatisticFeature;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

public class TrainingFilter extends SimpleBatchFilter {

    private static final int motionWindowSize = 200;
    private FastVector trainingVector;
    private FastVector statusVector;

    public TrainingFilter(){
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
        this.trainingVector = new FastVector();
        this.trainingVector.addElement(new Attribute("mean"));
        this.trainingVector.addElement(new Attribute("std"));
        this.trainingVector.addElement(new Attribute("max"));
        this.trainingVector.addElement(new Attribute("min"));
        this.trainingVector.addElement(new Attribute("status", this.statusVector));
    }

//    private Instance motionFeatureFormatter(double acc_x, double acc_y, double acc_z) {
//        double[] vals = new double[new Instances("classification", this.classificationVector, 0).numAttributes()];
//        return
//    }

    public String globalInfo() {
        return "A batch filter that convert the format of log file to which classifier can process.";
    }

    protected Instances determineOutputFormat(Instances inputFormat) {
        return new Instances("classification", this.trainingVector, 0);
    }

    protected Instances process(Instances inst) {
        Instances result = new Instances("training", this.trainingVector, 0);
        int class_index  = inst.classIndex();

        int i = 0;
        while (i < inst.numInstances() - 1) {
            System.out.println("<" + i + ">");
            int scale = 0;
            while (scale < motionWindowSize) {
                if ((i+scale+1) >= inst.numInstances() ||
                    inst.instance(i+scale).value(class_index) != inst.instance(i+scale+1).value(class_index)) {
                    scale ++;
                    break;
                }
                scale ++;
            }
            double[] gravity = new double[scale];
            // Process every instance one by one.
            for (int j = 0; j < scale; j++) {
                gravity[j] = Math.pow(inst.instance(i+j).value(1), 2)
                        + Math.pow(inst.instance(i+j).value(2), 2)
                        + Math.pow(inst.instance(i+j).value(3), 2);
                System.out.println("[" + (i+j) + "]" + inst.instance(i+j) + " gravity: " + gravity[j]);
            }
            double[] values = new double[result.numAttributes()];
            values[0] = StatisticFeature.mean(gravity);
            values[1] = StatisticFeature.sdev(gravity);
            values[2] = StatisticFeature.max(gravity);
            values[3] = StatisticFeature.min(gravity);
            values[4] = inst.instance(i).value(class_index);
            result.add(new Instance(1.0, values));
            i += scale;
        }
        return result;
    }

}
