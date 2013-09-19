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
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.RestAdminConstants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestServletRegistry {

   private final Map<String, ServletContainer> registeredServlets = new ConcurrentHashMap<String, ServletContainer>();

   private final Log logger;
   private final RestComponentFactory factory;
   private final HttpService httpService;
   private final EventService eventService;

   public RestServletRegistry(Log logger, RestComponentFactory factory, HttpService httpService, EventService eventService) {
      super();
      this.logger = logger;
      this.factory = factory;
      this.httpService = httpService;
      this.eventService = eventService;
   }

   public void register(ServiceReference<Application> reference) throws Exception {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      ServletContainer servlet = factory.createContainer(logger, reference);
      HttpContext httpContext = factory.createHttpContext(reference);
      httpService.registerServlet(contextName, servlet, null, httpContext);
      registeredServlets.put(componentName, servlet);
      logger.debug("Registered servlet for [%s] with alias [%s]\n", componentName, contextName);
      notifyRegistration(componentName, contextName);
   }

   public void deregister(ServiceReference<Application> reference) {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      logger.debug("De-registering servlet for [%s] with alias [%s]\n", componentName, contextName);
      HttpServlet servlet = registeredServlets.remove(componentName);
      if (servlet != null) {
         httpService.unregister(contextName);
         servlet.destroy();
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
