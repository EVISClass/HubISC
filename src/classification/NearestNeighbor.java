/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import organizestream.Instance;

/**
 *
 * @author mateu
 */
public class NearestNeighbor {
    private Instance instance;
    private double distance;

    public NearestNeighbor(Instance instance, double distance) {
        this.instance = instance;
        this.distance = distance;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    
    
    
}
