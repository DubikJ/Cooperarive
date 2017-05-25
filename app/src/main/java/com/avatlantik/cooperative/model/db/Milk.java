package com.avatlantik.cooperative.model.db;

import java.io.Serializable;

public class Milk implements Serializable {

    protected double fat;
    protected double snf;
    protected double dencity;
    protected double addedWater;
    protected double fp;
    protected double protein;
    protected double conductivity;
    protected double volume;

    public Milk(double fat, double snf, double dencity, double addedWater,
                double fp, double protein, double conductivity, double volume) {
        this.fat = fat;
        this.snf = snf;
        this.dencity = dencity;
        this.addedWater = addedWater;
        this.fp = fp;
        this.protein = protein;
        this.conductivity = conductivity;
        this.volume = volume;
    }

    public double getFat() {
        return fat;
    }

    public double getDencity() {
        return dencity;
    }

    public double getVolume() {
        return volume;
    }

    public double getProtein() {
        return protein;
    }

    public double getConductivity() {
        return conductivity;
    }

    public double getAddedWater() {
        return addedWater;
    }

    public double getSnf() {
        return snf;
    }

    public double getFp() {
        return fp;
    }

    public double[] toArray() {
        double[] result = new double[8];
        result[0] = fat;
        result[1] = snf;
        result[2] = dencity;
        result[3] = addedWater;
        result[4] = fp;
        result[5] = protein;
        result[6] = conductivity;
        result[7] = volume;
        return result;
    }

    public static abstract class MilkBuilder<B extends MilkBuilder> {
        protected double fat;
        protected double snf;
        protected double dencity;
        protected double addedWater;
        protected double fp;
        protected double protein;
        protected double conductivity;
        protected double volume;

        protected abstract B self();

        protected abstract <T extends Milk> T build();

        public B fat(double fat) {
            this.fat = fat;
            return self();
        }

        public B dencity(double dencity) {
            this.dencity = dencity;
            return self();
        }

        public B protein(double protein) {
            this.protein = protein;
            return self();
        }

        public B volume(double volume) {
            this.volume = volume;
            return self();
        }

        public B conductivity(double conductivity) {
            this.conductivity = conductivity;
            return self();
        }

        public B addedWater(double addedWater) {
            this.addedWater = addedWater;
            return self();
        }

        public B snf(double snf) {
            this.snf = snf;
            return self();
        }

        public B fp(double fp) {
            this.fp = fp;
            return self();
        }

    }
}
