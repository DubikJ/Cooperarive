package com.avatlantik.cooperative.model.db;

public class TrackMember {

    private int id;
    private String trackExternalId;
    private String memberExternalId;

    private TrackMember(int id, String trackExternalId, String memberExternalId) {
        this.id = id;
        this.trackExternalId = trackExternalId;
        this.memberExternalId = memberExternalId;
    }

    public int getId() {
        return id;
    }

    public String getTrackExternalId() {
        return trackExternalId;
    }

    public String getMemberExternalId() {
        return memberExternalId;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private int id;
        private String trackExternalId;
        private String memberExternalId;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder trackExternalId(String trackExternalId) {
            this.trackExternalId = trackExternalId;
            return this;
        }

        public Builder memberExternalId(String memberExternalId) {
            this.memberExternalId = memberExternalId;
            return this;
        }

        public TrackMember build() {
            return new TrackMember(id, trackExternalId, memberExternalId);
        }
    }
}
