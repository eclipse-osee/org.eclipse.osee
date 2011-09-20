/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.admin.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.RestAdminConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestServletManager {

   private final Map<String, ServletContainer> registeredServlets = new ConcurrentHashMap<String, ServletContainer>();
   private final List<ServiceReference<Application>> pending =
      new CopyOnWriteArrayList<ServiceReference<Application>>();

   private HttpService httpService;
   private Log logger;
   private EventService eventService;
   private Thread thread;

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   private HttpService getHttpService() {
      return httpService;
   }

   private Log getLogger() {
      return logger;
   }

   private EventService getEventService() {
      return eventService;
   }

   public void start() throws Exception {
      thread = new Thread("Register Pending Rest Services") {
         @Override
         public void run() {
            for (ServiceReference<Application> reference : pending) {
               register(reference);
            }
            pending.clear();
         }
      };
      thread.start();
   }

   public void stop() {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
   }

   private boolean isReady() {
      return httpService != null && logger != null;
   }

   public void addApplication(ServiceReference<Application> reference) {
      if (isReady()) {
         register(reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeApplication(ServiceReference<Application> reference) {
      if (isReady()) {
         unregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private void unregister(ServiceReference<Application> reference) {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      getLogger().debug("De-registering servlet for '%s' with alias '%s'\n", componentName, contextName);
      HttpServlet servlet = registeredServlets.remove(componentName);
      if (servlet != null) {
         getHttpService().unregister(contextName);
         servlet.destroy();
      }
      notifyDeRegistration(componentName, contextName);
   }

   private void register(ServiceReference<Application> reference) {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      try {
         ServletContainer servlet = createContainer(reference);
         HttpContext httpContext = createHttpContext(reference);
         getHttpService().registerServlet(contextName, servlet, null, httpContext);
         registeredServlets.put(componentName, servlet);
         notifyRegistration(componentName, contextName);
         getLogger().debug("Registered servlet for '%s' with alias '%s'\n", componentName, contextName);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private HttpContext createHttpContext(ServiceReference<Application> reference) {
      Bundle bundle = reference.getBundle();
      return new BundleHttpContext(bundle);
   }

   private ServletContainer createContainer(ServiceReference<Application> reference) throws Exception {
      Bundle bundle = reference.getBundle();
      Application application = bundle.getBundleContext().getService(reference);
      RestServiceUtils.checkValid(application);
      return new ServletContainer(application);
   }

   private void notifyRegistration(String componentName, String contextName) {
      Map<String, String> data = RestServiceUtils.toMap(componentName, contextName);
      getEventService().postEvent(RestAdminConstants.REST_REGISTRATION_EVENT, data);
   }

   private void notifyDeRegistration(String componentName, String contextName) {
      Map<String, String> data = RestServiceUtils.toMap(componentName, contextName);
      getEventService().postEvent(RestAdminConstants.REST_DEREGISTRATION_EVENT, data);
   }

}
