package com.avatlantik.cooperative.model.json;

import com.avatlantik.cooperative.model.db.Milk;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MilkStatsDTO extends Milk {

    @Expose
    @SerializedName("added_water")
    private double addedWater;

    private MilkStatsDTO(double fat, double snf, double dencity, double addedWater,
                         double fp, double protein, double conductivity, double volume) {
        super(fat, snf, dencity, addedWater, fp, protein, conductivity, volume);
        this.addedWater = addedWater;
    }

    @Override
    public double getAddedWater() {
        return addedWater;
    }

    public static class Builder extends MilkBuilder<Builder> {

        @Override
        public MilkStatsDTO build() {
            return new MilkStatsDTO(fat, snf, dencity, addedWater, fp, protein, conductivity, volume);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
