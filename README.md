# Luoying Fabric REST
Hyperledger Fabric是当前商用度最高的联盟区块链平台,客户端SDK正式release的有Node.js和Java版本, 非正式发布的SDK有Python, Go和REST. 对于其它开发语言而言原本可以使用REST,只可惜REST一年未更新了。Fabric的更新速度远远超前于SDK. 而Luoying-fabric-rest是类似原REST做法基于Java SDK做包装或代理, 方便其它开发语言通过http协议接入fabric节点和通道, 注册用户,调用链码查询和更新等等，尽量简化接入fabric网络的操作。


## Maven地址
```
<dependency>
  <groupId>com.whlylc</groupId>
  <artifactId>fabric-rest</artifactId>
  <version>1.2.0-rc1</version>
</dependency>
```

## Application Profile模板
参考/doc/app-profile.json
```json
{
  "ca": [
    {
      "name": "ca.example.com",
      "location": "http://192.168.31.168:7054",
      "admin": "admin",
      "password": "adminpw"
    }
  ],
  "users": [
    {
      "name": "user001",
      "orgName": "org1",
      "affiliation":"org1.deparment001"
    }
  ],
  "orderers": [
    {
      "name": "orderer.example.com",
      "location": "grpc://192.168.31.168:7050"
    }
  ],
  "peers": [
    {
      "name": "peer0.org1.example.com",
      "orgName": "org1",
      "location": "grpc://192.168.31.168:7051",
      "eventHub": "grpc://192.168.31.168:7053"
    }
  ],
  "orgs": [
    {
      "orgName": "org1",
      "orgMspId": "Org1MSP",
      "orgDomainName": "org1.example.com",
      "caName": "ca.example.com"
    }
  ],
  "channels": [
    {
      "name": "mychannel",
      "peers": [
        "peer0.org1.example.com"
      ],
      "orderers": [
        "orderer.example.com"
      ]
    }
  ]
}

```

## API文档
参考/doc/fabric-rest-api.docx

## Fabric测试网络
参考/fabcar,官方hyperldger fabric 1.2中fabric-sample自带例子, 稍做调整

## 测试用例
参考FabricRestTest.java
```java
package com.whlylc.fabricrest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whlylc.fabricrest.util.FileUtils;
import com.whlylc.fabricrest.util.HttpUtils;
import com.whlylc.fabricrest.vo.FabricUser;
import com.whlylc.fabricrest.vo.Result;
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

```

## 开发计划
完整的发布应该类似Tomcat一样的默认打包, 控制脚本等, 消息通知的版本有差异, 对官方Java SDK一些细节也有一些疑惑, 先陆续稳定一版。原则上Fabric REST只做到简单够用即可(毕竟代理了一层,HTTP和无状态也有些限制), 毕竟GRPC现在连JS SDK都有, 其它语言的SDK是时间问题.

## 开发伙伴召集
待定
