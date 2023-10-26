package classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import organizestream.Instance;

//HubISC algorithm 
public class HubISC {

    private ArrayList<String> differentClasses = new ArrayList<String>();
    private ArrayList<Instance> completeDataSet = new ArrayList<Instance>();
    private ArrayList<Instance> buffer = new ArrayList<Instance>();
    private ArrayList<Instance> offlinePhase = new ArrayList<Instance>();
    private ArrayList<String> classesOfflinePhase = new ArrayList<String>();
    private HashMap<String, ClassHubISC> trainingModel = new HashMap<String, ClassHubISC>();

    private int k;
    private double hk;
    private double l;
    private int bufferSize;
    private int instancesOfflinePhase;

    private double maxDistance = Double.MIN_VALUE;

    private Random random = new Random();
    private int runs = 1;

    public HubISC(int k, double hk, double l, int bufferSize, int instancesOfflinePhase) {
        this.k = k;
        this.hk = hk;
        this.l = l;
        this.bufferSize = bufferSize;
        this.instancesOfflinePhase = instancesOfflinePhase;
    }

    public Instance checkNearestPrototype(Instance instance, ArrayList<Instance> prototypes) {

        double distance = Double.MAX_VALUE;
        String classe = "";
        Instance closestPrototype = null;

        for (int i = 0; i < prototypes.size(); i++) {
            double currentDistance = Utils.distance(prototypes.get(i), instance);
            if (currentDistance < distance) {
                distance = currentDistance;
                classe = prototypes.get(i).getClasse();
                closestPrototype = prototypes.get(i);
            }
        }

        return closestPrototype;
    }

    //check nearest centroid class
    public String checkClass(Instance instance) {

        double distance = Double.MAX_VALUE;
        String classe = "";
        Instance closestPrototype = null;

        for (Map.Entry<String, ClassHubISC> entry : trainingModel.entrySet()) {
            ArrayList<Instance> prototypes = entry.getValue().getPrototypes();

            for (int i = 0; i < prototypes.size(); i++) {
                double currentDistance = Utils.distance(prototypes.get(i), instance);
                if (currentDistance < distance) {
                    distance = currentDistance;
                    classe = entry.getKey();
                    closestPrototype = prototypes.get(i);
                }
            }

        }

        if (distance <= l) {
            closestPrototype.setTime();
            return classe;
        }

        for (Map.Entry<String, ClassHubISC> entry : trainingModel.entrySet()) {
            ArrayList<Instance> prototypes = entry.getValue().getSleepMemory();

            for (int i = 0; i < prototypes.size(); i++) {
                double currentDistance = Utils.distance(prototypes.get(i), instance);
                if (currentDistance < distance) {
                    distance = currentDistance;
                    classe = entry.getKey();
                    closestPrototype = prototypes.get(i);
                }
            }

        }

        if (distance <= l) {
            closestPrototype.setTime();
            trainingModel.get(classe).returnOldPrototype(closestPrototype);
            return classe;
        }

        buffer.add(instance);
        return null;
    }

