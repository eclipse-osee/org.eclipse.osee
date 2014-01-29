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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.internal.filters.SecureResourceFilterFactory;
import org.eclipse.osee.rest.admin.internal.filters.SecurityContextFilter;
import org.eclipse.osee.rest.admin.internal.resources.ApplicationsResource;
import org.osgi.framework.Bundle;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestComponentFactory {
   private final Log logger;
   private final SecurityContextFilter securityContextFilter;

   private List<Object> defaultSingletonResources;
   private List<ContainerRequestFilter> containerRequestFilters;

   public RestComponentFactory(Log logger, SecurityContextFilter securityContextFilter) {
      super();
      this.logger = logger;
      this.securityContextFilter = securityContextFilter;
   }

   public List<ContainerRequestFilter> getRequestFilters() {
      if (containerRequestFilters == null) {
         containerRequestFilters = Collections.<ContainerRequestFilter> singletonList(securityContextFilter);
      }
      return containerRequestFilters;
   }

   public List<ResourceFilterFactory> getResourceFilterFactories() {
      SecureResourceFilterFactory filterFactory = new SecureResourceFilterFactory(securityContextFilter);
      return Collections.<ResourceFilterFactory> singletonList(filterFactory);
   }

   public List<Object> getResourceSingletons() {
      if (defaultSingletonResources == null) {
         GenericExceptionMapper exceptionMapper = new GenericExceptionMapper(logger);
         defaultSingletonResources = Collections.<Object> singletonList(exceptionMapper);
      }
      return defaultSingletonResources;
   }

   public RestServletContainer createContainer(String context) throws Exception {
      DefaultResourceConfig config = new DefaultResourceConfig();

      for (Object resource : getResourceSingletons()) {
         config.getSingletons().add(resource);
      }

      Map<String, Bundle> bundleMap = new ConcurrentHashMap<String, Bundle>();
      ObjectProvider<Iterable<Bundle>> provider = newBundleProvider(bundleMap);
      config.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, getRequestFilters());
      config.getProperties().put(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, getResourceFilterFactories());

      BundleHttpContext bundleContext = new BundleHttpContext(provider);
      BundleWadlGeneratorConfig wadlGenerator = new BundleWadlGeneratorConfig(logger, provider);

      ApplicationsResource resource = new ApplicationsResource(provider);
      config.getSingletons().add(resource);

      ServletContainer container = new ServletContainer(config);
      return new RestServletContainer(logger, context, bundleMap, container, config, bundleContext, wadlGenerator);
   }

   private ObjectProvider<Iterable<Bundle>> newBundleProvider(final Map<String, Bundle> bundleMap) {
      return new ObjectProvider<Iterable<Bundle>>() {

         @Override
         public Iterable<Bundle> get() {
            return bundleMap.values();
         }
      };
   }

}
