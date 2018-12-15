package com.luoying.fabricrest.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.TransactionRequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zeal on 2018/11/19 0019.
 */
public class ChaincodeRequest implements Serializable {

    private String name = null;

    private String version = null;

    private TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;

    private String function = null;

    //The Fabric SDK use ArrayList as parameter, we have no choice
    private ArrayList<String> args = null;

    //In milliseconds, timeout to send the proposal request
    private long proposalWaitTime = 120000;

    //private long transactionWaitTime = 32000;

    public ChaincodeRequest() {
    }

    public ChaincodeRequest(JSONObject jsonObject) {
        this.name = jsonObject.getString("chaincode");
        this.version = jsonObject.getString("version");
        this.function = jsonObject.getString("function");
        JSONArray array = jsonObject.getJSONArray("arguments");
        if (array != null && array.size() > 0) {
            ArrayList _args = new ArrayList(array.size());
            for (int i = 0; i < array.size(); ++i) {
                _args.add(array.getString(i));
            }
            this.args = _args;
        }
    }



    public String getName() {
        return name;
    }

    public ChaincodeRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ChaincodeRequest setVersion(String version) {
        this.version = version;
        return this;
    }

    public TransactionRequest.Type getLanguage() {
        return language;
    }

    public ChaincodeRequest setLanguage(TransactionRequest.Type language) {
        this.language = language;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public ChaincodeRequest setFunction(String function) {
        this.function = function;
        return this;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public ChaincodeRequest setArgs(ArrayList<String> args) {
        this.args = args;
        return this;
    }

    public ChaincodeRequest setArgs(String..._args) {
        if (this.args == null) {
            this.args = new ArrayList<>(_args.length);
        }
        for (String _arg : _args) {
            this.args.add(_arg);
        }
        return this;
    }




    public long getProposalWaitTime() {
        return proposalWaitTime;
    }

    public ChaincodeRequest setProposalWaitTime(long proposalWaitTime) {
        this.proposalWaitTime = proposalWaitTime;
        return this;
    }

    public ChaincodeID buildChaincodeID() {
        return ChaincodeID.newBuilder().setName(this.name).setVersion(this.version).build();
    }
}
