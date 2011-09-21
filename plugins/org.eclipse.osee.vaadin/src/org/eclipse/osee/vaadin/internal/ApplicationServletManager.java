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
package org.eclipse.osee.vaadin.internal;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServlet;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.vaadin.ApplicationConstants;
import org.eclipse.osee.vaadin.ApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServletManager {

   private final Map<String, HttpServlet> registered = new ConcurrentHashMap<String, HttpServlet>();

   private final List<ServiceReference<ApplicationFactory>> pending =
      new CopyOnWriteArrayList<ServiceReference<ApplicationFactory>>();

   private HttpService httpService;
   private EventService eventService;
   private Log logger;
   private Thread thread;

   private Dictionary<String, String> initParams;

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private HttpService getHttpService() {
      return httpService;
   }

   private EventService getEventService() {
      return eventService;
   }

   private Log getLogger() {
      return logger;
   }

   public void start(Map<String, Object> properties) {
      initParams = ApplicationUtils.getConfigParams(properties);
      thread = new Thread("Register Pending Vaadin Applications") {
         @Override
         public void run() {
            for (ServiceReference<ApplicationFactory> reference : pending) {
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
      initParams = null;
   }

   private boolean isReady() {
      return getHttpService() != null && getLogger() != null && getEventService() != null && initParams != null;
   }

   public void addFactory(ServiceReference<ApplicationFactory> reference) {
      if (isReady()) {
         register(reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeFactory(ServiceReference<ApplicationFactory> reference) {
      if (isReady()) {
         unregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private void register(ServiceReference<ApplicationFactory> reference) {
      String componentName = ApplicationUtils.getComponentName(reference);
      String contextName = ApplicationUtils.getContextName(reference);

      try {
         Bundle bundle = reference.getBundle();
         ApplicationFactory applicationFactory = bundle.getBundleContext().getService(reference);

         ApplicationUtils.checkValid(applicationFactory);

         HttpServlet servlet = createContainer(applicationFactory);
         HttpContext httpContext = new BundleHttpContext(bundle);

         httpService.registerServlet(contextName, servlet, initParams, httpContext);
         registered.put(componentName, servlet);
         notifyRegistration(reference);
         getLogger().debug("Registered servlet for '%s' alias '%s'", componentName, contextName);
      } catch (Exception ex) {
         getLogger().error(ex, "Error registering servelt for '%s' alias '%s'", componentName, contextName);
      }
   }

   private HttpServlet createContainer(ApplicationFactory applicationFactory) throws Exception {
      ApplicationUtils.checkValid(applicationFactory);
      Set<ApplicationSession> sessions = new ConcurrentSkipListSet<ApplicationSession>();
      return new ApplicationServlet(sessions, applicationFactory);
   }

   private void unregister(ServiceReference<ApplicationFactory> reference) {
      String componentName = ApplicationUtils.getComponentName(reference);
      String contextName = ApplicationUtils.getContextName(reference);

      getLogger().debug("De-registering servlet for '%s' alias '%s'", componentName, contextName);
      HttpServlet servlet = registered.remove(componentName);
      if (servlet != null) {
         httpService.unregister(contextName);
         servlet.destroy();
      }
      notifyDeRegistration(reference);
   }

   private void notifyRegistration(ServiceReference<ApplicationFactory> reference) {
      Map<String, String> data = ApplicationUtils.toMap(reference);
      eventService.postEvent(ApplicationConstants.APP_REGISTRATION_EVENT, data);
   }

   private void notifyDeRegistration(ServiceReference<ApplicationFactory> reference) {
      Map<String, String> data = ApplicationUtils.toMap(reference);
      eventService.postEvent(ApplicationConstants.APP_DEREGISTRATION_EVENT, data);
   }
}
