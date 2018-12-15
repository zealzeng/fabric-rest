package com.luoying.fabricrest;

import com.luoying.server.http.DefaultHttpServer;
import com.luoying.server.http.HttpService;

import java.io.File;
import java.io.IOException;

/**
 * Fabric rest server
 * Created by Zeal on 2018/9/15 0015.
 */
public class RestServer {

    /** Listening port */
    private int port = 8080;

    /** Fabric rest service */
    private RestService restService = null;

    /** Http server */
    private DefaultHttpServer server = null;

    /**
     * @param port
     * @param appProfileDir
     * @throws IOException
     */
    public RestServer(int port, File appProfileDir) throws IOException {
        this.setPort(port);
        RestServiceConfig serviceConfig = new RestServiceConfig(appProfileDir);
        this.restService = new RestService(serviceConfig);
    }

    /**
     * @param port
     * @param serviceConfig
     * @throws IOException
     */
    public RestServer(int port, RestServiceConfig serviceConfig) throws IOException {
        this.setPort(port);
        this.restService = new RestService(serviceConfig);
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     * @return
     */
    public RestServer setPort(int port) {
        if (port <= 0) {
            throw new IllegalStateException("Invalid port " + port);
        }
        this.port = port;
        return this;
    }

    /**
     * Start up fabric rest server
     * @throws Exception
     */
    public void startup() throws Exception {
        if (this.server != null) {
            throw new IllegalStateException("Fabric rest server is already started");
        }
        //FIXME Enhance luoying server to support more configurations for http server
        server = new DefaultHttpServer(8080, this.restService);
        this.server.startup();
    }

    /**
     * Shut down fabric rest server
     */
    public void shutdown() {
        if (this.server == null) {
            throw new IllegalStateException("Fabric rest server is not started yet");
        }
        this.server.shutdown();
    }

    public static void main(String[] args) throws Exception {
        File appProfileDir = new File("D:/Project/baas/fabric-rest/app-profile");
        RestServer restServer = new RestServer(8080, appProfileDir);
        restServer.startup();
    }

}
