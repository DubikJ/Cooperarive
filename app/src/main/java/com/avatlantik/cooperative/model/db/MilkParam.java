package com.avatlantik.cooperative.model.db;

public class MilkParam extends Milk {
    private int id;
    private int visitId;
    private boolean isEkomilk;

    private MilkParam(int id, int visitId, double fat, double snf, double dencity,
                      double addedWater, double fp, double protein, double conductivity,
                      double volume, boolean isEkomilk) {
        super(fat, snf, dencity, addedWater, fp, protein, conductivity, volume);
        this.id = id;
        this.visitId = visitId;
        this.isEkomilk = isEkomilk;
    }

    public int getId() {
        return id;
    }

    public int getVisitId() {
        return visitId;
    }

    public boolean isEkomilk() {
        return isEkomilk;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends MilkBuilder<Builder> {
        private int visitId;
        private int id;
        private boolean isEkomilk;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder visitId(int visitId) {
            this.visitId = visitId;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder isEkomilk(boolean value) {
            isEkomilk = value;
            return this;
        }

        @Override
        public MilkParam build() {
            return new MilkParam(id, visitId, fat, snf, dencity, addedWater,
                    fp, protein, conductivity, volume, isEkomilk);
        }
    }
}
