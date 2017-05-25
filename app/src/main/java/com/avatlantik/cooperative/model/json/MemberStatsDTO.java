package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.SerializedName;

public final class MemberStatsDTO {
    @SerializedName("company_loan")
    private double companyLoan;
    @SerializedName("member_loan")
    private double memberLoan;
    @SerializedName("milk_volume")
    private double milkVolume;

    public MemberStatsDTO(double companyLoan, double memberLoan, double milkVolume) {
        this.companyLoan = companyLoan;
        this.memberLoan = memberLoan;
        this.milkVolume = milkVolume;
    }

    public double getCompanyLoan() {
        return companyLoan;
    }

    public double getMemberLoan() {
        return memberLoan;
    }

    public double getMilkVolume() {
        return milkVolume;
    }
}
