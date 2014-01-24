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
import org.eclipse.osee.rest.admin.internal.resources.ApplicationsResource;
import org.osgi.framework.Bundle;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestComponentFactory {
   private final Log logger;
   private List<Object> defaultSingletonResources;

   public RestComponentFactory(Log logger) {
      this.logger = logger;
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
