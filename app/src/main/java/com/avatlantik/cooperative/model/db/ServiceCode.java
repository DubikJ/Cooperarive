package com.avatlantik.cooperative.model.db;

public class ServiceCode {
    private int id;
    private String externalId;
    private String name;
    private String parentId;

    private ServiceCode(int id, String externalId, String name, String parentId) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.parentId = parentId;
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

    public String getParentId() {
        return parentId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String externalId;
        private String name;
        private String parentId;

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

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public ServiceCode build() {
            return new ServiceCode(id, externalId, name, parentId);
        }
    }
}
