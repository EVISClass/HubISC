package classification;

import java.util.ArrayList;
import organizestream.Instance;

//Class representation of the HubISC algorithm
public class ClassHubISC {

    private String id;
    private ArrayList<Instance> instances = new ArrayList<Instance>();
    private  ArrayList<Instance> prototypes = new ArrayList<Instance>();
    private  ArrayList<Instance> sleepMemory = new ArrayList<Instance>();
    private double maxDistance = Double.MIN_VALUE;

    public ClassHubISC(String id) {
        this.id = id;
    }

    public void incrementalTrainingInstance(Instance instance) {
        instances.add(instance);
    }

    public ArrayList<Instance> getPrototypes() {
        return prototypes;
    }

    public void setPrototypes(ArrayList<Instance> prototypes) {
        this.prototypes = prototypes;
    }

    public ArrayList<Instance> getSleepMemory() {
        return sleepMemory;
    }

    public void setSleepMemory(ArrayList<Instance> sleepMemory) {
        this.sleepMemory = sleepMemory;
    }
    
    public void addPrototype(Instance instance, boolean init){
        if(prototypes.size() == 0 || init){
            prototypes.add(instance);
            return;
        }
        
        long oldest = Integer.MAX_VALUE;
        int indexOldest = -1;
        
        for(int i=0; i < prototypes.size(); i++){
            if(prototypes.get(i).getTime() < oldest){
                oldest = prototypes.get(i).getTime();
                indexOldest = i;
            }
        }
        
        if(sleepMemory.size() > 0)
            sleepMemory.remove(0);
        
        sleepMemory.add(prototypes.get(indexOldest));
        prototypes.remove(indexOldest);
        prototypes.add(instance);
 
    }
    
    public void returnOldPrototype(Instance instance){
        if(prototypes.size() == 0){
            prototypes.add(instance);
            return;
        }
        
        long oldest = Integer.MAX_VALUE;
        int indexOldest = -1;
        
        for(int i=0; i < prototypes.size(); i++){
            if(prototypes.get(i).getTime() < oldest){
                oldest = prototypes.get(i).getTime();
                indexOldest = i;
            }
        }
                
        sleepMemory.remove(instance);
        sleepMemory.add(prototypes.get(indexOldest));
        prototypes.remove(indexOldest);
        prototypes.add(instance);
 
    }

    

}
