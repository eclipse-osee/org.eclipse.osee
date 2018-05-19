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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper;
import org.apache.cxf.jaxrs.utils.ResourceUtils;
import org.apache.cxf.transport.common.gzip.GZIPFeature;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.osee.jaxrs.JacksonFeature;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.applications.AbstractJaxRsApplicationContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.AbstractJaxRsContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainerProvider;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsContainerProviderImpl;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsFactory;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsProvider;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsProviders;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public final class CxfJaxRsFactory implements JaxRsFactory {
   private Log logger;
   private HttpService httpService;
   private List<Feature> features;
   private List<? extends Object> providers;
   private Map<String, Object> properties;
   private Map<Object, Object> extensionMappings;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void start(Map<String, Object> props) {
      logger.debug("Starting [%s]...", getClass().getSimpleName());

      // Ensure CXF JAX-RS implementation is loaded
      RuntimeDelegate runtimeDelegate = new org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl();
      RuntimeDelegate.setInstance(runtimeDelegate);

      List<Object> providers = new ArrayList<>();
      WebApplicationExceptionMapper waem = new WebApplicationExceptionMapper();
      waem.setPrintStackTrace(true);
      waem.setAddMessageToResponse(true);

      providers.add(waem);
      providers.add(new GenericExceptionMapper(logger));
      providers.addAll(JacksonFeature.getProviders());
      this.providers = providers;

      List<Feature> features = new ArrayList<>();
      LoggingFeature loggingFeature = new LoggingFeature();
      loggingFeature.setPrettyLogging(true);

      features.add(loggingFeature);
      features.add(new GZIPFeature());
      this.features = features;

      Map<Object, Object> extensionMappings = new HashMap<>(6);
      extensionMappings.put("html", MediaType.TEXT_HTML);
      extensionMappings.put("txt", MediaType.TEXT_PLAIN);
      extensionMappings.put("xml", MediaType.APPLICATION_XML);
      extensionMappings.put("json", MediaType.APPLICATION_JSON);
      extensionMappings.put("gzip", "application/gzip");
      extensionMappings.put("zip", "application/zip");

      this.extensionMappings = extensionMappings;
      this.properties = props;
   }

   public void stop() {
      if (providers != null) {
         providers.clear();
         providers = null;
      }
      if (features != null) {
         features.clear();
         features = null;
      }
      if (extensionMappings != null) {
         extensionMappings.clear();
         extensionMappings = null;
      }
      properties = null;
   }

   private Map<Object, Object> getExtensionMappings() {
      return extensionMappings;
   }

   private List<? extends Object> getProviders() {
      return providers;
   }

   private List<Feature> getFeatures() {
      return features;
   }

   private Map<String, Object> getProperties() {
      return properties;
   }

   @Override
   public JaxRsContainerProvider newJaxRsContainerProvider(String contextName) {
      return new JaxRsContainerProviderImpl(this, contextName);
   }

   @Override
   public JaxRsContainer newJaxRsContainer(String contextName) {
      Dictionary<String, Object> props = new Hashtable<>();
      CxfJaxRsContainer container = new CxfJaxRsContainer(logger, httpService, props);
      container.setServletContext(contextName);
      logger.trace("Create - [%s]", container);
      return container;
   }

   private CxfJaxRsApplicationContainer newApplicationContainer(String applicationContext, JaxRsProviders provider) {
      return new CxfJaxRsApplicationContainer(applicationContext, provider);
   }

   private CXFNonSpringServlet newBaseJaxsRsServlet(JaxRsVisitable visitable) {
      return new CXFNonSpringServlet();
   }

   public Server newCxfServer(CXFNonSpringServlet servlet, String applicationPath, Application application, JaxRsProviders providers) {
      String contextName = servlet.getServletName();
      Bus bus = servlet.getBus();
      if (bus == null) {
         throw new OseeWebApplicationException(Status.INTERNAL_SERVER_ERROR,
            "Error initializing [%s] for application [%s] - bus was null", contextName, application);
      }

      boolean ignoreApplicationPath = true;
      boolean staticSubresourceResolution = true;
      JAXRSServerFactoryBean bean =
         ResourceUtils.createApplication(application, ignoreApplicationPath, staticSubresourceResolution);

      if (JaxRsUtils.hasPath(applicationPath)) {
         String subAddress = JaxRsUtils.normalize(applicationPath);
         bean.setAddress(subAddress);
      }

      if (providers.hasProviders()) {
         for (JaxRsProvider container : providers.getProviders()) {
            bean.setProvider(container.getProvider());
         }
      }

      bean.setProviders(getProviders());
      bean.setFeatures(getFeatures());
      bean.setProperties(getProperties());
      bean.setExtensionMappings(getExtensionMappings());

      bean.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
      bean.setTransportId("http://cxf.apache.org/transports/http");

      bean.setBus(bus);
      bean.setStart(false);

      Server server = bean.create();
      return server;
   }

   private final class CxfJaxRsContainer extends AbstractJaxRsContainer<CXFNonSpringServlet, CxfJaxRsApplicationContainer, JaxRsProvider> {

      public CxfJaxRsContainer(Log logger, HttpService httpService, Dictionary<String, Object> props) {
         super(logger, httpService, props);
      }

      @Override
      protected CxfJaxRsApplicationContainer createApplicationContainer(String applicationContext) {
         return newApplicationContainer(applicationContext, this);
      }

      @Override
      protected CXFNonSpringServlet createBaseJaxsRsServlet(JaxRsVisitable visitable) {
         return newBaseJaxsRsServlet(visitable);
      }

      @Override
      protected void startContainer(CxfJaxRsApplicationContainer container) {
         CXFNonSpringServlet baseServlet = getBaseServlet();
         container.startContainer(baseServlet);
      }

      @Override
      protected void stopContainer(CxfJaxRsApplicationContainer container) {
         container.stopContainer();
      }

      @Override
      protected JaxRsProvider createJaxRsProvider(Bundle bundle, Object provider) {
         return new JaxRsFeatureImpl(bundle, provider);
      }

   };

   private final class CxfJaxRsApplicationContainer extends AbstractJaxRsApplicationContainer {

      private final AtomicBoolean isRegistered = new AtomicBoolean(false);
      private final JaxRsProviders providers;
      private volatile Server server;

      public CxfJaxRsApplicationContainer(String applicationContext, JaxRsProviders providers) {
         super(applicationContext);
         this.providers = providers;
      }

      public void startContainer(CXFNonSpringServlet servlet) {
         if (!isRegistered.getAndSet(true)) {
            Server newServer = newCxfServer(servlet, getApplicationContext(), getApplication(), providers);
            newServer.start();
            server = newServer;
         }
      }

      public void stopContainer() {
         if (isRegistered.getAndSet(false)) {
            if (server != null) {
               server.stop();
               server.destroy();
            }
         }
      }
   }

   private static final class JaxRsFeatureImpl implements JaxRsProvider {

      private final Bundle bundle;
      private final Object provider;

      public JaxRsFeatureImpl(Bundle bundle, Object provider) {
         super();
         this.bundle = bundle;
         this.provider = provider;
      }

      @Override
      public Bundle getBundle() {
         return bundle;
      }

      @Override
      public Object getProvider() {
         return provider;
      }

   }

}
