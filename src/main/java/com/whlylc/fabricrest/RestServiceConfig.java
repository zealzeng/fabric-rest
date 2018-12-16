package com.whlylc.fabricrest;

import com.whlylc.fabricrest.vo.FabricError;

import java.io.File;

/**
 * Fabric rest service configuration
 * @author Zeal
 */

public class RestServiceConfig {

    public static final String APP_PROFILE_FILE = "app-profile.json";

    public static final String APP_SECRET_FILE = "app.secret";

    private File appProfileDir = null;

    public RestServiceConfig(File appProfileDir) {
        this.appProfileDir = appProfileDir;
        int resultCode = this.validate();
        if (resultCode != FabricError.NO_ERROR) {
            throw new IllegalStateException("Invalid rest service configuration, error code is " +  resultCode);
        }
    }

    /**
     * @return
     */
    private int validate() {
        if (this.appProfileDir == null || !this.appProfileDir.isDirectory() || !this.appProfileDir.exists()) {
            return FabricError.INVALID_APP_PROFILE_DIR;
        }
        return FabricError.NO_ERROR;
    }

    public File getAppProfileDir() {
        return appProfileDir;
    }

    public RestServiceConfig setAppProfileDir(File appProfileDir) {
        this.appProfileDir = appProfileDir;
        return this;
    }

}
