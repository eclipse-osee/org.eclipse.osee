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
package org.eclipse.osee.jaxrs.server.internal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Application;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author David W. Miller
 */
public class RestServletContainer {
   private final Map<String, Application> applications = new ConcurrentHashMap<String, Application>();

   private final Log logger;
   private final String baseContext;
   private final Map<String, Bundle> bundles;
   private final ServletContainer container;
   private final DefaultResourceConfig resourceConfig;
   private final HttpContext context;
   private final BundleWadlGeneratorConfig wadl;

   public RestServletContainer(Log logger, String baseContext, Map<String, Bundle> bundles, ServletContainer container, DefaultResourceConfig resourceConfig, HttpContext context, BundleWadlGeneratorConfig wadl) {
      super();
      this.logger = logger;
      this.baseContext = baseContext;
      this.bundles = bundles;
      this.container = container;
      this.resourceConfig = resourceConfig;
      this.context = context;
      this.wadl = wadl;
   }

   public String getContext() {
      return baseContext;
   }

   public void addApplication(String key, Bundle bundle, Application application) {
      logger.debug("Add - servlet context[%s] - application[%s]", getContext(), key);
      applications.put(key, application);
      resourceConfig.add(application);
      bundles.put(key, bundle);
      updateWadlSupport();
   }

   private void updateWadlSupport() {
      Map<String, Object> properties = resourceConfig.getProperties();
      if (wadl.hasExtendedWadl()) {
         properties.put(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG, wadl);
      } else {
         properties.remove(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG);
      }
   }

   public void removeApplication(String key) {
      logger.debug("Remove - servlet context[%s] - application[%s]", getContext(), key);
      Application remove = applications.remove(key);
      if (remove != null) {
         removeResources(resourceConfig.getClasses(), remove.getClasses());
         removeResources(resourceConfig.getSingletons(), remove.getSingletons());
      }
      Bundle bundle = bundles.remove(key);
      if (bundle != null) {
         updateWadlSupport();
      }
   }

   public void cleanUp() {
      for (String key : applications.keySet()) {
         removeApplication(key);
      }
   }

   private void removeResources(Set<?> set, Set<?> toRemove) {
      set.removeAll(toRemove);
   }

   public boolean isEmpty() {
      return applications.isEmpty();
   }

   public void destroy() {
      getContainer().destroy();
   }

   public void reload() {
      getContainer().reload();
   }

   public ServletContainer getContainer() {
      return container;
   }

   public HttpContext getHttpContext() {
      return context;
   }

}
