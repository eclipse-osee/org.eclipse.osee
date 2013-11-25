/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.admin.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Application;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.RestAdminConstants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class RestServletRegistry {

   private final ConcurrentHashMap<String, RestServletContainer> servlets =
      new ConcurrentHashMap<String, RestServletContainer>();

   private final Log logger;
   private final HttpService httpService;
   private final EventService eventService;
   private final RestComponentFactory factory;

   public RestServletRegistry(Log logger, HttpService httpService, EventService eventService, RestComponentFactory factory) {
      super();
      this.logger = logger;
      this.httpService = httpService;
      this.eventService = eventService;
      this.factory = factory;
   }

   public void register(ServiceReference<Application> reference) throws Exception {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      boolean requiresRegistration = false;

      RestServletContainer newContainer = factory.createContainer(logger);

      RestServletContainer container = servlets.putIfAbsent(contextName, newContainer);
      if (container == null) {
         container = newContainer;
         requiresRegistration = true;
      }

      synchronized (container) {
         container.addApplication(componentName, reference);
         notifyRegistration(componentName, contextName);

         if (requiresRegistration) {
            httpService.registerServlet(contextName, container.getContainer(), null, container.getHttpContext());
            logger.debug("Registered servlet for [%s] with alias [%s]\n", componentName, contextName);
         } else {
            container.reload();
         }
      }
   }

   public void deregister(ServiceReference<Application> reference) {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      logger.debug("De-registering servlet for [%s] with alias [%s]\n", componentName, contextName);
      RestServletContainer container = servlets.get(contextName);
      if (container != null) {
         synchronized (container) {
            container.removeApplication(componentName);

            if (container.isEmpty()) {
               servlets.remove(contextName);
               httpService.unregister(contextName);
               container.destroy();
            } else {
               container.reload();
            }
         }
      }
      notifyDeRegistration(componentName, contextName);
   }

   private void notifyRegistration(String componentName, String contextName) {
      Map<String, String> data = RestServiceUtils.toMap(componentName, contextName);
      eventService.postEvent(RestAdminConstants.REST_REGISTRATION_EVENT, data);
   }

   private void notifyDeRegistration(String componentName, String contextName) {
      Map<String, String> data = RestServiceUtils.toMap(componentName, contextName);
      eventService.postEvent(RestAdminConstants.REST_DEREGISTRATION_EVENT, data);
   }

}
