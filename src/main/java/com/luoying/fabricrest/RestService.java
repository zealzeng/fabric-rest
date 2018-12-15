package com.luoying.fabricrest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luoying.fabricrest.util.ChannelClient;
import com.luoying.fabricrest.util.StringUtils;
import com.luoying.fabricrest.vo.*;
import com.luoying.fabricrest.util.FabricUtils;
import com.luoying.server.http.HttpRequest;
import com.luoying.server.http.HttpResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Fabric rest service
 * Created by Zeal on 2018/9/16 0016.
 */
public class RestService extends CmnJsonService {

    //Logger
    private static final Logger logger = LogManager.getLogger(RestService.class);

    private RestServiceConfig appConfig = null;

    public RestService() {
    }

    public RestService(RestServiceConfig config) {
        this.appConfig = config;
    }

    public RestServiceConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(RestServiceConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void initialize() throws Exception {
        //appConfig = new RestServiceConfig();
    }

    /**
     * Fabric rest service
     * @param request
     * @param response
     * @throws Exception
     */
    public void service(HttpRequest request, HttpResponse response) throws Exception {

        String uri = request.getRequestURI();
        String path = request.getRequestPath();
        System.err.println("path=" + path);

        if ("".equals(path) || "/".equals(path)) {
            response.write("Fabric REST index page");
        }
        //App profile setting
        else if ("/appProfile".equals(path)) {
            this.appProfile(request, response);
        }
        //Query chaincode function
        else if ("/queryByChaincode".equals(path)) {
            this.queryByChaincode(request, response);
        }
        //Update chaincode function
        else if ("/updateByChaincode".equals(path)) {
            this.updateByChaincode(request, response);
        }
    }



    /**
     * Query chain code function
     * @param request
     * @return
     * @throws Exception
     */
    private void queryByChaincode(HttpRequest request, HttpResponse response) throws Exception {

        Result<AppProfile> result = this.loadAppProfile(request);
        if (result.getResultCode() != FabricError.NO_ERROR) {
            this.jsonResult(response, result.getResultCode());
            return;
        }
        AppProfile appProfile = result.getResultEntity();
        JSONObject jsonObject = this.getRequestJsonObject(request);
        ChannelClient channelClient = this.createChannelClient(appProfile, jsonObject);
        //FIXME Validate request parameters
        ChaincodeRequest req = new ChaincodeRequest(jsonObject);

        try {
            Collection<ProposalResponse> responses = channelClient.queryByChaincode(req);
            Object resultObject = FabricUtils.proposalResponsesToJson(responses);
            this.jsonResult(response, FabricError.NO_ERROR, resultObject);
        }
        finally {
            channelClient.close();
        }
    }

    /**
     * Update chain code function
     * @param request
     * @return
     * @throws Exception
     */
    private void updateByChaincode(HttpRequest request, HttpResponse response) throws Exception {

        Result<AppProfile> result = this.loadAppProfile(request);
        if (result.getResultCode() != FabricError.NO_ERROR) {
            this.jsonResult(response, result.getResultCode());
            return;
        }
        AppProfile appProfile = result.getResultEntity();
        JSONObject jsonObject = this.getRequestJsonObject(request);
        ChannelClient channelClient = this.createChannelClient(appProfile, jsonObject);
        //FIXME Validate request parameters
        ChaincodeRequest req = new ChaincodeRequest(jsonObject);

        try {
            CompletableFuture<BlockEvent.TransactionEvent> future = channelClient.updateByChaincode(req);
            //FIXME Just keep waiting for sometime
            BlockEvent.TransactionEvent event = future.get(32000, TimeUnit.SECONDS);
            FabricUtils.debugTransactionEvent(event);
            if (event.isValid()) {
                this.jsonResult(response, FabricError.NO_ERROR);
            }
            else {
                //FIXME Add more errors?
                this.jsonResult(response, FabricError.UPDATE_BY_CHAINCODE_FAILED);
            }
        }
        finally {
            channelClient.close();
        }
    }

    /**
     * Application connection profile
     * @param request
     * @param response
     * @throws Exception
     */
    private void appProfile(HttpRequest request, HttpResponse response) {

        String appId = request.getParameter(AppProfile.APP_ID);
        if (StringUtils.isBlank(appId)) {
            this.jsonResult(response, FabricError.INVALID_APP_ID);
            return;
        }
        String appSecret = request.getParameter(AppProfile.APP_SECRET);
        if (StringUtils.isBlank(appSecret)) {
            this.jsonResult(response, FabricError.INVALID_APP_SECRET);
            return;
        }
        //FIXME Decrypt and validate the request body
        String requestBody = request.getRequestBody();
        if (StringUtils.isBlank(requestBody)) {
            this.jsonResult(response, FabricError.INVALID_REQUEST_BODY);
            return;
        }
        //FIXME Validate app profile dir
        File appProfileDir = this.appConfig.getAppProfileDir();
        File appDir = new File(appProfileDir, appId);
        File appProfileFile = new File(appDir, RestServiceConfig.APP_PROFILE_FILE);
        File appSecretFile = new File(appDir, RestServiceConfig.APP_SECRET_FILE);
        try {
            String _appSecret = FileUtils.readFileToString(appSecretFile, UTF_8);
            if (!appSecret.equals(_appSecret)) {
                this.jsonResult(response, FabricError.INVALID_APP_SECRET);
                return;
            }
        }
        catch (Exception e) {
            logger.error("Failed to read app secret", e);
            this.jsonResult(response, FabricError.INVALID_APP_SECRET);
            return;
        }

        //Parse app profile json string
        AppProfile appProfile = new AppProfile(appId, requestBody);
        //Enroll CA admins
        int code = this.enrollCaAdmins(appProfile);
        if (code != FabricError.NO_ERROR) {
            this.synchronizeAppProfile(appProfileFile, appProfile);
            this.jsonResult(response, code, appProfile.getJsonProfile());
            return;
        }
        //Enroll normal users
        code = this.enrollUsers(appProfile);
        if (code != FabricError.NO_ERROR) {
            this.synchronizeAppProfile(appProfileFile, appProfile);
            this.jsonResult(response, code, appProfile.getJsonProfile());
            return;
        }
        //Flush profile to file
        Result<JSONObject> result = this.synchronizeAppProfile(appProfileFile, appProfile);
        this.jsonResult(response, result);
    }

//    private void listAffiliations(HttpRequest request, HttpResponse response) throws Exception {
//
//        String appId = request.getParameter(AppProfile.APP_ID);
//        if (StringUtils.isBlank(appId)) {
//            this.jsonResult(response, FabricError.INVALID_APP_ID);
//            return;
//        }
//        //FIXME app secret
//        AppProfile appProfile = this._loadAppProfile(appId);
//        List<FabricCa> caList = appProfile.getCaList();
//        if (caList == null || caList.size() <= 0) {
//            this.jsonResult(response, FabricError.NO_ERROR, new JSONObject(0));
//            return;
//        }
//
//        for (FabricCa ca : caList) {
//
//        }
//
//
//    }
//
//    private void listCaAffiliations(FabricCa ca) throws Exception {
//        HFCAClient caClient = HFCAClient.createNewInstance(ca.getLocation(), null);
//        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
//        //FIXME Validate admin, won't enroll admin
//        FabricUser admin = ca.getAdminUser();
//        admin.setName("admin");
//
//        HFCAAffiliation affiliation = caClient.getHFCAAffiliations(admin);
//        //caClient.newHFCAAffiliation()
//
//    }

    //=========================================================================================

    /**
     * Flush latest app profile to file
     * @param appProfileFile
     * @param appProfile
     */
    private Result<JSONObject> synchronizeAppProfile(File appProfileFile, AppProfile appProfile) {
        if (appProfileFile.exists()) {
            String backupFileName = RestServiceConfig.APP_PROFILE_FILE + '.' + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
            File backupFile = new File(appProfileFile.getParentFile(), backupFileName);
            //Assume it always succeeds
            appProfileFile.renameTo(backupFile);
        }
        String profileJsonString = appProfile.getJsonString();
        try {
            FileUtils.write(appProfileFile, profileJsonString, UTF_8);
            return new Result<>(FabricError.NO_ERROR, "OK", appProfile.getJsonProfile());
        }
        catch (Exception e) {
            logger.error("Failed to save app-config.json", e);
            return new Result<>(FabricError.SAVE_PROFILE_FAIL, "Failed to save app profile", appProfile.getJsonProfile());
        }
    }

//    /**
//     * Parse app profile json string
//     *
//     * @param appId
//     * @param jsonString
//     * @return
//     */
//    private AppProfile parseAppProfile(String appId, String jsonString) {
//        AppProfile appProfile = new AppProfile(appId, null,  jsonString);
//        //FIXME Validate app profile
//        return appProfile;
//    }


    /**
     * Create channel client
     *
     * @return
     */
    private ChannelClient createChannelClient(AppProfile appProfile, JSONObject jsonObject) throws Exception {

        String channelName = jsonObject.getString("channel");
        //FIXME More validation since use or org can be empty
        String orgName = jsonObject.getString("organization");
        FabricUser fabricUser = appProfile.getJsonUserByName(orgName, jsonObject.getString("user")).toJavaObject(FabricUser.class);
        fabricUser.setMspId(appProfile.getOrgByName(orgName).getOrgMspId());
        List<FabricOrderer> orderers = appProfile.getOrdererList();
        //FIXME Allow to pass specified peer names
        List<FabricPeer> peers = appProfile.getPeersByOrgName(orgName);
        return new ChannelClient(channelName, fabricUser, orderers, peers);
    }

    /**
     * Enroll CA admin if it's necessary
     * @param appProfile
     * @return
     */
    private int enrollCaAdmins(AppProfile appProfile) {
        JSONArray caArray = appProfile.getJsonCaArray();
        if (caArray == null || caArray.size() <= 0) {
            return FabricError.NO_ERROR;
        }
        //FIXME Validate CA list
        for (int i = 0; i < caArray.size(); ++i) {
            JSONObject ca = caArray.getJSONObject(i);
            String privateKey = ca.getString("privateKey");
            String signedCert = ca.getString("signedCert");
            //Need enroll ca admin account
            if (StringUtils.isBlank(privateKey) || StringUtils.isBlank(signedCert)) {
                try {
                    //It will update appProfile instance
                    enrollCaAdmin(ca);
                }
                catch (Exception e){
                    logger.warn("Failed to enroll ca admin", e);
                    return FabricError.ENROLL_CA_ADMIN_FAIL;
                }
            }
        }
        return FabricError.NO_ERROR;
    }

    /**
     * Enroll CA admin if private key or signedCert is not present
     * @param ca
     * @throws Exception
     */
    private void enrollCaAdmin(JSONObject ca) throws Exception {
        //HFCAClient.createNewInstance
        String caName = ca.getString("name");
        String caLocation = ca.getString("location");
        String admin = ca.getString("admin");
        String password = ca.getString("password");
        //FIXME Other CA properties if running in TLS
        HFCAClient caClient = HFCAClient.createNewInstance(caName, caLocation, null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        Enrollment enrollment = caClient.enroll(admin, password);
        String privateKey = FabricUtils.getPEMString(enrollment.getKey());
        ca.put("privateKey", privateKey);
        ca.put("signedCert", enrollment.getCert());
    }

    /**
     * Enroll normal users to CA
     *
     * @param appProfile
     * @param appProfile
     * @throws Exception
     */
    private int enrollUsers(AppProfile appProfile) {

        List<FabricUser> users = appProfile.getUsers();
        if (users == null || users.size() <= 0) {
            return FabricError.PROFILE_USERS_REQUIRED;
        }
        //The new users to call chain code
        List<FabricUser> newUsers = new ArrayList<>(users.size());
        for (FabricUser user : users) {
            int code = user.validateProfile();
            if (code != FabricError.NO_ERROR) {
                return code;
            }
            //New users
            if (StringUtils.isBlank(user.getPrivateKey()) || StringUtils.isBlank(user.getSignedCert())) {
                FabricOrg org = appProfile.getOrgByName(user.getOrgName());
                if (org == null) {
                    return FabricError.PROFILE_USER_ORG_NOT_EXIST;
                }
                //FIXME validate org like FabricOrg.validateProfile()
                FabricCa ca = appProfile.getCaByName(org.getCaName());
                if (ca == null) {
                    return FabricError.PROFILE_ORG_CA_NOT_EXIST;
                }
                //FIXME validate ca like FabricCa.validateProfile()
                org.setFabricCa(ca);
                user.setFabricOrg(org);
                newUsers.add(user);
            }
        }
        //Register and enroll new users
        if (newUsers.size() > 0) {
            for (FabricUser newUser : newUsers) {
                int code = registerAndEnrollUser(appProfile, newUser);
                if (code != FabricError.NO_ERROR) {
                    return code;
                }
            }
        }
        return FabricError.NO_ERROR;
    }

    private int registerAndEnrollUser(AppProfile appProfile, FabricUser newUser) {

        FabricOrg org = newUser.getFabricOrg();
        FabricCa ca = org.getFabricCa();
        HFCAClient caClient = null;

        //Need register if no enrollment secret
        if (StringUtils.isBlank(newUser.getSecret())) {
            try {
                caClient = HFCAClient.createNewInstance(ca.getLocation(), null);
                caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                RegistrationRequest rr = null;
                FabricUser adminUser = ca.getAdminUser();
                if (StringUtils.isNotBlank(newUser.getAffiliation())) {
                    getOrCreateAffiliation(caClient, adminUser, newUser.getAffiliation());
                    rr = new RegistrationRequest(newUser.getName(), newUser.getAffiliation());
                }
                else {
                    rr = new RegistrationRequest(newUser.getName());
                }

                String secret = caClient.register(rr, adminUser);
                newUser.setSecret(secret);
                //Write to app profile
                appProfile.setUserAttribute(org.getOrgName(), ca.getName(), "secret", secret);
            }
            catch (Exception e) {
                logger.error("Failed to register user", e);
                return FabricError.CA_REGISTER_FAIL;
            }
        }
        try {
            if (caClient == null) {
                caClient = HFCAClient.createNewInstance(ca.getLocation(), null);
                caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            }
            //FIXME Validate affiliation before invoking
            Enrollment enrollment = caClient.enroll(newUser.getName(), newUser.getSecret());
            String privateKey = FabricUtils.getPEMString(enrollment.getKey());
            String signedCert = enrollment.getCert();
            newUser.setPrivateKey(privateKey);
            newUser.setSignedCert(signedCert);
            appProfile.setUserAttribute(org.getOrgName(), newUser.getName(), "privateKey", privateKey);
            appProfile.setUserAttribute(org.getOrgName(), newUser.getName(), "signedCert", signedCert);
            return FabricError.NO_ERROR;
        }
        catch (Exception e) {
            logger.error("Failed to enroll user", e);
            return FabricError.ENROLL_CA_USER_FAIL;
        }
    }

    /**
     * Get or create affiliation
     * @param caClient
     * @param admin
     * @param affiliationName
     * @throws Exception
     */
    private void getOrCreateAffiliation(HFCAClient caClient, FabricUser admin, String affiliationName) throws Exception {

        Set<String> set = this.getAllAffiliations(caClient, admin);
        //Contain
        if (set.contains(affiliationName)) {
            return;
        }
        //Not contain and create it
        int startIndex = 0;
        while (true) {
            int index = affiliationName.indexOf('.', startIndex);
            if (index == -1) {
                if (!set.contains(affiliationName)) {
                    createAffiliation(caClient, admin, affiliationName);
                }
                break;
            }
            else {
                String name = affiliationName.substring(0, index);
                if (!set.contains(name)) {
                    createAffiliation(caClient, admin, affiliationName);
                }
                startIndex = index + 1;
            }
        }
    }

    /**
     * Create affiliation
     * @param caClient
     * @param admin
     * @param affiliationName
     * @throws Exception
     */
    private void createAffiliation(HFCAClient caClient, FabricUser admin, String affiliationName) throws Exception {
        HFCAAffiliation affiliation = caClient.newHFCAAffiliation(affiliationName);
        HFCAAffiliation.HFCAAffiliationResp resp = affiliation.create(admin);
        if (resp.getStatusCode() != 201) {
            throw new IllegalStateException("Failed to create affiliation " + affiliationName);
        }
    }

    /**
     * Get all affiliation names
     * @param caClient
     * @param user
     * @return
     * @throws Exception
     */
    private Set<String> getAllAffiliations(HFCAClient caClient, FabricUser user) throws Exception {
        HFCAAffiliation resp = caClient.getHFCAAffiliations(user);
        Collection<HFCAAffiliation> collection = resp.getChildren();
        Set<String> set = new HashSet<>();
        _getAllAffiliations(collection, set);
        return set;
    }

    private void _getAllAffiliations(Collection<HFCAAffiliation> collection,  Set<String> affiliations) throws Exception {
        if (collection == null || collection.size() <= 0) {
            return;
        }
        for (HFCAAffiliation aff : collection) {
            affiliations.add(aff.getName());
            _getAllAffiliations(aff.getChildren(), affiliations);
        }
    }

    private Result<AppProfile> loadAppProfile(HttpRequest request) {
        String appId = request.getParameter(AppProfile.APP_ID);
        if (StringUtils.isBlank(appId)) {
            return new Result<>(FabricError.INVALID_APP_ID);
        }
        String appSecret = request.getParameter(AppProfile.APP_SECRET);
        if (StringUtils.isBlank(appSecret)) {
            return new Result<>(FabricError.INVALID_APP_SECRET);
        }
        //JSONObject jsonObject = this.getRequestJsonObject(request);
        AppProfile appProfile = _loadAppProfile(appId);
        if (appProfile == null) {
            return new Result<>(FabricError.INVALID_APP_ID);
        }
        if (!appSecret.equals(appProfile.getAppSecret())) {
            return new Result<>(FabricError.INVALID_APP_SECRET);
        }
        else {
            return new Result<>(FabricError.NO_ERROR, "", appProfile);
        }
    }

    /**
     * Load app profile from file
     * @param appId
     * @return
     * @throws IOException
     */
    private AppProfile _loadAppProfile(String appId) {

        File appProfileDir = this.appConfig.getAppProfileDir();
        File appDir = new File(appProfileDir, appId);
        if (!appDir.exists()) {
            return null;
        }
        File appProfileFile = new File(appDir, RestServiceConfig.APP_PROFILE_FILE);
        if (!appProfileFile.exists()) {
            logger.warn("Failed to find " + appProfileFile.getAbsolutePath());
            return null;
        }
        File appSecretFile = new File(appDir, RestServiceConfig.APP_SECRET_FILE);
        if(!appSecretFile.exists()) {
            logger.warn("Failed to find " + appSecretFile.getAbsolutePath());
            return null;
        }
        try {
            String appProfile = FileUtils.readFileToString(appProfileFile, UTF_8);
            String appSecret = FileUtils.readFileToString(appSecretFile, UTF_8);
            return new AppProfile(appId, appSecret, appProfile);
        }
        catch (Exception e) {
            logger.error("Failed to read app profile or secret file", e);
            return null;
        }
    }



}
