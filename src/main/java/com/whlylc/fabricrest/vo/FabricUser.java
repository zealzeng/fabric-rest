package com.whlylc.fabricrest.vo;

import com.whlylc.fabricrest.util.FabricUtils;
import com.whlylc.fabricrest.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.*;
import java.util.Set;

public class FabricUser implements User, Serializable {

    private static final long serialVersionUID = 8077132186383604355L;

    private String name;

    private String privateKey = null;

    private String signedCert = null;

    private String orgName = null;

    //CA register call will return enrollment secret
    private String secret;

    //For example: org1.department1
    private String affiliation;

    private String mspId;

    //=======================================================================
    private FabricOrg fabricOrg = null;

    private Enrollment enrollment = null;

    private Set<String> roles;

    private String account;

    public void setName(String name) {
        this.name = name;
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

    public FabricUser() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    /**
     * Set the account.
     *
     * @param account The account.
     */
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    /**
     * Set the affiliation.
     *
     * @param affiliation the affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        if (this.enrollment == null) {
            try {
                this.enrollment =  FabricUtils.createEnrollment(this.privateKey, this.signedCert);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    /**
     * Determine if this name has been registered.
     *
     * @return {@code true} if registered; otherwise {@code false}.
     */
    public boolean isRegistered() {
        return !StringUtil.isNullOrEmpty(secret);
    }

    /**
     * Determine if this name has been enrolled.
     *
     * @return {@code true} if enrolled; otherwise {@code false}.
     */
    public boolean isEnrolled() {
        //return this.enrollment != null;
        return StringUtils.isNotBlank(this.privateKey) && StringUtils.isNotBlank(this.signedCert);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspID) {
        this.mspId = mspID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int validateProfile() {
        if (StringUtils.isBlank(this.name)) {
            return FabricError.PROFILE_USER_NAME_REQUIRED;
        }
        if (StringUtils.isBlank(this.orgName)) {
            return FabricError.PROFILE_ORG_NAME_REQUIRED;
        }
        if (StringUtils.isBlank(this.privateKey) && StringUtils.isBlank(this.signedCert)) {
            return FabricError.NO_ERROR;
        }
        else if (StringUtils.isNotBlank(this.privateKey) && StringUtils.isNotBlank(this.signedCert)) {
            return FabricError.NO_ERROR;
        }
        else {
            return FabricError.PROFILE_INVALID_USER_KEYS;
        }
    }

    public FabricOrg getFabricOrg() {
        return fabricOrg;
    }

    public void setFabricOrg(FabricOrg fabricOrg) {
        this.fabricOrg = fabricOrg;
    }
}
