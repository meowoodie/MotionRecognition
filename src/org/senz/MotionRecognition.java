package org.senz;

import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import weka.core.*;
import weka.filters.unsupervised.attribute.*;
import weka.filters.*;
//import weka.core.Attribute;
//import weka.core.FastVector;
//import weka.core.Instance;
//import weka.core.Instances;
import org.senz.*;

public class MotionRecognition {

    private FastVector mRecords;
    private FastVector mStatus;
    private Instances  mRecordsDataset;
    private ClassificationFilter mFilter;
//    private Instances  mUnknownDataset;
//    private Instances  mTrainingDataset;

    private boolean    mTrainingSampleFlag;

    public MotionRecognition(){

        this.createStatusVector();
        this.createRecordVector();

        this.mRecordsDataset = new Instances("records", this.mRecords, 0);
        this.mFilter         = new ClassificationFilter();

        System.out.println("Initiation is over!");
    }

    private void createStatusVector(){
        this.mStatus = new FastVector();
        this.mStatus.addElement("Walking");
        this.mStatus.addElement("Running");
        this.mStatus.addElement("Sitting");
        this.mStatus.addElement("Driving");
        this.mStatus.addElement("Riding");
        this.mStatus.addElement("Unknown");
    }
    
    private void createRecordVector(){
        this.mRecords = new FastVector();
        this.mRecords.addElement(new Attribute("timestamp"));
        this.mRecords.addElement(new Attribute("acc_x"));
        this.mRecords.addElement(new Attribute("acc_y"));
        this.mRecords.addElement(new Attribute("acc_z"));
        this.mRecords.addElement(new Attribute("accuracy"));
        this.mRecords.addElement(new Attribute("status", this.mStatus));
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

    private void readRecordFile(String file) throws Exception{
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
        this.mRecordsDataset.setClassIndex(this.mRecordsDataset.numAttributes() - 1);
    }

    public boolean run(String file) {
        try {
            this.readRecordFile(file);
        } catch (Exception e){
            System.out.println(e);
        }

        System.out.println(this.mFilter.globalInfo());

        try {
            // initializing the filter once with record set
            this.mFilter.setInputFormat(this.mRecordsDataset);
            // configures the Filter based on train instances and returns filtered instances
            Instances processedData = Filter.useFilter(this.mRecordsDataset, this.mFilter);

        } catch (Exception e){
            System.out.println(e);
        }
//        try {
//            this.mFilter.setInputFormat(this.mRecordsDataset);
//            System.out.println("cao!");
//            for (int i = 0; i < this.mRecordsDataset.numInstances(); i++) {
//                this.mFilter.input(this.mRecordsDataset.instance(i));
//            }
//            System.out.println("madan!");
////            if (!this.mFilter.batchFinished()) {
////                System.out.println("fuck!");
////                return false;
////            }
//            System.out.println("riri!0");
////            this.mFilter.batchFinished();
//            System.out.println("riri!1");
//            Instances newData = this.mFilter.getOutputFormat();
//            System.out.println("riri!2");
//            Instance processed;
//            System.out.println("riri!3");
//            while ((processed = this.mFilter.output()) != null) {
//                System.out.println("xianren!");
//                System.out.println(processed);
//                newData.add(processed);
//            }
//            System.out.println(newData);
//        } catch (Exception e){
//            System.out.println(e);
//        }

        return true;
    }

    public static void main(String[] args) {
        MotionRecognition motion_recognition;
        System.out.println("Hello World!");
        motion_recognition = new MotionRecognition();
//        try {
//            motion_recognition.readRecordFile("train6.txt");
//        } catch (Exception e){
//            System.out.println(e);
//        }
        motion_recognition.run("train6.txt");
//        System.out.println(motion_recognition.mRecordsDataset);
    }
}
