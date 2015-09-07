package org.senz.MotionRecognition;

import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.senz.MotionRecognition.filter.TrainingFilter;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.filters.*;
import weka.core.converters.ConverterUtils.DataSource;
import org.senz.MotionRecognition.filter.ClassificationFilter;

public class MotionRecognition {

    // Types of motion status in each record tuple.
    private FastVector mStatus;
    // Types of one record tuple.
    private FastVector mRecords;
    // Types of one processed record tuple.
    private FastVector mProcessed;

    // Weka Instances from loaded motion record.
    private Instances  mRecordsDataset;
    // Weka Instances from processed motion record.
    private Instances  mProcessedDataset;

    // Weka filter for motion predicting dataset which status was unknown.
    private ClassificationFilter mClassifyFilter;
    // Weka filter for motion training dataset which status were known.
    private TrainingFilter mTrainFilter;

    // Classifier for classifying
    private Classifier mCls;

    private boolean mTrainingSampleFlag = false;

    public MotionRecognition(){

        this.createStatusVector();
        this.createRecordVector();
        this.createDatasetVector();

        this.mRecordsDataset   = new Instances("records", this.mRecords, 0);
        this.mRecordsDataset.setClassIndex(this.mRecordsDataset.numAttributes() - 1);
        this.mProcessedDataset = new Instances("processed", this.mProcessed, 0);
        this.mProcessedDataset.setClassIndex(this.mProcessedDataset.numAttributes() - 1);

        this.mClassifyFilter   = new ClassificationFilter();
        this.mTrainFilter      = new TrainingFilter();

        System.out.println("Initiation is over!");
    }

    private void createStatusVector() {
        this.mStatus = new FastVector();
        this.mStatus.addElement("Walking");
        this.mStatus.addElement("Running");
        this.mStatus.addElement("Sitting");
        this.mStatus.addElement("Driving");
        this.mStatus.addElement("Riding");
        this.mStatus.addElement("Unknown");
    }
    
    private void createRecordVector() {
        this.mRecords = new FastVector();
        this.mRecords.addElement(new Attribute("timestamp"));
        this.mRecords.addElement(new Attribute("acc_x"));
        this.mRecords.addElement(new Attribute("acc_y"));
        this.mRecords.addElement(new Attribute("acc_z"));
        this.mRecords.addElement(new Attribute("accuracy"));
        this.mRecords.addElement(new Attribute("status", this.mStatus));
    }

    private void createDatasetVector() {
        this.mProcessed = new FastVector();
        this.mProcessed.addElement(new Attribute("mean"));
        this.mProcessed.addElement(new Attribute("std"));
        this.mProcessed.addElement(new Attribute("max"));
        this.mProcessed.addElement(new Attribute("min"));
        this.mProcessed.addElement(new Attribute("status", this.mStatus));
    }

    private Instance rawRecordFormatter(Long timestamp, double acc_x, double acc_y, double acc_z, int accuracy, String status) {
        double[] vals = new double[this.mRecordsDataset.numAttributes()];
        vals[0] = timestamp;
        vals[1] = acc_x;
        vals[2] = acc_y;
        vals[3] = acc_z;
        vals[4] = accuracy;
        vals[5] = this.mStatus.indexOf(status);
        return new Instance(1.0, vals);
    }

    private void loadArffFile(String file) throws Exception{
        this.mRecordsDataset = DataSource.read(file);
        this.mRecordsDataset.setClassIndex(this.mRecordsDataset.numAttributes() - 1);
        System.out.println(this.mRecordsDataset);
        // todo: validate whether label attribute exists.
    }

    private void loadRecordFile(String file) throws Exception {
        FileReader     fileReader = new FileReader(new File(file));
        BufferedReader br         = new BufferedReader(fileReader);
        String         line       = null;
        while ((line = br.readLine()) != null) {
            JSONObject obj   = new JSONObject(line);
            JSONArray values = obj.getJSONArray("values");
            double acc_x     = values.getDouble(0);
            double acc_y     = values.getDouble(1);
            double acc_z     = values.getDouble(2);
            Long   timestamp = obj.getLong("timestamp");
            int    accuracy  = obj.getInt("accuracy");
            String status;
            try {
                status  = obj.getString("status");
                this.mTrainingSampleFlag = true;
            } catch (JSONException e){
                status  = "Unknown";
                this.mTrainingSampleFlag = false;
            }
            Instance record  = this.rawRecordFormatter(timestamp, acc_x, acc_y, acc_z, accuracy, status);
            this.mRecordsDataset.add(record);
        }
//        this.mRecordsDataset.setClassIndex(this.mRecordsDataset.numAttributes() - 1);
    }

    private void loadModel(String model_file) throws Exception {
        this.mCls = (Classifier) SerializationHelper.read(model_file);
    }

    public void train(String labeled_file, String model_dir) {
        try {
            // load record file into weka instances.
            this.loadRecordFile(labeled_file);
            // initializing the filter once with record set.
            this.mTrainFilter.setInputFormat(this.mRecordsDataset);
            // configures the Filter based on train instances and returns filtered instances.
            Instances processedData = Filter.useFilter(this.mRecordsDataset, this.mTrainFilter);
            processedData.setClassIndex(processedData.numAttributes() - 1);

            System.out.println(processedData);

            LibSVM svm = new LibSVM();
            svm.buildClassifier(processedData);
            SerializationHelper.write(model_dir + "LibSVM.model", svm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String classify(String unlabeled_file, String model_file) {
//        System.out.println(this.mClassifyFilter.globalInfo());
        try {
            // load record file into weka instances.
            this.loadRecordFile(unlabeled_file);
            // load model file into weka classifier.
            this.loadModel(model_file);
            // initializing the filter once with record set.
            this.mClassifyFilter.setInputFormat(this.mRecordsDataset);
            // configures the Filter based on train instances and returns filtered instances.
            Instances processedData = Filter.useFilter(this.mRecordsDataset, this.mClassifyFilter);
            processedData.setClassIndex(processedData.numAttributes() - 1);
            // classification.
            double result = this.mCls.classifyInstance(processedData.firstInstance());

            System.out.println("Prediction is " + (String) this.mStatus.elementAt((int) result));

            return (String) this.mStatus.elementAt((int) result);
        } catch (Exception e){
            e.printStackTrace();
            return "Unknown";
        }
    }

    public static void main(String[] args) {
        MotionRecognition mr;
        System.out.println("Hello World!");
        mr = new MotionRecognition();
//        mr.classify("train6.txt", "LibSVM.model");
        mr.train("train6.txt", "");
        mr.classify("train6.txt", "LibSVM.model");
    }
}
