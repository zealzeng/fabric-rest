package com.luoying.fabricrest.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Application connection profile
 * @author
 */
public class AppProfile {

    public static final String APP_ID = "appId";

    public static final String APP_SECRET = "appSecret";

    private String appId = null;

    private String appSecret = null;

    private JSONObject jsonProfile = null;

    public AppProfile(String appId, String jsonString) {
        this(appId, null, jsonString);
    }

    public AppProfile(String appId, String appSecret, String jsonString) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.jsonProfile = JSON.parseObject(jsonString, Feature.OrderedField);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public JSONArray getJsonUsers() {
        return this.jsonProfile.getJSONArray("users");
    }

    public JSONArray getJsonAdmins() {
        return this.jsonProfile.getJSONArray("admin");
    }

    public List<FabricUser> getUsers() {
        JSONArray users = this.getJsonUsers();
        if (users == null || users.size() <= 0) {
            return new ArrayList<>(0);
        }
        else {
            return users.toJavaList(FabricUser.class);
        }
    }

    public JSONObject getJsonUserByName(String orgName, String userName) {
        JSONArray array = this.getJsonUsers();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject user = array.getJSONObject(i);
            if (orgName.equals(user.getString("orgName")) && userName.equals(user.getString("name"))) {
                return user;
            }
        }
        return null;
    }

    public FabricUser getUserByName(String orgName, String userName) {
        JSONObject user = this.getJsonUserByName(orgName, userName);
        if (null == user) {
            return null;
        } else {
            return user.toJavaObject(FabricUser.class);
        }
    }


    public List<FabricUser> getAdmins() {
        JSONArray admins = this.getJsonAdmins();
        if (admins == null || admins.size() <= 0) {
            return new ArrayList<>(0);
        }
        else {
            return admins.toJavaList(FabricUser.class);
        }
    }

    public JSONObject getJsonAdminByOrgName(String orgName) {
        JSONArray array = this.getJsonAdmins();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject admin = array.getJSONObject(i);
            if (orgName.equals(admin.getString("orgName"))) {
                return admin;
            }
        }
        return null;
    }


    public JSONObject getJsonAdmin() {
        return this.jsonProfile.getJSONObject("admin");
    }

    public FabricAdmin getAdmin() {
        JSONObject admin = this.getJsonAdmin();
        if (admin == null) {
            return null;
        }
        else {
            return admin.toJavaObject(FabricAdmin.class);
        }
    }

    public void setUserAttribute(String orgName, String userName, String attrName, String attrValue) {
        JSONObject user = this.getJsonUserByName(orgName, userName);
        if (user == null) {
            return;
        }
        user.put(attrName, attrValue);
    }

    public JSONArray getJsonOrgs() {
        return this.jsonProfile.getJSONArray("orgs");
    }

    public List<FabricOrg> getOrgs() {
        JSONArray orgs = this.getJsonOrgs();
        if (orgs == null || orgs.size() <= 0) {
            return new ArrayList<>(0);
        }
        else {
            return orgs.toJavaList(FabricOrg.class);
        }
    }

    public JSONObject getJsonOrgByName(String orgName) {
        JSONArray array = this.getJsonOrgs();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject org = array.getJSONObject(i);
            if (orgName.equals(org.getString("orgName"))) {
                return org;
            }
        }
        return null;
    }

    public FabricOrg getOrgByName(String orgName) {
        JSONObject org = this.getJsonOrgByName(orgName);
        if (org == null) {
            return null;
        }
        else {
            return org.toJavaObject(FabricOrg.class);
        }
    }

    public JSONArray getJsonCaArray() {
        return this.jsonProfile.getJSONArray("ca");
    }

    public List<FabricCa> getCaList() {
        JSONArray caArray = this.getJsonCaArray();
        if (caArray == null || caArray.size() <= 0) {
            return new ArrayList<>(0);
        }
        else {
            return caArray.toJavaList(FabricCa.class);
        }
    }

    public JSONObject getJsonCaByName(String caName) {
        JSONArray array = this.getJsonCaArray();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject ca = array.getJSONObject(i);
            if (caName.equals(ca.getString("name"))) {
                return ca;
            }
        }
        return null;
    }

    public FabricCa getCaByName(String caName) {
        JSONObject ca = this.getJsonCaByName(caName);
        if (ca == null) {
            return null;
        }
        else {
            return ca.toJavaObject(FabricCa.class);
        }
    }

    public void setCaAttribute(String caName, String attrName, String attrValue) {
        JSONObject ca = this.getJsonCaByName(caName);
        if (ca == null) {
            return;
        }
        ca.put(attrName, attrValue);
    }

    public JSONObject getJsonProfile() {
        return this.jsonProfile;
    }

    public String getJsonString() {
        if (this.jsonProfile == null) {
            return "{}";
        }
        else {
            return JSON.toJSONString(this.jsonProfile, SerializerFeature.SortField);
        }
    }


    //channel
    public JSONArray getJsonChannelArray() {
        return this.jsonProfile.getJSONArray("channels");
    }

    public List<FabricChannel> getChannelList() {
        JSONArray array =  this.getJsonChannelArray();
        if (array == null && array.size() < 0) {
            return new ArrayList<>(0);
        } else {
            return array.toJavaList(FabricChannel.class);
        }
    }

    public JSONObject getJsonChannelByName(String channelName) {
        JSONArray array = this.getJsonChannelArray();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject channel = array.getJSONObject(i);
            if (channelName.equals(channel.getString("name"))) {
                return channel;
            }
        }
        return null;
    }

    public FabricChannel getChannelByName(String channelName) {
        JSONObject channel = this.getJsonChannelByName(channelName);
        if (channel == null) {
            return null;
        }
        else {
            return channel.toJavaObject(FabricChannel.class);
        }
    }

    //orderer
    public JSONArray getJsonOrdererArray() {
        return this.jsonProfile.getJSONArray("orderers");
    }

    public List<FabricOrderer> getOrdererList() {
        JSONArray array =  this.getJsonOrdererArray();
        if (array == null && array.size() < 0) {
            return new ArrayList<>(0);
        } else {
            return array.toJavaList(FabricOrderer.class);
        }
    }

    public JSONObject getJsonOrdererByName(String ordererName) {
        JSONArray array = this.getJsonOrdererArray();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject orderer = array.getJSONObject(i);
            if (ordererName.equals(orderer.getString("name"))) {
                return orderer;
            }
        }
        return null;
    }

    public FabricOrderer getOrdererByName(String ordererName) {
        JSONObject  orderer= this.getJsonOrdererByName(ordererName);
        if (orderer == null) {
            return null;
        }
        else {
            return orderer.toJavaObject(FabricOrderer.class);
        }
    }

    //peer
    public JSONArray getJsonPeerArray() {
        return this.jsonProfile.getJSONArray("peers");
    }

    public List<FabricPeer> getPeerList() {
        JSONArray array =  this.getJsonPeerArray();
        if (array == null && array.size() < 0) {
            return new ArrayList<>(0);
        } else {
            return array.toJavaList(FabricPeer.class);
        }
    }

    public JSONObject getJsonPeerByName(String peerName) {
        JSONArray array = this.getJsonPeerArray();
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); ++i) {
            JSONObject peer = array.getJSONObject(i);
            if (peerName.equals(peer.getString("name"))) {
                return peer;
            }
        }
        return null;
    }

    public FabricPeer getPeerByName(String peerName) {
        JSONObject peer= this.getJsonPeerByName(peerName);
        if (peer == null) {
            return null;
        }
        else {
            return peer.toJavaObject(FabricPeer.class);
        }
    }

    public List<FabricPeer> getPeersByOrgName(String orgName) {
        List<FabricPeer> peers = getPeerList();
        if (peers == null || peers.size() <= 0) {
            return null;
        }
        List<FabricPeer> orgPeers = new ArrayList<>(peers.size());
        for (FabricPeer peer : peers) {
            if (orgName.equals(peer.getOrgName())) {
                orgPeers.add(peer);
            }
        }
        return orgPeers;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
