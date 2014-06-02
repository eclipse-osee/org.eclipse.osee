/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.applications.AbstractJaxRsApplicationContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.AbstractJaxRsContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainerProvider;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsContainerProviderImpl;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsFactory;
import org.eclipse.osee.jaxrs.server.internal.exceptions.GenericExceptionMapper;
import org.eclipse.osee.jaxrs.server.internal.filters.SecureResourceFilterFactory;
import org.eclipse.osee.jaxrs.server.internal.filters.SecurityContextFilter;
import org.eclipse.osee.jaxrs.server.internal.resources.ServicesResource;
import org.eclipse.osee.logger.Log;
import org.osgi.service.http.HttpService;
import com.sun.jersey.api.container.filter.UriConnegFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public final class JerseyJaxRsFactory implements JaxRsFactory {

   private final Log logger;
   private final HttpService httpService;
   private final SecurityContextFilter securityContextFilter;

   private List<Object> singletons;
   private List<Object> requestFilters;

   public JerseyJaxRsFactory(Log logger, HttpService httpService, SecurityContextFilter securityContextFilter) {
      super();
      this.logger = logger;
      this.httpService = httpService;
      this.securityContextFilter = securityContextFilter;
   }

   @Override
   public JaxRsContainerProvider newJaxRsContainerProvider(String contextName) {
      return new JaxRsContainerProviderImpl(this, contextName);
   }

   @Override
   public JaxRsContainer newJaxRsContainer(String contextName) {
      Dictionary<String, Object> props = new Hashtable<String, Object>();
      JerseyJaxRsContainer container = new JerseyJaxRsContainer(logger, httpService, props);
      container.setServletContext(contextName);
      logger.trace("Create - [%s]", container);
      return container;
   }

   private List<Object> getResourceSingletons() {
      if (singletons == null) {
         List<Object> resources = new ArrayList<Object>();
         resources.add(new GenericExceptionMapper(logger));
         singletons = resources;
      }
      return singletons;
   }

   private List<Object> getRequestFilters() {
      if (requestFilters == null) {
         List<Object> resources = new ArrayList<Object>();
         Map<String, MediaType> mappings = new HashMap<String, MediaType>();
         mappings.put("xml", MediaType.APPLICATION_XML_TYPE);
         mappings.put("json", MediaType.APPLICATION_JSON_TYPE);
         UriConnegFilter filter1 = new UriConnegFilter(mappings);
         resources.add(filter1);
         requestFilters = resources;
      }
      return requestFilters;
   }

   private List<ResourceFilterFactory> getResourceFilterFactories() {
      SecureResourceFilterFactory filterFactory = new SecureResourceFilterFactory(logger, securityContextFilter);
      return Collections.<ResourceFilterFactory> singletonList(filterFactory);
   }

   private JerseyJaxRsApplicationContainer newApplicationContainer(String applicationContext) {
      return new JerseyJaxRsApplicationContainer(applicationContext);
   }

   private DefaultResourceConfig newResourceConfig() {
      DefaultResourceConfig config = new DefaultResourceConfig();
      Map<String, Object> properties = config.getProperties();
      properties.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, getRequestFilters());
      properties.put(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, getResourceFilterFactories());
      for (Object resource : getResourceSingletons()) {
         config.getSingletons().add(resource);
      }
      return config;
   }

   private HttpServlet newBaseJaxsRsServlet(JaxRsVisitable visitable) {
      DefaultResourceConfig config = newResourceConfig();
      config.getSingletons().add(new ServicesResource(visitable));
      return new ServletContainer(config);
   }

   private HttpServlet newApplicationServlet(JaxRsVisitable visitable, Application application) {
      DefaultResourceConfig config = newResourceConfig();
      config.add(application);
      JerseyWadlGeneratorConfig wadl = new JerseyWadlGeneratorConfig(logger, visitable);
      Map<String, Object> properties = config.getProperties();
      if (wadl.hasExtendedWadl()) {
         properties.put(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG, wadl);
      } else {
         properties.remove(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG);
      }
      return new ServletContainer(config);
   }

   private final class JerseyJaxRsContainer extends AbstractJaxRsContainer<HttpServlet, JerseyJaxRsApplicationContainer> {

      public JerseyJaxRsContainer(Log logger, HttpService httpService, Dictionary<String, Object> props) {
         super(logger, httpService, props);
      }

      @Override
      protected JerseyJaxRsApplicationContainer createApplicationContainer(String applicationContext) {
         return newApplicationContainer(applicationContext);
      }

      @Override
      protected HttpServlet createBaseJaxsRsServlet(JaxRsVisitable visitable) {
         return newBaseJaxsRsServlet(visitable);
      }

      @Override
      protected void startContainer(JerseyJaxRsApplicationContainer container) {
         String baseContext = getServletContext();
         Dictionary<String, Object> properties = getServletProperties();
         HttpService httpService = getHttpService();
         container.startContainer(httpService, baseContext, properties);
      }

      @Override
      protected void stopContainer(JerseyJaxRsApplicationContainer container) {
         String baseContext = getServletContext();
         HttpService httpService = getHttpService();
         container.stopContainer(httpService, baseContext);
      }

   };

   private final class JerseyJaxRsApplicationContainer extends AbstractJaxRsApplicationContainer {

      private final AtomicBoolean isRegistered = new AtomicBoolean(false);

      public JerseyJaxRsApplicationContainer(String applicationContext) {
         super(applicationContext);
      }

      public void startContainer(HttpService httpService, String baseContext, Dictionary<String, Object> props) {
         if (!isRegistered.getAndSet(true)) {
            String contextName = getAbsoluteContext(baseContext);
            Application application = getApplication();
            HttpServlet servlet = newApplicationServlet(this, application);
            try {
               httpService.registerServlet(contextName, servlet, props, null);
            } catch (Exception ex) {
               throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR,
                  "Error registering servlet [%s] ", contextName);
            }
         }
      }

      public void stopContainer(HttpService httpService, String baseContext) {
         if (isRegistered.getAndSet(false)) {
            String contextName = getAbsoluteContext(baseContext);
            httpService.unregister(contextName);
         }
      }

      private String getAbsoluteContext(String baseContext) {
         String appPath = JaxRsUtils.normalize(getApplicationContext());
         String path = String.format("%s%s", baseContext, appPath);
         path = path.replaceAll("//", "/");
         return path;
      }
   }

}
