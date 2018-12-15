package com.luoying.fabricrest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luoying.fabricrest.util.FileUtils;
import com.luoying.fabricrest.util.HttpUtils;
import com.luoying.fabricrest.vo.FabricUser;
import com.luoying.fabricrest.vo.Result;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.File;

/**
 * Created by Zeal on 2018/10/7 0007.
 */
public class FabricRestTest {

    private String appId = "app1";

    private String appSecret = "1234567890";

    private String uri = "http://localhost:8080";

    public void testAppProfile() throws Exception {

        String url = uri + "/appProfile?appId=" + appId + "&appSecret=" + appSecret;
        File dir = new File(System.getProperty("user.dir"));
        File jsonFile = new File(dir, "doc/app-profile.json");
        String json = FileUtils.readFileToString(jsonFile, "UTF-8");
        Result<String> result = HttpUtils.post(url, json);
        System.out.println(result.getResultEntity());
        File targetFile = new File(dir, "doc/app-profile-target.json");
        FileUtils.write(targetFile, result.getResultEntity(), "UTF-8");
    }

    public void testListAffiliation() throws Exception {

        //Fabric CA URL
        HFCAClient caClient = HFCAClient.createNewInstance("http://192.168.31.168:7054", null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        FabricUser user = new FabricUser();
        //TODO Copy the ca private key and signed cert here to query all affiliations
        user.setPrivateKey("-----BEGIN EC PRIVATE KEY-----\r\nMHcCAQEEIEzAclLGEdj27lO/xAxo951L1/KEcrFUQst9UfyHavZOoAoGCCqGSM49\r\nAwEHoUQDQgAEmdlbG3KArAVGEBDLshNLJs32gVdqdvwVV2WSF0aLC3kiz8LUVHsV\r\nxWSSw3oJDlW//b/51dyl/vnbu6EymT/uOw==\r\n-----END EC PRIVATE KEY-----\r\n");
        user.setSignedCert("-----BEGIN CERTIFICATE-----\nMIICAjCCAaigAwIBAgIUdBr3IbIZ+lXJusXNmyvgE7f4WU8wCgYIKoZIzj0EAwIw\nczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNh\nbiBGcmFuY2lzY28xGTAXBgNVBAoTEG9yZzEuZXhhbXBsZS5jb20xHDAaBgNVBAMT\nE2NhLm9yZzEuZXhhbXBsZS5jb20wHhcNMTgxMjE1MDczMTAwWhcNMTkxMjE1MDcz\nNjAwWjAhMQ8wDQYDVQQLEwZjbGllbnQxDjAMBgNVBAMTBWFkbWluMFkwEwYHKoZI\nzj0CAQYIKoZIzj0DAQcDQgAEmdlbG3KArAVGEBDLshNLJs32gVdqdvwVV2WSF0aL\nC3kiz8LUVHsVxWSSw3oJDlW//b/51dyl/vnbu6EymT/uO6NsMGowDgYDVR0PAQH/\nBAQDAgeAMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFMXIi7kIFecYeM6EHzBYDILl\n31EfMCsGA1UdIwQkMCKAIEI5qg3NdtruuLoM2nAYUdFFBNMarRst3dusalc2Xkl8\nMAoGCCqGSM49BAMCA0gAMEUCIQDRBUqTiJTiECNe9hEdCChLlfMBThi6K1hS9JGr\nIMsIpAIgZCbdXmW0uEQxjcjytCAYlm9hb7Wzm+ArRFmKAnSOAfs=\n-----END CERTIFICATE-----\n");
        HFCAAffiliation resp = caClient.getHFCAAffiliations(user);
        for (HFCAAffiliation aff : resp.getChildren()) {
            System.out.println(aff.getName() + "==>");
            for (HFCAAffiliation c : aff.getChildren()) {
                System.out.println(c.getName());
            }
        }
//        HFCAAffiliation aff = caClient.newHFCAAffiliation("org1");
//        HFCAAffiliation d = aff.createDecendent("department1");
//        HFCAAffiliation.HFCAAffiliationResp p = d.create(user);
//        System.out.println("statusCode=" + p.getStatusCode());

    }

    public void testInitLedger() throws Exception {
        JSONObject object = new JSONObject(true);
        object.put("channel", "mychannel");
        object.put("version", "1.0");
        object.put("chaincode", "fabcar");
        object.put("function", "initLedger");
        object.put("user", "user001");
        object.put("organization", "org1");

        String url = uri + "/queryByChaincode?appId=" + appId + "&appSecret=" + appSecret;
        String requestBody = object.toJSONString();
        Result<String> httpResult = HttpUtils.post(url, requestBody);
        System.out.println(httpResult);
    }

    public void queryAllCars() throws Exception {
        JSONObject object = new JSONObject(true);
        object.put("channel", "mychannel");
        object.put("version", "1.0");
        object.put("chaincode", "fabcar");
        object.put("function", "queryAllCars");
        object.put("user", "user001");
        object.put("organization", "org1");

        String url = uri + "/queryByChaincode?appId=" + appId + "&appSecret=" + appSecret;
        String requestBody = object.toJSONString();
        Result<String> httpResult = HttpUtils.post(url, requestBody);
        System.out.println(httpResult);
    }

    public void createCar() throws Exception {
        JSONObject object = new JSONObject(true);
        object.put("channel", "mychannel");
        object.put("version", "1.0");
        object.put("chaincode", "fabcar");
        object.put("function", "createCar");
        JSONArray array = new JSONArray();
        //var car = Car{Make: args[1], Model: args[2], Colour: args[3], Owner: args[4]}
        //Car{Make: "Ford", Model: "Mustang", Colour: "red", Owner: "Brad"},
        array.add("CAR11");
        array.add("Ford");
        array.add("Mustang");
        array.add("gray");
        array.add("Zeal");
        object.put("arguments", array);
        object.put("user", "user001");
        object.put("organization", "org1");

        String url = uri + "/updateByChaincode?appId=" + appId + "&appSecret=" + appSecret;
        String requestBody = object.toJSONString();
        Result<String> httpResult = HttpUtils.post(url, requestBody);
        System.out.println(httpResult);
    }


    public static void main(String[] args) throws Exception {
        FabricRestTest test = new FabricRestTest();
//        test.testAppProfile();
//        test.testListAffiliation();
        //It's already called by start.sh, but we invoke it again
//        test.testInitLedger();
        test.createCar();
        //Result:[resultCode=200, resultMessage=null,resultEntity={"resultCode":0,"resultEntity":[{"Record":{"owner":"Tomoko","colour":"blue","model":"Prius","make":"Toyota"},"Key":"CAR0"},{"Record":{"owner":"Brad","colour":"red","model":"Mustang","make":"Ford"},"Key":"CAR1"},{"Record":{"owner":"Zeal","colour":"gray","model":"Mustang","make":"Ford"},"Key":"CAR11"},{"Record":{"owner":"Jin Soo","colour":"green","model":"Tucson","make":"Hyundai"},"Key":"CAR2"},{"Record":{"owner":"Max","colour":"yellow","model":"Passat","make":"Volkswagen"},"Key":"CAR3"},{"Record":{"owner":"Adriana","colour":"black","model":"S","make":"Tesla"},"Key":"CAR4"},{"Record":{"owner":"Michel","colour":"purple","model":"205","make":"Peugeot"},"Key":"CAR5"},{"Record":{"owner":"Aarav","colour":"white","model":"S22L","make":"Chery"},"Key":"CAR6"},{"Record":{"owner":"Pari","colour":"violet","model":"Punto","make":"Fiat"},"Key":"CAR7"},{"Record":{"owner":"Valeria","colour":"indigo","model":"Nano","make":"Tata"},"Key":"CAR8"},{"Record":{"owner":"Shotaro","colour":"brown","model":"Barina","make":"Holden"},"Key":"CAR9"}],"resultMessage":""}]
        test.queryAllCars();
        //FIXME Change owner

    }

}
