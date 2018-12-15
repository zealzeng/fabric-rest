package com.whlylc.fabricrest.vo;

import java.util.*;

/**
 *
 */
public class FabricChannel {

    private String name;

    private List<String> peers;

    private List<String> orderers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPeers() {
        return peers;
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
    }

    public List<String> getOrderers() {
        return orderers;
    }

    public void setOrderers(List<String> orderers) {
        this.orderers = orderers;
    }
}
