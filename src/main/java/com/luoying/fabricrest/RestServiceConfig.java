package com.luoying.fabricrest;

import com.luoying.fabricrest.vo.FabricError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Fabric rest service configuration
 * @author Zeal
 */

public class RestServiceConfig {

    public static final String FABRIC_REST_CONFIG_FILE = "fabric-rest.properties";

//    public static final String APP_PROFILE_DIR = "app_profile_dir";

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

    //    public static final String FBC_CONFIG_ROOT = "fbc_config_root";


//    private Properties properties = null;

//    public RestServiceConfig(File configFile) throws IOException {
//        loadConfigFile(configFile);
//    }
//
//    private void loadConfigFile(File configFile) throws IOException {
//        Properties properties = new Properties();
//        try (FileInputStream fis = new FileInputStream(configFile)) {
//            properties.load(fis);
//        }
//
//    }
//
//    /**
//     * getProperty return back property for the given value.
//     *
//     * @param propertyKey
//     * @return String value for the property
//     */
//    public String getProperty(String propertyKey) {
//        return properties.getProperty(propertyKey);
//    }
//
//    /**
//     * Get property as file
//     * @param propertyKey
//     * @return
//     */
//    public File getFileProperty(String propertyKey) {
//        String str = this.properties.getProperty(propertyKey);
//        if (str != null && str.length() > 0) {
//            return new File(str);
//        }
//        else {
//            return null;
//        }
//    }

}
