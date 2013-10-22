package com.github.ddth.example.simple;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private ServiceTracker httpTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void remove(ServiceReference reference) {
                try {
                    Object mapping = reference.getProperty("mapping");
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
                    final String mapping = "/example-simple/hello1";
                    Dictionary<String, Object> dict = new Hashtable();
                    dict.put("mapping", mapping);
                    httpService.registerServlet("/example-simple/hello1", new HelloWorldServlet1(),
                            dict, null);
                } catch (Exception e) {
                    e.printStackTrace();
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
