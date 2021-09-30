package com.zopnote.android.merchant.data.model;

import java.util.List;

/**
 * Created by nmohideen on 22/06/18.
 */

public class Merchant {

    private String id;
    private String name;
    private String ownerName;
    private Double billed;
    private Double paidCash;
    private Double paidOnline;
    private Double pending;
    private List<String> routes;
    private List<String> areaList;
    private List<String> productList;
    private String addressFieldsConfig;
    private String profilePicUrl;
    private String contactNumber;
    private String PAN;
    private String aadhar;
    private String proofPicUrl;
    private String agreementSigned;
    private String bankAccountName;
    private String bankAccountNo;
    private String bankIFSCCode;
    private String bankName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getBilled() {
        return billed;
    }

    public void setBilled(Double billed) {
        this.billed = billed;
    }

    public Double getPaidCash() {
        return paidCash;
    }

    public void setPaidCash(Double paidCash) {
        this.paidCash = paidCash;
    }

    public Double getPaidOnline() {
        return paidOnline;
    }

    public void setPaidOnline(Double paidOnline) {
        this.paidOnline = paidOnline;
    }

    public Double getPending() {
        return pending;
    }

    public void setPending(Double pending) {
        this.pending = pending;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }

    public List<String> getProductList() {
        return productList;
    }

    public void setProductList(List<String> productList) {
        this.productList = productList;
    }

    public List<String> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<String> areaList) {
        this.areaList = areaList;
    }

    public String getAddressFieldsConfig() {
        return addressFieldsConfig;
    }

    public void setAddressFieldsConfig(String addressFieldsConfig) {
        this.addressFieldsConfig = addressFieldsConfig;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getProofPicUrl() {
        return proofPicUrl;
    }

    public void setProofPicUrl(String proofPicUrl) {
        this.proofPicUrl = proofPicUrl;
    }

    public String getAgreementSigned() {
        return agreementSigned;
    }

    public void setAgreementSigned(String agreementSigned) {
        this.agreementSigned = agreementSigned;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankIFSCCode() {
        return bankIFSCCode;
    }

    public void setBankIFSCCode(String bankIFSCCode) {
        this.bankIFSCCode = bankIFSCCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return name + " - " + ownerName;
    }
}
