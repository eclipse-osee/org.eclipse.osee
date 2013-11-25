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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Application;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author David W. Miller
 */
public class RestServletContainer {
   private final Map<String, Application> applications = new ConcurrentHashMap<String, Application>();
   private final Map<String, Bundle> bundles = new ConcurrentHashMap<String, Bundle>();

   private final ServletContainer container;
   private final DefaultResourceConfig resourceConfig;
   private final HttpContext context;
   private final BundleWadlGeneratorConfig wadl;

   public RestServletContainer(Log logger, ServletContainer container, DefaultResourceConfig resourceConfig) {
      super();
      this.container = container;
      this.resourceConfig = resourceConfig;
      this.wadl = new BundleWadlGeneratorConfig(logger, bundles.values());
      this.context = new BundleHttpContext(bundles.values());
   }

   public void addApplication(String key, ServiceReference<Application> reference) {
      Bundle bundle = reference.getBundle();

      Application application = bundle.getBundleContext().getService(reference);
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

   public Iterable<String> getComponents() {
      return applications.keySet();
   }

   public boolean isEmpty() {
      return applications.isEmpty();
   }

   private void removeResources(Set<?> set, Set<?> toRemove) {
      set.removeAll(toRemove);
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
