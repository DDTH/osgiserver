package com.github.ddth.example.simple;

import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private final static String PROP_MAPPING = "mapping";

    private ServiceTracker httpTracker;
    private Logger LOGGER = LoggerFactory.getLogger(Activator.class);
    private ServiceRegistration<?> serviceHello, serviceHelloJson;

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
                // register 2 APIs via ServiceTracker
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    final String mappingHello = "/example-simple/hello";
                    Hashtable<String, String> props = new Hashtable<String, String>();
                    props.put(PROP_MAPPING, mappingHello);
                    httpService.registerServlet(mappingHello, new HelloWorldServlet(), props, null);

                    final String mappingHelloJson = "/example-simple/helloJson";
                    props = new Hashtable<String, String>();
                    props.put(PROP_MAPPING, mappingHelloJson);
                    httpService.registerServlet(mappingHelloJson, new HelloJsonServlet(), props,
                            null);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return httpService;
            }
        };
        // start tracking all HTTP services...
        httpTracker.open();

        // register 2 service using the Whiteboard
        // note: use Servlet.class, not HttpServlet.class
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("alias", "/example-simple/helloWhiteboard");
        this.serviceHello = context.registerService(Servlet.class, new HelloWorldServlet(), props);

        props = new Hashtable<String, String>();
        props.put("alias", "/example-simple/helloJsonWhiteboard");
        this.serviceHelloJson = context.registerService(Servlet.class, new HelloJsonServlet(),
                props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceHello != null) {
            serviceHello.unregister();
        }

        if (serviceHelloJson != null) {
            serviceHelloJson.unregister();
        }

        if (httpTracker != null) {
            // stop tracking all HTTP services...
            httpTracker.close();
        }
    }

}
