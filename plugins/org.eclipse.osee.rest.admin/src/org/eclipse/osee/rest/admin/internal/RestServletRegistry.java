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

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import javax.ws.rs.core.Application;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestServletRegistry {

   private final ConcurrentHashMap<String, LazyObject<RestServletContainer>> servlets =
      new ConcurrentHashMap<String, LazyObject<RestServletContainer>>();

   private final Log logger;
   private final HttpService httpService;
   private final RestComponentFactory factory;

   public RestServletRegistry(Log logger, HttpService httpService, RestComponentFactory factory) {
      super();
      this.logger = logger;
      this.httpService = httpService;
      this.factory = factory;
   }

   public void register(ServiceReference<Application> reference) throws Exception {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      LazyObject<RestServletContainer> provider = getProvider(contextName);
      RestServletContainer container = provider.get();

      Bundle bundle = reference.getBundle();
      Application application = bundle.getBundleContext().getService(reference);
      container.addApplication(componentName, bundle, application);

      container.reload();
   }

   public void deregister(ServiceReference<Application> reference) {
      String componentName = RestServiceUtils.getComponentName(reference);
      String contextName = RestServiceUtils.getContextName(reference);

      LazyObject<RestServletContainer> provider = getProvider(contextName);
      RestServletContainer container = provider.get();

      container.removeApplication(componentName);
      if (container.isEmpty()) {
         removeContainer(provider);
      } else {
         container.reload();
      }
   }

   public void cleanUp() {
      Iterator<LazyObject<RestServletContainer>> iterator = servlets.values().iterator();
      while (iterator.hasNext()) {
         LazyObject<RestServletContainer> provider = iterator.next();
         RestServletContainer container = provider.get();
         container.cleanUp();
         removeContainer(provider);
         iterator.remove();
      }
      servlets.clear();
   }

   private void removeContainer(LazyObject<RestServletContainer> provider) {
      RestServletContainer container = provider.get();
      String contextName = container.getContext();

      logger.debug("Remove - servlet context[%s]", contextName);
      servlets.remove(contextName);
      httpService.unregister(contextName);
      container.destroy();
      provider.invalidate();
   }

   private LazyObject<RestServletContainer> getProvider(final String contextName) {
      LazyObject<RestServletContainer> provider = servlets.get(contextName);
      if (provider == null) {
         LazyObject<RestServletContainer> newProvider = new ServletContainerProvider(contextName);
         provider = servlets.putIfAbsent(contextName, newProvider);
         if (provider == null) {
            provider = newProvider;
         }
      }
      return provider;
   }

   private class ServletContainerProvider extends LazyObject<RestServletContainer> implements ObjectProvider<RestServletContainer> {

      private final String contextName;

      public ServletContainerProvider(String contextName) {
         super();
         this.contextName = contextName;
      }

      @Override
      protected FutureTask<RestServletContainer> createLoaderTask() {
         Callable<RestServletContainer> newCallable = new Callable<RestServletContainer>() {
            @Override
            public RestServletContainer call() throws Exception {
               logger.debug("Add - servlet context[%s]", contextName);
               RestServletContainer container = factory.createContainer(contextName);

               String name = container.getContext();
               ServletContainer servletContainer = container.getContainer();
               HttpContext httpContext = container.getHttpContext();

               httpService.registerServlet(name, servletContainer, null, httpContext);
               return container;
            }
         };
         return new FutureTask<RestServletContainer>(newCallable);
      }
   }

}
