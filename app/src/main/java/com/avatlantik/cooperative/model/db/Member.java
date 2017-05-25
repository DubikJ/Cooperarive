package com.avatlantik.cooperative.model.db;

import java.io.Serializable;

public class Member implements Serializable {
    private int id;
    private String externalId;
    private String name;
    private String address;
    private String phone;
    private String qrcode;

    private Member(int id, String externalId, String name, String address, String phone, String qrcode) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.address = address;
        this.phone = phone;
        this.qrcode = qrcode;
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

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getQrcode() {
        return qrcode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String externalId;
        private String name;
        private String address;
        private String phone;
        private String qrcode;
        private int id;

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder qrcode(String qrcode) {
            this.qrcode = qrcode;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Member build() {
            return new Member(id, externalId, name, address, phone, qrcode);
        }
    }
}
