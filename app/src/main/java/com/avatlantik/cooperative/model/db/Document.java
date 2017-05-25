package com.avatlantik.cooperative.model.db;

public class Document {

    private int id;
    private int visitId;
    private String code;
    private boolean value;

    private Document(int id, int visitId, String code, boolean value) {
        this.id = id;
        this.visitId = visitId;
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getVisitId() {
        return visitId;
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
        private int visitId;
        private String code;
        private boolean value;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder visitId(int visitId) {
            this.visitId = visitId;
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

        public Document build() {
            return new Document(id, visitId, code, value);
        }
    }
}
