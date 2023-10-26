package framework;

import classification.HubISC;
import java.io.File;
import organizestream.DataSet;

public class Main {

    public static void main(String[] args) {

        String path = null;

        try {
            if (System.getProperty("os.name").equals("Linux")) {
                path = new File(".").getCanonicalPath() + "/dataset/";
            } else {
                path = new File(".").getCanonicalPath() + "\\dataset\\";
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        
        //Executing EVISClass Framework
        
        //Data stream simulator
        DataSet dataset = new DataSet();
        dataset.loadDataSet(path + "CIFAR20classes10000instances.data");
        String newFilePath = dataset.generateNewDataSet(20, 25, true, true);

        
        HubISC hub = new HubISC(10, 0.7, 2, 15, 500);
        hub.execute(newFilePath);
        //****************************************************************
    }

}
