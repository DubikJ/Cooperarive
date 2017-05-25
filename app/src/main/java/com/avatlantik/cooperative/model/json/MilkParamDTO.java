package com.avatlantik.cooperative.model.json;

import com.avatlantik.cooperative.model.db.Milk;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class MilkParamDTO extends Milk {

    @Expose
    @SerializedName("added_water")
    private double addedWater;
    @Expose
    @SerializedName("ekomilk")
    private boolean isEkomilk;

    private MilkParamDTO(double fat, double snf, double dencity, double addedWater, double fp,
                         double protein, double conductivity, double volume, boolean isEkomilk) {
        super(fat, snf, dencity, addedWater, fp, protein, conductivity, volume);
        this.addedWater = addedWater;
        this.isEkomilk = isEkomilk;
    }

    @Override
    public double getAddedWater() {
        return addedWater;
    }

    public boolean isEkomilk() {
        return isEkomilk;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends MilkBuilder<Builder> {

        private boolean isEkomilk;

        public Builder ekomilk(boolean ekomilk) {
            isEkomilk = ekomilk;
            return this;
        }

        @Override
        public MilkParamDTO build() {
            return new MilkParamDTO(fat, snf, dencity, addedWater, fp, protein, conductivity, volume, isEkomilk);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
