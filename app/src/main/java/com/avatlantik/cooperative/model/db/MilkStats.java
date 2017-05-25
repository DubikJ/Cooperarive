package com.avatlantik.cooperative.model.db;

public class MilkStats extends Milk {

    private int id;
    private String memberId;

    private MilkStats(int id, String memberId, double fat, double snf, double dencity,
                      double addedWater, double fp, double protein, double conductivity,
                      double volume) {
        super(fat, snf, dencity, addedWater, fp, protein, conductivity, volume);
        this.id = id;
        this.memberId = memberId;
    }

    public int getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends MilkBuilder<Builder> {
        private int id;
        private String memberId;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder memberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public MilkStats build() {
            return new MilkStats(id, memberId, fat, snf, dencity, addedWater,
                    fp, protein, conductivity, volume);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
