package com.luoying.fabricrest.util;

import com.luoying.fabricrest.vo.*;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;

/**
 * Created by Zeal on 2018/11/13 0013.
 */
public class ChannelClient implements Closeable {

    private static final Logger logger = LogManager.getLogger(ChannelClient.class);

    private HFClient hfClient = null;

    private AppProfile appProfile = null;

    private Channel channel = null;

    public ChannelClient(String channelName, FabricUser fabricUser, List<FabricOrderer> fabricOrderers, List<FabricPeer> fabricPeers) throws Exception {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        this.hfClient = client;
        this.hfClient.setUserContext(fabricUser);
        this.channel = this.constructChannel(channelName, fabricOrderers, fabricPeers);
    }

    /**
     * Construct channel
     *
     * @param channelName
     * @return
     * @throws Exception
     */
    private Channel constructChannel(String channelName, List<FabricOrderer> fabricOrderers, List<FabricPeer> fabricPeers) throws Exception {

        Channel _channel = this.hfClient.newChannel(channelName);

        //Set orderer
        for (FabricOrderer fabricOrderer : fabricOrderers) {
            _channel.addOrderer(this.hfClient.newOrderer(fabricOrderer.getName(), fabricOrderer.getLocation()));
        }

        final Channel.PeerOptions peerEventingOptions = createPeerOptions().registerEventsForBlocks().setPeerRoles(
                EnumSet.of(Peer.PeerRole.ENDORSING_PEER, Peer.PeerRole.LEDGER_QUERY, Peer.PeerRole.CHAINCODE_QUERY, Peer.PeerRole.EVENT_SOURCE));
        //createPeerOptions().registerEventsForFilteredBlocks().setPeerRoles(EnumSet.of(Peer.PeerRole.ENDORSING_PEER, Peer.PeerRole.LEDGER_QUERY, Peer.PeerRole.CHAINCODE_QUERY, Peer.PeerRole.EVENT_SOURCE)
        for (FabricPeer fabricPeer : fabricPeers) {
            Peer peer = this.hfClient.newPeer(fabricPeer.getName(), fabricPeer.getLocation());
            //FIXME The peer role affects channel.sendTransactionProposals
            _channel.addPeer(peer, peerEventingOptions);
        }
        _channel.initialize();
        return _channel;
    }

    /**
     * Invoke chaincode to query ledger
     * @param request
     * @return
     * @throws Exception
     */
    public Collection<ProposalResponse> queryByChaincode(ChaincodeRequest request) throws Exception {

        ChaincodeID chaincodeID = request.buildChaincodeID();

        QueryByChaincodeRequest queryByChaincodeRequest = this.hfClient.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(request.getArgs());
        queryByChaincodeRequest.setFcn(request.getFunction());
        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        Map<String, byte[]> tmap = new HashMap<>(4);
        tmap.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tmap.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tmap);
        //FIXME query channel.getPeers() or query peers?
        //Collection<ProposalResponse> queryProposals = this.channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
        Collection<ProposalResponse> queryProposals = this.channel.queryByChaincode(queryByChaincodeRequest);
        return queryProposals;
    }

    /**
     * Invoke chaincode to update ledger
     * @param request
     * @return
     * @throws Exception
     */
    public CompletableFuture<BlockEvent.TransactionEvent> updateByChaincode(ChaincodeRequest request) throws Exception {

        ChaincodeID chaincodeID = request.buildChaincodeID();

        TransactionProposalRequest transactionProposalRequest = this.hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(request.getLanguage());
        transactionProposalRequest.setFcn(request.getFunction());
        transactionProposalRequest.setProposalWaitTime(request.getProposalWaitTime());
        transactionProposalRequest.setArgs(request.getArgs());

        Map<String, byte[]> tmap = new HashMap<>();
        tmap.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); //Just some extra junk in transient map
        tmap.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
        transactionProposalRequest.setTransientMap(tmap);
        //FIXME sent to channel.getPeers() or getEndorsingPeers()?
        //  Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposalToEndorsers(transactionProposalRequest);
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest);
        // Check that all the proposals are consistent with each other. We should have only one set
        // where all the proposals above are consistent. Note the when sending to Orderer this is done automatically.
        //  Shown here as an example that applications can invoke and select.
        // See org.hyperledger.fabric.sdk.proposal.consistency_validation config property.
        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            String error = "Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size();
            logger.error(error);
            FabricUtils.debugProposalResponse(transactionPropResp);
            throw new IllegalStateException(error);
        }

        Collection<ProposalResponse> successful = new ArrayList<>(transactionPropResp.size());
        Collection<ProposalResponse> failed = new ArrayList<>(transactionPropResp.size());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Successful transaction proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
                }
                successful.add(response);
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed transaction proposal response " + response);
                }
                failed.add(response);
            }
        }

        //FIXME All must be successful?
        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            String error = "Not enough endorsers for invoke " + request.getFunction() + " " + failed.size() + " endorser error: " +
                    firstTransactionProposalResponse.getMessage() + ". Was verified: " + firstTransactionProposalResponse.isVerified();
            logger.error(error);
            FabricUtils.debugProposalResponse(transactionPropResp);
            throw new IllegalStateException(error);
        }
        //BlockEvent.TransactionEvent event = channel.sendTransaction(successful).get(32000, TimeUnit.SECONDS);
        return channel.sendTransaction(successful);
    }


    @Override
    public void close()  {
        this.channel.shutdown(true);
        //FIXME Do more research
        try {
            ExecutorService service = (ExecutorService) MethodUtils.invokeMethod(this.hfClient, true, "getExecutorService");
            service.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
