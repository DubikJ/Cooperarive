package com.avatlantik.cooperative.model.db;

public class MemberStats {
    private int id;
    private String memberId;
    private double companyLoan;
    private double memberLoan;
    private double milkVolume;

    private MemberStats(int id, String memberId, double memberLoan, double companyLoan, double milkVolume) {
        this.id = id;
        this.memberId = memberId;
        this.memberLoan = memberLoan;
        this.companyLoan = companyLoan;
        this.milkVolume = milkVolume;
    }

    public double getCompanyLoan() {
        return companyLoan;
    }

    public int getId() {
        return id;
    }

    public double getMemberLoan() {
        return memberLoan;
    }

    public double getMilkVolume() {
        return milkVolume;
    }

    public String getMemberId() {
        return memberId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String memberId;
        private double memberLoan;
        private double companyLoan;
        private double milkVolume;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder memberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder memberLoan(double memberLoan) {
            this.memberLoan = memberLoan;
            return this;
        }

        public Builder companyLoan(double companyLoan) {
            this.companyLoan = companyLoan;
            return this;
        }

        public Builder milkVolume(double milkVolume) {
            this.milkVolume = milkVolume;
            return this;
        }

        public MemberStats build() {
            return new MemberStats(id, memberId, memberLoan, companyLoan, milkVolume);
        }
    }
}
