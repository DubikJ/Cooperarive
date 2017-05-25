package com.avatlantik.cooperative.model.db;

public class DocumentCode {

    private final int id;
    private final String externalId;
    private final String name;

    private DocumentCode(int id, String externalId, String name) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
    }

    public int getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
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
        private String name;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public DocumentCode build() {
            return new DocumentCode(id, externalId, name);
        }
    }
}
