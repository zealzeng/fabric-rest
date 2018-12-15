package com.whlylc.fabricrest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whlylc.fabricrest.vo.FabricEnrollment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;

import java.io.*;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Collection;

/**
 * Fabric utility
 */
public class FabricUtils {

    private static final Logger logger = LogManager.getLogger(FabricUtils.class);

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.addProvider(new SunMSCAPI());
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private FabricUtils() {
    }

    /**
     * Touch and make sure load the BC provider
     */
    public static void initialize() {
    }

    /**
     * Create enrollment object
     * @param pemPrivateKey
     * @param pemSignedCert
     * @return
     * @throws IOException
     */
    public static FabricEnrollment createEnrollment(String pemPrivateKey, String pemSignedCert) throws IOException {
        PrivateKey privateKey = null;
        if (pemPrivateKey != null && !pemPrivateKey.isEmpty()) {
            privateKey = getPrivateKeyFromPEMString(pemPrivateKey);
        }
        return new FabricEnrollment(privateKey, pemSignedCert);
    }

    /**
     * Get private key from pem string
     * @param pemPrivateKey
     * @return
     * @throws IOException
     */
    public static PrivateKey getPrivateKeyFromPEMString(String pemPrivateKey) throws IOException {
        try (Reader pemReader = new StringReader(pemPrivateKey)) {
            PrivateKeyInfo pemPair = null;
            try (PEMParser pemParser = new PEMParser(pemReader)) {
                Object object = pemParser.readObject();
                if (object.getClass().equals(PrivateKeyInfo.class)) {
                    pemPair = (PrivateKeyInfo) object;
                } else if (object.getClass().equals(PEMKeyPair.class)) {
                    pemPair = ((PEMKeyPair) object).getPrivateKeyInfo();
                }
            }
            return new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        }
    }

    /**
     * Private key to PEM string
     * @param privateKey
     * @return
     * @throws IOException
     */
    public static String getPEMString(PrivateKey privateKey) throws IOException {
        StringWriter pemStrWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(pemStrWriter)) {
            pemWriter.writeObject(privateKey);
        }
        return pemStrWriter.toString();
    }


    public static void debugProposalResponse(Collection<ProposalResponse> responses) {
        for (ProposalResponse response : responses) {
            debugProposalResponse(response);
        }
    }

    public static void debugProposalResponse(ProposalResponse response) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Proposal response detail:");
            sb.append("txId=").append(response.getTransactionID());
            sb.append(",peer=").append(response.getPeer().getName());
            sb.append(",status=").append(response.getStatus());
            sb.append(",message=").append(response.getMessage());
            logger.debug(sb.toString());
        }
    }

    public static void debugTransactionEvent(BlockEvent.TransactionEvent event) {
        if (logger.isDebugEnabled()) {
            //FIXME More detail
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction event detail:");
            sb.append("txId=").append(event.getTransactionID());
            sb.append(",peer=").append(event.getPeer().getName());
            sb.append(",channelId=").append(event.getChannelId());
            logger.debug(sb.toString());
        }
    }


    /**
     * @param proposalResponse
     * @return
     */
    public static boolean isProposalResponseSuccessful(ProposalResponse proposalResponse) {
        return proposalResponse.isVerified() && proposalResponse.getStatus() == ProposalResponse.Status.SUCCESS;
    }

    /**
     * @param proposalResponse
     * @param charset
     * @return
     */
    public static String getProposalResponseString(ProposalResponse proposalResponse, Charset charset) {
        return proposalResponse.getProposalResponse().getResponse().getPayload().toString(charset);
    }

    /**
     * Default charset is utf-8
     * @param proposalResponse
     * @return
     */
    public static String getProposalResponseString(ProposalResponse proposalResponse) {
        return proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
    }

    public static JSONObject proposalResponsesToJsonObject(Collection<ProposalResponse> queryProposals) {
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!FabricUtils.isProposalResponseSuccessful(proposalResponse)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " +
                            proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage() + ". Was verified : " + proposalResponse.isVerified());
                }
            }
            else {
                String value = FabricUtils.getProposalResponseString(proposalResponse);
                return JSON.parseObject(value);
            }
        }
        FabricUtils.debugProposalResponse(queryProposals);
        return null;
    }

    public static JSONArray proposalResponsesToJsonArray(Collection<ProposalResponse> queryProposals) {
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!FabricUtils.isProposalResponseSuccessful(proposalResponse)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " +
                            proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage() + ". Was verified : " + proposalResponse.isVerified());
                }
            }
            else {
                String value = FabricUtils.getProposalResponseString(proposalResponse);
                return JSON.parseArray(value);
            }
        }
        FabricUtils.debugProposalResponse(queryProposals);
        return null;
    }

    /**
     * Transfer response to json, json array or string
     * @param queryProposals
     * @return
     */
    public static Object proposalResponsesToJson(Collection<ProposalResponse> queryProposals) {
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!FabricUtils.isProposalResponseSuccessful(proposalResponse)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " +
                            proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage() + ". Was verified : " + proposalResponse.isVerified());
                }
            }
            else {
                String value = FabricUtils.getProposalResponseString(proposalResponse);
                if (StringUtils.isEmpty(value)) {
                    return "";
                }
                try {
                    return JSON.parseObject(value);
                }
                catch (Exception e) {
                    try {
                        return JSON.parseArray(value);
                    }
                    catch (Exception ex) {
                        return value;
                    }
                }
            }
        }
        FabricUtils.debugProposalResponse(queryProposals);
        return null;
    }

}
