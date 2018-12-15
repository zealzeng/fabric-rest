package com.luoying.fabricrest.vo;

import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class FabricOrg {

    private String orgName;

    private String orgMspId;

    private String orgDomainName;

    private String caName;

    //=====================================

    private FabricCa fabricCa = null;

    /**@deprecated */
    HFCAClient caClient;

    public FabricOrg() {
    }

    public FabricOrg(String orgName, String orgMspId, String orgDomainName) {
        this.orgName = orgName;
        this.orgMspId = orgMspId;
        this.orgDomainName = orgDomainName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgMspId() {
        return orgMspId;
    }

    public void setOrgMspId(String orgMspId) {
        this.orgMspId = orgMspId;
    }

    public String getOrgDomainName() {
        return orgDomainName;
    }

    public void setOrgDomainName(String orgDomainName) {
        this.orgDomainName = orgDomainName;
    }

    /**@deprecated */
    public HFCAClient getCaClient() {
        return caClient;
    }

    /**@deprecated */
    public void setCaClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public FabricCa getFabricCa() {
        return fabricCa;
    }

    public void setFabricCa(FabricCa fabricCa) {
        this.fabricCa = fabricCa;
    }

    //    public String getCa() {
//        return ca;
//    }
//
//    public void setCa(String ca) {
//        this.ca = ca;
//    }
}
