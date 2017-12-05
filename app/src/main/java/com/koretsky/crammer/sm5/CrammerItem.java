package com.koretsky.crammer.sm5;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Михаил on 28.11.2017.
 */

public class CrammerItem implements Serializable, Comparable<CrammerItem> {
    private String value;
    private String answer;
    private float eFactor;
    private float averageMark;
    private int timesRepeated;
    private int repInterval;
    private Date nextRepDate;

    public CrammerItem(String v, String a) {
        value = v;
        answer = a;
        averageMark = (float) 2.5;
        timesRepeated = 0;
        repInterval = 1;
        nextRepDate = new Date();
        this.syncNextRepDate();

    }

    public String getAnswer() {
        return answer;
    }

    public String getValue() {
        return value;
    }

    protected Date getNextRepDate() {
        return this.nextRepDate;
    }

    protected void syncNextRepDate() {
        this.nextRepDate.setTime((repInterval * 86400000) + nextRepDate.getTime());
    }

    protected boolean cmprName(String name) {
        return name.equals(this.value);
    }

    protected float modifyEfactor(float mark) {
        averageMark = (float) ((averageMark * (timesRepeated - 1) / timesRepeated) + (mark / timesRepeated));
        if (mark < 3) {
            timesRepeated = 0;
            return eFactor;
        }
        this.eFactor = (float) (eFactor + (0.1 - (5 - mark) * (0.08 + (5 - mark) * 0.02)));
        if (eFactor < 1.3) eFactor = (float) 1.3;
        if (eFactor > 5.0) eFactor = (float) 5.0;
        return eFactor;
    }

    protected float getAverageMark() {
        return averageMark;
    }

    protected int incTimesRep() {
        return this.timesRepeated = timesRepeated + 1;
    }

    public int getEFactorForMatrix() {
        return (int) (this.eFactor * 10);
    }

    public int getTimesRepeated() {
        return (int) this.timesRepeated;
    }

    protected int setInterval(int interval) {
        this.repInterval = interval;
        return this.repInterval;
    }

    public int getInterval() {
        return this.repInterval;
    }

    @Override
    public int compareTo(@NonNull CrammerItem crammerItem) {
        return this.getNextRepDate().compareTo(crammerItem.getNextRepDate());
    }
}