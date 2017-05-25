package com.avatlantik.cooperative.model.db;


import java.io.Serializable;
import java.util.Date;

public class Visit implements Serializable {
    private int id;
    private int memberId;
    private Date date;

    private Visit(int id, int memberId, Date date) {
        this.id = id;
        this.memberId = memberId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getMemberId() {
        return memberId;
    }

    public Date getDate() {
        return date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private int memberId;
        private Date date;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder memberId(int memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Visit build() {
            return new Visit(id, memberId, date);
        }
    }
}
