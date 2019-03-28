package com.gui.gui.merriam;

import java.io.Serializable;
import java.util.ArrayList;

/***
 * This contains all the individual elements to be stored in database,
 * as well as to be read from the API response and helps to displaying data to user
 *
 * Serializable is implemented to pass instance of this class with the INTENT
 * */
class Word implements Serializable {

    private String hw, pr, fl;
    private ArrayList<String> dt;

    String getHw() {
        return hw;
    }

    void setHw(String hw) {
        this.hw = hw;
    }

    String getPr() {
        return pr;
    }

    void setPr(String pr) {
        this.pr = pr;
    }

    String getFl() {
        return fl;
    }

    void setFl(String fl) {
        this.fl = fl;
    }

    ArrayList<String> getDt() {
        return dt;
    }

    void setDt(ArrayList<String> dt) {
        this.dt = dt;
    }
}
