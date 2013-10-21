package com.github.ddth.orestws.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.framework.util.Util;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneBootstrap {

    private static Logger LOGGER = LoggerFactory.getLogger(StandaloneBootstrap.class);

    private static Framework framework;
    private static File ORESTWS_HOME;
    private static File ORESTWS_OSGI_PROPERTIES;

    public static void main(String[] args) throws IOException, BundleException,
            InterruptedException {
        checkEnv();
        initFelixFramework();
        // initBundleConfigDao();
        // startAllBundles();

        Runtime.getRuntime().addShutdownHook(new Thread("ORESTWS Shutdown Hook") {
            public void run() {
                try {
                    if (framework != null) {
                        framework.stop();
                        framework.waitForStop(0);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error stopping framework", e);
                }
            }
        });

        framework.waitForStop(0);
    }

    private static void checkEnv() {
        final String PROP_ORESTWS_HOME = "orestws.home";
        String orestwsHome = System.getProperty(PROP_ORESTWS_HOME);
        if (StringUtils.isBlank(orestwsHome)) {
            throw new IllegalArgumentException("[" + PROP_ORESTWS_HOME
                    + "] is not defined. Please define it with -D" + PROP_ORESTWS_HOME);
        }
        ORESTWS_HOME = new File(orestwsHome);
        if (!ORESTWS_HOME.isDirectory()) {
            throw new IllegalArgumentException("[" + ORESTWS_HOME.getAbsolutePath()
                    + "] is not a directory");
        }

        final String PROP_ORESTWS_OSGI_PROPERTIES = "orestws.osgi.properties";
        String orestwsOsgiProperties = System.getProperty(PROP_ORESTWS_OSGI_PROPERTIES);
        if (StringUtils.isBlank(orestwsOsgiProperties)) {
            throw new IllegalArgumentException("[" + PROP_ORESTWS_OSGI_PROPERTIES
                    + "] is not defined. Please define it with -D" + PROP_ORESTWS_OSGI_PROPERTIES);
        }
        ORESTWS_OSGI_PROPERTIES = new File(orestwsOsgiProperties);
        if (!ORESTWS_OSGI_PROPERTIES.isFile() || !ORESTWS_OSGI_PROPERTIES.canRead()) {
            throw new IllegalArgumentException("[" + ORESTWS_OSGI_PROPERTIES.getAbsolutePath()
                    + "] is not a file or not readable");
        }
    }

    private static void initFelixFramework() throws IOException, BundleException {
        Properties configProps = _loadOsgiConfigProperties();

        // configure Felix auto-deploy directory
        String sAutoDeployDir = configProps.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY);
        if (sAutoDeployDir == null) {
            throw new RuntimeException("Can not find configuration ["
                    + AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + "] in file "
                    + ORESTWS_OSGI_PROPERTIES.getAbsolutePath());
        }
        File fAutoDeployDir = new File(ORESTWS_HOME, sAutoDeployDir);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + ": "
                    + fAutoDeployDir.getAbsolutePath());
        }
        configProps.setProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
                fAutoDeployDir.getAbsolutePath());

        // configure Felix temp (storage) directory
        String sCacheDir = configProps.getProperty(Constants.FRAMEWORK_STORAGE);
        if (sCacheDir == null) {
            throw new RuntimeException("Can not find configuration [" + Constants.FRAMEWORK_STORAGE
                    + "] in file " + ORESTWS_OSGI_PROPERTIES.getAbsolutePath());
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Constants.FRAMEWORK_STORAGE + ": " + sCacheDir);
        }
        File fCacheDir = new File(ORESTWS_HOME, sCacheDir);
        configProps.setProperty(Constants.FRAMEWORK_STORAGE, fCacheDir.getAbsolutePath());

        // configure Felix's File Install watch directory
        final String PROP_FELIX_FILE_INSTALL_DIR = "felix.fileinstall.dir";
        String sMonitorDir = configProps.getProperty(PROP_FELIX_FILE_INSTALL_DIR);
        if (sMonitorDir != null) {
            File fMonitorDir = new File(ORESTWS_HOME, sMonitorDir);
            configProps.setProperty(PROP_FELIX_FILE_INSTALL_DIR, fMonitorDir.getAbsolutePath());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(PROP_FELIX_FILE_INSTALL_DIR + ": " + fMonitorDir.getAbsolutePath());
            }
        }

        // check for Felix's Remote Shell listen IP & Port
        if (LOGGER.isDebugEnabled()) {
            String remoteShellListenIp = configProps.getProperty("osgi.shell.telnet.ip");
            String remoteShellListenPort = configProps.getProperty("osgi.shell.telnet.port");
            LOGGER.debug("Remote Shell: " + remoteShellListenIp + ":" + remoteShellListenPort);
        }

        Map<String, String> config = new HashMap<String, String>();
        for (Entry<Object, Object> entry : configProps.entrySet()) {
            config.put(entry.getKey().toString(), entry.getValue().toString());
        }
        FrameworkFactory factory = new org.apache.felix.framework.FrameworkFactory();
        framework = factory.newFramework(config);
        framework.init();
        AutoProcessor.process(configProps, framework.getBundleContext());
        _autoDeployBundles(fAutoDeployDir, framework);
        framework.start();
    }

    private static void _autoDeployBundles(File fileOrDir, Framework framework)
            throws FileNotFoundException, BundleException {
        if (fileOrDir.isFile() && fileOrDir.getName().endsWith(".jar")) {
            _deployBundle(fileOrDir.getName(), fileOrDir);
        } else {
            // make sure bundles are deployed and started in order!
            File[] files = fileOrDir.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    return file1.compareTo(file2);
                }
            });
            for (File f : files) {
                if (!f.getName().startsWith(".")) {
                    _autoDeployBundles(f, framework);
                }
            }
        }
    }

    private static Bundle _deployBundle(String bundleId, File file) throws BundleException,
            FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return _deployBundle(bundleId, fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private static Bundle _deployBundle(String bundleId, InputStream is) throws BundleException {
        return _deployBundle(bundleId, is, false);
    }

    private static Bundle _deployBundle(String bundleId, InputStream is, boolean start)
            throws BundleException {
        Bundle bundle = framework.getBundleContext().installBundle(bundleId, is);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Bundle [" + bundle + "] has been deployed.");
        }
        if (start) {
            _startBundle(bundle);
        }
        return bundle;
    }

    private static void _startBundle(Bundle bundle) throws BundleException {
        int state = bundle.getState();
        if ((state == Bundle.RESOLVED || state == Bundle.INSTALLED)
                && bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
            bundle.start();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Bundle [" + bundle + "] has been started.");
            }
        }
    }

    private static Properties _loadOsgiConfigProperties() throws IOException {
        Properties configProps = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ORESTWS_OSGI_PROPERTIES);
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

}
