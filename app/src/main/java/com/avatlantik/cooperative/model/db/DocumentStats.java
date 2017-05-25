package com.avatlantik.cooperative.model.db;

public class DocumentStats {

    private int id;
    private String memberId;
    private String code;
    private boolean value;

    private DocumentStats(int id, String memberId, String code, boolean value) {
        this.id = id;
        this.memberId = memberId;
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getCode() {
        return code;
    }

    public boolean isValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String memberId;
        private String code;
        private boolean value;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder memberId(String memberId) {
            this.memberId = memberId;
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

        public DocumentStats build() {
            return new DocumentStats(id, memberId, code, value);
        }
    }
}
