package com.luoying.fabricrest.vo;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * Created by Zeal on 2018/10/7 0007.
 */
public class FabricEnrollment implements Enrollment, Serializable {

    private final PrivateKey privateKey;

    private final String certificate;

    public FabricEnrollment(PrivateKey privateKey, String certificate) {
        this.certificate = certificate;

        this.privateKey = privateKey;
    }

    @Override
    public PrivateKey getKey() {
        return privateKey;
    }

    @Override
    public String getCert() {
        return certificate;
    }


}
