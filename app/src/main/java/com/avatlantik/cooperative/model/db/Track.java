package com.avatlantik.cooperative.model.db;

public final class Track {
    private int id;
    private String externalId;
    private String date;
    private String name;

    public Track(int id, String externalId, String date, String name) {
        this.id = id;
        this.externalId = externalId;
        this.date = date;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String externalId;
        private String date;
        private String name;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Track build() {
            return new Track(id, externalId, date, name);
        }
    }
}
