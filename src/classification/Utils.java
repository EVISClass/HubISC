package classification;

import java.util.ArrayList;
import organizestream.Instance;

public class Utils {

    //euclidian distance
    public static double distance(Instance i1, Instance i2) {
        if (i1.getAttributes().size() != i2.getAttributes().size()) {
            return Double.MAX_VALUE;
        }

        double distance = 0;

        for (int i = 0; i < i1.getAttributes().size(); i++) {
            distance += Math.pow(i1.getAttributes().get(i) - i2.getAttributes().get(i), 2);
        }

        return Math.sqrt(distance);
    }
    
    public static void calculateHubness(ArrayList<Instance> instance, int k){
        
        
        for(int i=0; i < instance.size(); i++){
            for(int j=0; j < instance.size(); j++){
                if(i == j)
                    continue;
                
                double distance = distance(instance.get(i), instance.get(j));
                
                instance.get(i).addNearestNeighbor(new NearestNeighbor(instance.get(j), distance), k);               
            }
        }
        
        
        for(int i=0; i < instance.size(); i++){
            for(int j=0; j < instance.size(); j++){
                if(i == j)
                    continue;
                
                if(instance.get(j).searchNearestNeighbor(instance.get(i))){
                    instance.get(i).incrementHubness();
                }
                
            }
        }
        
        
        
    }
}
