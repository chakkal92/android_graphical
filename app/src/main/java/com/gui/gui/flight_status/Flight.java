package com.gui.gui.flight_status;

import java.io.Serializable;

/***
 * This contains all the individual elements to be stored in database,
 * as well as to be read from the API response and helps to displaying data to user
 *
 * Serializable is implemented to pass instance of this class with the INTENT
 * */
public class Flight implements Serializable {
    double latitude, longitude, altitude, direction, horizontal;
    String status, number, iataNumber, icaoNumber;
    String aIATA, dIATA;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIataNumber() {
        return iataNumber;
    }

    public void setIataNumber(String iataNumber) {
        this.iataNumber = iataNumber;
    }

    public String getIcaoNumber() {
        return icaoNumber;
    }

    public void setIcaoNumber(String icaoNumber) {
        this.icaoNumber = icaoNumber;
    }

    public String getaIATA() {
        return aIATA;
    }

    public void setaIATA(String aIATA) {
        this.aIATA = aIATA;
    }

    public String getdIATA() {
        return dIATA;
    }

    public void setdIATA(String dIATA) {
        this.dIATA = dIATA;
    }

}
