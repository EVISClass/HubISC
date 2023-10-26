package organizestream;

import classification.NearestNeighbor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

//Data instance representation
public class Instance {

    private ArrayList<Double> attributes = new ArrayList<Double>();
    private String classe;
    private double distance = 0;
    private int hubness=0;
    private long time; 
    private ArrayList<NearestNeighbor> nearestNeighbors = new ArrayList<NearestNeighbor>();

    public Instance(ArrayList<Double> attributes, String classe) {
        this.classe = classe;
        this.attributes = attributes;
    }

    public Instance(String line) {

        String[] temp = line.split(",");

        for (int i = 0; i < temp.length - 1; i++) {
            attributes.add(Double.parseDouble(temp[i]));
        }

        classe = temp[temp.length - 1];
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public ArrayList<Double> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Double> attributes) {
        this.attributes = attributes;
    }
    
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public void incrementHubness(){
        hubness++;
    }

    public int getHubness() {
        return hubness;
    }

    public void setHubness(int hubness) {
        this.hubness = hubness;
    }
    
    
    
    public boolean searchNearestNeighbor(Instance instance){
        for(int i=0; i < nearestNeighbors.size(); i++){
            if(instance.equals(nearestNeighbors.get(i).getInstance()))
                    return true;
        }
        
        return false;
    }
    
    public void addNearestNeighbor(NearestNeighbor instance, int k){
        if(nearestNeighbors.size() < k){
            nearestNeighbors.add(instance);
            return;
        }
        
        for(int i=0; i < nearestNeighbors.size(); i++){
            for(int j=i+1; j < nearestNeighbors.size(); j++){
                if(nearestNeighbors.get(i).getDistance() > nearestNeighbors.get(j).getDistance()){
                    NearestNeighbor aux = nearestNeighbors.get(i);
                    nearestNeighbors.set(i, nearestNeighbors.get(j));
                    nearestNeighbors.set(j, aux);
                }               
            }
        }
        
        if(nearestNeighbors.get(k-1).getDistance() > instance.getDistance()){
            nearestNeighbors.set(k-1, instance);
        }
        
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    
    
    public void setTime(){
        time = (new Date().getTime()/1000);
    }


    
    
}
