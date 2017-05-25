package com.avatlantik.cooperative.model.db;

public class ServiceDelivery {
    private int id;
    private int trackMemberId;
    private String code;
    private boolean value;

    private ServiceDelivery(int id, int trackMemberId, String code, boolean value) {
        this.id = id;
        this.trackMemberId = trackMemberId;
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getTrackMemberId() {
        return trackMemberId;
    }

    public String getCode() {
        return code;
    }

    public boolean getValue() {
        return value;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private int trackMemberId;
        private String code;
        private boolean value;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder trackMemberId(int trackMemberId) {
            this.trackMemberId = trackMemberId;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder value(boolean value) {
            this.value = value;
            return this;
        }

        public ServiceDelivery build() {
            return new ServiceDelivery(id, trackMemberId, code, value);
        }
    }
}