    //execute HubISC
    public void execute(String path) {

        if (!loadDataSet(path)) {
            System.out.println("error load dataset");
            return;
        }
        ArrayList<Float> arrayAccuracys = new ArrayList<Float>();
        ArrayList<Integer> arrayLabels = new ArrayList<Integer>();
        double analysed = 0;
        int rights = 0;
        int labels = 0;

        for (int i = 0; i < runs; i++) {

            int tempAnalysed = 0;
            int tempRight = 0;
            int tempLabel = 0;

            labels += instancesOfflinePhase;
            maxDistance = Double.MIN_VALUE;

            System.out.println("Run " + (i + 1) + " of " + runs);

            trainingModel = new HashMap<String, ClassHubISC>();

            for (int j = 0; j < completeDataSet.size(); j++) {

                //offline phase
                if (j < instancesOfflinePhase - 1) {
                    offlinePhase.add(completeDataSet.get(j));

                    if (!classesOfflinePhase.contains(completeDataSet.get(j).getClasse())) {
                        classesOfflinePhase.add(completeDataSet.get(j).getClasse());
                    }
                } else {

                    if (j == instancesOfflinePhase - 1) {

                        offlinePhase.add(completeDataSet.get(j));

                        Utils.calculateHubness(offlinePhase, k);

                        int highestHubnessScore = 0;

                        for (int l = 0; l < offlinePhase.size(); l++) {
                            if (offlinePhase.get(l).getHubness() > highestHubnessScore) {
                                highestHubnessScore = offlinePhase.get(l).getHubness();
                            }
                        }

                        for (int l = 0; l < offlinePhase.size(); l++) {
                            if (offlinePhase.get(l).getHubness() >= (highestHubnessScore * hk)) {
                                ClassHubISC classe = trainingModel.get(offlinePhase.get(l).getClasse());
                                classesOfflinePhase.remove(offlinePhase.get(l).getClasse());

                                if (classe == null) {
                                    classe = new ClassHubISC(offlinePhase.get(l).getClasse());
                                    classe.addPrototype(offlinePhase.get(l), true);
                                    trainingModel.put(offlinePhase.get(l).getClasse(), classe);
                                } else {

                                    classe.addPrototype(offlinePhase.get(l), true);
                                    trainingModel.replace(offlinePhase.get(l).getClasse(), classe);
                                }
                            }
                        }

                        //check class without hubs
                        if (classesOfflinePhase.size() > 0) {

                            for (int m = 0; m < classesOfflinePhase.size(); m++) {
                                highestHubnessScore = 0;
                                for (int l = 0; l < offlinePhase.size(); l++) {
                                    if (offlinePhase.get(l).getClasse().equals(classesOfflinePhase.get(m))) {
                                        if (offlinePhase.get(l).getHubness() > highestHubnessScore) {
                                            highestHubnessScore = offlinePhase.get(l).getHubness();
                                        }
                                    }
                                }

                                for (int l = 0; l < offlinePhase.size(); l++) {
                                    if (offlinePhase.get(l).getClasse().equals(classesOfflinePhase.get(m))) {
                                        if (offlinePhase.get(l).getHubness() >= highestHubnessScore) {
                                            ClassHubISC classe = new ClassHubISC(offlinePhase.get(l).getClasse());
                                            classe.addPrototype(offlinePhase.get(l), true);
                                            trainingModel.put(offlinePhase.get(l).getClasse(), classe);

                                        }
                                    }
                                }
                            }
                        }

                    }

                    //start streaming
                    String classe = checkClass(completeDataSet.get(j));

                    if (classe != null) {

                        tempAnalysed++;
                        analysed++;

                        if (classe.equals(completeDataSet.get(j).getClasse())) {
                            rights++;
                            tempRight++;
                        }

                    }

                    //checks if buffer is full
                    if (buffer.size() >= bufferSize) {

                        Utils.calculateHubness(buffer, k);

                        int highestHubnessScore = 0;

                        for (int l = 0; l < buffer.size(); l++) {
                            if (buffer.get(l).getHubness() > highestHubnessScore) {
                                highestHubnessScore = buffer.get(l).getHubness();
                            }
                        }

                        ArrayList<Instance> newPrototypes = new ArrayList<Instance>();

                        for (int l = 0; l < buffer.size(); l++) {
                            if (buffer.get(l).getHubness() >= (highestHubnessScore * hk)) {
                                newPrototypes.add(buffer.get(l));
                            }
                        }

                        for (int l = 0; l < buffer.size(); l++) {
                            if (!newPrototypes.contains(buffer.get(l))) {
                                Instance prototype = checkNearestPrototype(buffer.get(l), newPrototypes);
                                if (prototype != null && prototype.getClasse().equals(buffer.get(l).getClasse())) {
                                    rights++;
                                    tempRight++;
                                }

                                tempAnalysed++;
                                analysed++;
                            }

                        }

                        buffer.clear();
                        labels += newPrototypes.size();
                        tempLabel += newPrototypes.size();

                        for (int l = 0; l < newPrototypes.size(); l++) {
                            ClassHubISC classH = trainingModel.get(newPrototypes.get(l).getClasse());

                            if (classH == null) {
                                classH = new ClassHubISC(newPrototypes.get(l).getClasse());
                                classH.addPrototype(newPrototypes.get(l), false);
                                trainingModel.put(newPrototypes.get(l).getClasse(), classH);
                            } else {

                                classH.addPrototype(newPrototypes.get(l), false);
                                trainingModel.replace(newPrototypes.get(l).getClasse(), classH);
                            }

                        }

                    }

                }

            }

            arrayAccuracys.add((float) tempRight / (float) tempAnalysed);
            arrayLabels.add(tempLabel);
        }

        double std = 0;
        double avg = 0;

        for (Float f : arrayAccuracys) {
            avg += f;
        }

        avg = avg / arrayAccuracys.size();

        System.out.println("AVG Accuracy: " + avg);

        for (Float f : arrayAccuracys) {
            std += Math.pow(f - avg, 2);
        }

        std = std / arrayAccuracys.size();
        System.out.println("STD accuracy: " + Math.sqrt(std));

        std = 0;
        avg = 0;

        for (Integer i : arrayLabels) {
            avg += i;
        }

        avg = avg / arrayLabels.size();

        System.out.println("AVG labels: " + avg);

        for (Integer i : arrayLabels) {
            std += Math.pow(i - avg, 2);
        }

        std = std / arrayLabels.size();
        System.out.println("STD label: " + Math.sqrt(std));

    }

    // load dataset and create instances
    public boolean loadDataSet(String dataSetFile) {

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(dataSetFile));

            String line;

            int countInstance = 0;

            while ((line = in.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                System.out.println("Loading instance " + (++countInstance));

                Instance instance = new Instance(line);

                if (!differentClasses.contains(instance.getClasse())) {
                    differentClasses.add(instance.getClasse());
                }

                completeDataSet.add(instance);
            }

            in.close();

        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;

    }

}
