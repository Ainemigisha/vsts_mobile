package com.example.alvin.vstsmobile;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class AbnormalAverageSpeeds implements Serializable {

    private int lf_id;
    private double av_speed = 0;
    private boolean isOverSpeeding = false;
    private List<Locations> abnormalSpeedLocations;
    private List<Double> abnormalSpeeds;

    public AbnormalAverageSpeeds(int lf_id){
        this.lf_id = lf_id;
    }

    public void setLf_id(int lf_id) {
        this.lf_id = lf_id;
    }

    public void setAv_speed(double av_speed) {
        this.av_speed = av_speed;
    }

    public void setAbnormalSpeedLocations(List<Locations> abnormalSpeedLocations) {
        this.abnormalSpeedLocations = abnormalSpeedLocations;
    }

    public void setAbnormalSpeeds(List<Double> abnormalSpeeds) {
        this.abnormalSpeeds = abnormalSpeeds;
    }

    public void setOverSpeeding(boolean overSpeeding) {
        isOverSpeeding = overSpeeding;
    }

    public int getLf_id() {
        return lf_id;
    }

    public double getAv_speed() {
        return av_speed;
    }

    public List<Double> getAbnormalSpeeds() {
        return abnormalSpeeds;
    }

    public List<Locations> getAbnormalSpeedLocations() {
        return abnormalSpeedLocations;
    }

    public boolean isOverSpeeding(){
        return isOverSpeeding;
    }

    public void addAbnormalSpeed(Locations location){
        int total = 0;
        Double sum = null;

        if (abnormalSpeedLocations.size() == 5){
            abnormalSpeedLocations.remove(0);

        }
        abnormalSpeedLocations.add(location);
        abnormalSpeeds.add(location.getSpeed());

        for (Double speed: abnormalSpeeds){
            sum += speed;
            total +=1;
        }

        if (sum != null)
            av_speed = sum / total;

        if (av_speed != 0 && av_speed > 30 && total == 5)
            isOverSpeeding = true;
        else
            isOverSpeeding = false;
    }






}
