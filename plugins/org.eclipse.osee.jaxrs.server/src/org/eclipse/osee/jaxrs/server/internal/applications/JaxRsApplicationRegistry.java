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
package org.eclipse.osee.jaxrs.server.internal.applications;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.JaxRsConfiguration;
import org.eclipse.osee.jaxrs.server.internal.JaxRsConstants;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsApplicationRegistry implements JaxRsVisitable {

   public static interface JaxRsContainerProvider {

      JaxRsContainer get();

      JaxRsContainer unSet();

      boolean hasContainer();
   }

   public static interface JaxRsContainer {

      String getServletContext();

      void setServletContext(String contextName);

      void addApplication(String componentName, String applicationContext, Bundle bundle, Application application);

      void removeApplication(String componentName);

      boolean isEmpty();

      void start();

      void stop();

      void accept(JaxRsVisitor visitor);

      void addProvider(String componentName, Bundle bundle, Object provider);

      void removeProvider(String componentName);

   }

   private final ConcurrentHashMap<String, JaxRsContainerProvider> servlets =
      new ConcurrentHashMap<>();

   private Log logger;
   private JaxRsFactory factory;

   private String baseContext = JaxRsConstants.DEFAULT_JAXRS_BASE_CONTEXT;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJaxRsFactory(JaxRsFactory factory) {
      this.factory = factory;
   }

   public void start() {
      logger.trace("Starting [%s]...", getClass().getSimpleName());
   }

   public void stop() {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
      Iterator<JaxRsContainerProvider> iterator = servlets.values().iterator();
      while (iterator.hasNext()) {
         JaxRsContainer container = iterator.next().get();
         if (container != null) {
            container.stop();
         }
         iterator.remove();
      }
   }

   public String getBaseContext() {
      return baseContext;
   }

   public synchronized void configure(JaxRsConfiguration config) {
      logger.trace("Configuring [%s]...", getClass().getSimpleName());
      final String oldBaseContext = baseContext;
      final String newBaseContext = config.getBaseContext();
      if (!oldBaseContext.equals(newBaseContext)) {
         logger.trace("Configuration Changed for [%s] - [%s]", getClass().getSimpleName(), config);
         baseContext = newBaseContext;
         JaxRsContainerProvider removed = servlets.remove(oldBaseContext);
         if (removed != null) {
            if (removed.hasContainer()) {
               JaxRsContainer container = removed.get();
               container.stop();
               container.accept(new JaxRsVisitor() {
                  @Override
                  public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
                     final String baseContext = getBaseContext();
                     JaxRsContainer newContainer = getContainerInitIfNull(baseContext);
                     newContainer.addApplication(componentName, applicationContext, bundle, application);
                  }

                  @Override
                  public void onProvider(String componentName, Bundle bundle, Object provider) {
                     final String baseContext = getBaseContext();
                     JaxRsContainer newContainer = getContainerInitIfNull(baseContext);
                     newContainer.addProvider(componentName, bundle, provider);
                  }
               });
            }
         }
      }
   }

   public void register(String componentName, Bundle bundle, Application application) {
      String contextName = JaxRsUtils.getApplicationPath(componentName, application);

      final String baseContext = getBaseContext();
      JaxRsContainer container = getContainerInitIfNull(baseContext);
      container.addApplication(componentName, contextName, bundle, application);
   }

   public void deregister(String componentName) {
      final String baseContext = getBaseContext();
      JaxRsContainer container = getContainerOrNull(baseContext);
      if (container != null) {
         container.removeApplication(componentName);
         if (container.isEmpty()) {
            servlets.remove(baseContext);
         }
      }
   }

   public void registerProvider(String componentName, Bundle bundle, Object provider) {
      final String baseContext = getBaseContext();
      JaxRsContainer container = getContainerInitIfNull(baseContext);
      container.addProvider(componentName, bundle, provider);
   }

   public void deregisterProvider(String componentName) {
      final String baseContext = getBaseContext();
      JaxRsContainer container = getContainerOrNull(baseContext);
      if (container != null) {
         container.removeProvider(componentName);
         if (container.isEmpty()) {
            servlets.remove(baseContext);
         }
      }
   }

   @Override
   public void accept(JaxRsVisitor visitor) {
      visitor.onStartRegistry();
      try {
         for (JaxRsContainerProvider provider : servlets.values()) {
            if (provider.hasContainer()) {
               JaxRsContainer container = provider.get();
               container.accept(visitor);
            }
         }
      } finally {
         visitor.onEndRegistry();
      }
   }

   private JaxRsContainer getContainerOrNull(String contextName) {
      JaxRsContainer toReturn = null;
      JaxRsContainerProvider reference = servlets.get(contextName);
      if (reference != null) {
         toReturn = reference.get();
      }
      return toReturn;
   }

   private JaxRsContainer getContainerInitIfNull(String contextName) {
      JaxRsContainerProvider reference = servlets.get(contextName);
      if (reference == null) {
         JaxRsContainerProvider newContainer = factory.newJaxRsContainerProvider(contextName);
         reference = servlets.putIfAbsent(contextName, newContainer);
         if (reference == null) {
            servlets.put(contextName, newContainer);
            reference = newContainer;
         }
      }
      return reference.get();
   }

}
