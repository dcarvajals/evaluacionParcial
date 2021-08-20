package com.example.evaluacionparcial;

public class ModelPais {
    private String nombre = "";
    private String capital = "";
    private String alpha = "";
    private String iso2 = "";
    private String isonum = "";
    private String iso3 = "";
    private String fips = "";
    private String pref_tel = "";

    private double recwest = 0;
    private double receast = 0;
    private double recnorth = 0;
    private double recsouth = 0;

    public ModelPais() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIsonum() {
        return isonum;
    }

    public void setIsonum(String isonum) {
        this.isonum = isonum;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getFips() {
        return fips;
    }

    public void setFips(String fips) {
        this.fips = fips;
    }

    public String getPref_tel() {
        return pref_tel;
    }

    public void setPref_tel(String pref_tel) {
        this.pref_tel = pref_tel;
    }

    public double getRecwest() {
        return recwest;
    }

    public void setRecwest(double recwest) {
        this.recwest = recwest;
    }

    public double getReceast() {
        return receast;
    }

    public void setReceast(double receast) {
        this.receast = receast;
    }

    public double getRecnorth() {
        return recnorth;
    }

    public void setRecnorth(double recnorth) {
        this.recnorth = recnorth;
    }

    public double getRecsouth() {
        return recsouth;
    }

    public void setRecsouth(double recsouth) {
        this.recsouth = recsouth;
    }
}
