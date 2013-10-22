package com.github.ddth.example.simple;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private final static String PROP_MAPPING = "mapping";

    private ServiceTracker httpTracker;
    private Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void remove(ServiceReference reference) {
                try {
                    Object mapping = reference.getProperty(PROP_MAPPING);
                    if (mapping != null) {
                        HttpService service = (HttpService) this.context.getService(reference);
                        if (service != null) {
                            service.unregister(mapping.toString());
                        }
                    }
                } catch (IllegalArgumentException exception) {
                    // Ignore; servlet registration probably failed earlier
                    // on...
                }
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object addingService(ServiceReference reference) {
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    final String mappingHello = "/example-simple/hello";
                    Dictionary<String, Object> dictHello = new Hashtable();
                    dictHello.put(PROP_MAPPING, mappingHello);
                    httpService.registerServlet(mappingHello, new HelloWorldServlet(), dictHello,
                            null);

                    final String mappingHelloJson = "/example-simple/helloJson";
                    Dictionary<String, Object> dictHelloJson = new Hashtable();
                    dictHelloJson.put(PROP_MAPPING, mappingHelloJson);
                    httpService.registerServlet(mappingHelloJson, new HelloJsonServlet(),
                            dictHelloJson, null);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return httpService;
            }
        };
        // start tracking all HTTP services...
        httpTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (httpTracker != null) {
            // stop tracking all HTTP services...
            httpTracker.close();
        }
    }

}
