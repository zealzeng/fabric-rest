package com.luoying.fabricrest.vo;

import java.util.Properties;

/**
 * Fabric CA
 */
public class FabricCa {

    private String name;

    private String location;

    private String admin;

    private String password;

    private String privateKey = null;

    private String signedCert = null;

    //===========================================================

    private Properties caProperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Properties getCaProperties() {
        return caProperties;
    }

    public void setCaProperties(Properties caProperties) {
        this.caProperties = caProperties;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSignedCert() {
        return signedCert;
    }

    public void setSignedCert(String signedCert) {
        this.signedCert = signedCert;
    }

    public FabricUser getAdminUser() {
        FabricUser user = new FabricUser();
        user.setName(this.name);
        user.setPrivateKey(privateKey);
        user.setSignedCert(signedCert);
        return user;
    }
}
