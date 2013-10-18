package com.github.ddth.orestws.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.felix.framework.util.Util;
import org.apache.felix.main.AutoProcessor;

public class StandaloneBootstrap {

    private static File ORESTWS_HOME;
    private static File ORESTWS_OSGI_PROPERTIES;
    
    
    public static void main(String[] args) throws IOException {
        checkEnv();
        initFelixFramework();
        // initBundleConfigDao();
        // startAllBundles();
    }

    private static void checkEnv() {
        JAVA_OPTS+=("-Dorestws.home=$ORESTWS_HOME")
                JAVA_OPTS+=("-Dorestws.osgi.properties=$ORESTWS_HOME/bin/osgi-felix.properties")
    }

    private static void initFelixFramework() throws IOException {
        Properties configProps = loadConfigProperties();
        System.out.println(configProps);
    }

    private static Properties loadConfigProperties() throws IOException {
        File configFile = new File(System.getProperty("osgiConfigFile"));
        Properties configProps = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(configFile);
            configProps.load(fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String instanceRandomStr = df.format(date) + "_" + RandomStringUtils.randomAlphanumeric(8);

        // perform variables substitution for system properties.
        for (Enumeration<?> e = configProps.propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = configProps.getProperty(name);
            if (value != null) {
                value = value.replace("${random}", instanceRandomStr);
            }
            value = Util.substVars(value, name, null, configProps);
            configProps.setProperty(name, value);
        }

        return configProps;
    }

    private static void _test(Properties configProps) {
        File configFile = null;
        // configure Felix auto-deploy directory
        String sAutoDeployDir = configProps.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY);
        if (sAutoDeployDir == null) {
            throw new RuntimeException("Can not find configuration ["
                    + AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + "] in file "
                    + configFile.getAbsolutePath());
        }
        // File fAutoDeployDir = new File(renderOsgiContainerLocation(),
        // sAutoDeployDir);
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + ": "
        // + fAutoDeployDir.getAbsolutePath());
        // }
        // configProps.setProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
        // fAutoDeployDir.getAbsolutePath());

        // IdGenerator idGen =
        // IdGenerator.getInstance(IdGenerator.getMacAddr());
        // DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // Date date = new Date();
        // String instanceRandomStr = df.format(date) + "_"
        // + System.getProperty(SYSPROP_SPRING_PROFILE, "") + "_" +
        // idGen.generateId48Hex();
        // // perform variables substitution for system properties.
        // for (Enumeration<?> e = configProps.propertyNames();
        // e.hasMoreElements();) {
        // String name = (String) e.nextElement();
        // String value = configProps.getProperty(name);
        // if (value != null) {
        // value = value.replace("${random}", instanceRandomStr);
        // }
        // value = Util.substVars(value, name, null, configProps);
        // configProps.setProperty(name, value);
        // }
        //
        // // configure Felix auto-deploy directory
        // String sAutoDeployDir =
        // configProps.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY);
        // if (sAutoDeployDir == null) {
        // throw new RuntimeException("Can not find configuration ["
        // + AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + "] in file "
        // + configFile.getAbsolutePath());
        // }
        // File fAutoDeployDir = new File(renderOsgiContainerLocation(),
        // sAutoDeployDir);
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + ": "
        // + fAutoDeployDir.getAbsolutePath());
        // }
        // configProps.setProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
        // fAutoDeployDir.getAbsolutePath());
        //
        // // configure Felix temp (storage) directory
        // String sCacheDir =
        // configProps.getProperty(Constants.FRAMEWORK_STORAGE);
        // if (sCacheDir == null) {
        // throw new RuntimeException("Can not find configuration [" +
        // Constants.FRAMEWORK_STORAGE
        // + "] in file " + configFile.getAbsolutePath());
        // } else if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug(Constants.FRAMEWORK_STORAGE + ": " + sCacheDir);
        // }
        // File fCacheDir = new File(sCacheDir);
        // String contextPath = DaspGlobal.getServletContext().getContextPath();
        // if (contextPath.equals("")) {
        // contextPath = "_";
        // }
        // fCacheDir = new File(fCacheDir, contextPath);
        // configProps.setProperty(Constants.FRAMEWORK_STORAGE,
        // fCacheDir.getAbsolutePath());
        //
        // // configure Felix's File Install watch directory
        // String sMonitorDir =
        // configProps.getProperty("felix.fileinstall.dir");
        // if (sMonitorDir != null) {
        // File fMonitorDir = new File(renderOsgiContainerLocation(),
        // sMonitorDir);
        // configProps.setProperty("felix.fileinstall.dir",
        // fMonitorDir.getAbsolutePath());
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("felix.fileinstall.dir: " +
        // fMonitorDir.getAbsolutePath());
        // }
        // }
        //
        // // configure Felix's Remote Shell listen IP & Port
        // if (remoteShellListenIp != null) {
        // configProps.setProperty("osgi.shell.telnet.ip", remoteShellListenIp);
        // }
        // if (remoteShellListenPort > 0) {
        // configProps
        // .setProperty("osgi.shell.telnet.port",
        // String.valueOf(remoteShellListenPort));
        // }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Remote Shell: " + remoteShellListenIp + ":" +
        // remoteShellListenPort);
        // }
        //
        // return configProps;

    }

}
