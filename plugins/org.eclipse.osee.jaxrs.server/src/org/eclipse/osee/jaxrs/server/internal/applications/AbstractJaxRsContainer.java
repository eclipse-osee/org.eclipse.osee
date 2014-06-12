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

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainer;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJaxRsContainer<H extends HttpServlet, C extends AbstractJaxRsApplicationContainer> implements JaxRsContainer, JaxRsVisitable {

   private final Log logger;
   private final HttpService httpService;
   private final Dictionary<String, Object> props;

   private final ConcurrentHashMap<String, C> applications = new ConcurrentHashMap<String, C>();
   private final Map<String, String> componentToContext = new ConcurrentHashMap<String, String>();
   private final AtomicReference<String> servletContextName = new AtomicReference<String>();
   private final AtomicBoolean isRegistered = new AtomicBoolean(false);

   private volatile H baseJaxsRsServlet;

   public AbstractJaxRsContainer(Log logger, HttpService httpService, Dictionary<String, Object> props) {
      super();
      this.logger = logger;
      this.httpService = httpService;
      this.props = props;
   }

   protected HttpService getHttpService() {
      return httpService;
   }

   protected Dictionary<String, Object> getServletProperties() {
      return props;
   }

   protected H getBaseServlet() {
      return baseJaxsRsServlet;
   }

   @Override
   public String getServletContext() {
      return servletContextName.get();
   }

   @Override
   public void setServletContext(String contextName) {
      this.servletContextName.set(contextName);
   }

   @Override
   public boolean isEmpty() {
      return applications.isEmpty();
   }

   @Override
   public void accept(JaxRsVisitor visitor) {
      visitor.onServletContext(getServletContext(), applications.size());
      for (C container : applications.values()) {
         container.accept(visitor);
      }
   }

   protected abstract C createApplicationContainer(String applicationContext);

   protected abstract H createBaseJaxsRsServlet(JaxRsVisitable visitable);

   protected abstract void startContainer(C container);

   protected abstract void stopContainer(C container);

   private C getContextContainerInitIfNull(String applicationContext) {
      C container = applications.get(applicationContext);
      if (container == null) {
         C newContainer = createApplicationContainer(applicationContext);
         container = applications.putIfAbsent(applicationContext, newContainer);
         if (container == null) {
            container = newContainer;
         }
      }
      return container;
   }

   private C getContextContainerOrNull(String applicationContext) {
      return applications.get(applicationContext);
   }

   @Override
   public synchronized void addApplication(String componentName, String applicationContext, Bundle bundle, Application application) {
      logger.trace("Add Application - [%s] - application[%s]", this, componentName);
      startServlet();
      C container = getContextContainerInitIfNull(applicationContext);
      stopContainer(container);
      container.add(componentName, bundle, application);
      componentToContext.put(componentName, applicationContext);
      try {
         startContainer(container);
      } catch (Exception ex) {
         logger.error(ex, "Error activating application [%s] on [%s]", this, componentName);
      }
   }

   @Override
   public synchronized void removeApplication(String componentName) {
      logger.trace("Remove Application - [%s] - application[%s]", this, componentName);
      String contextName = componentToContext.remove(componentName);
      C container = getContextContainerOrNull(contextName);
      if (container != null) {
         stopContainer(container);
         container.remove(componentName);
         if (container.isEmpty()) {
            applications.remove(contextName);
         } else {
            startContainer(container);
         }
      }
   }

   @Override
   public synchronized void start() {
      startServlet();
      for (C container : applications.values()) {
         startContainer(container);
      }
   }

   @Override
   public synchronized void stop() {
      for (C container : applications.values()) {
         stopContainer(container);
      }
      stopServlet();
   }

   private void startServlet() {
      if (!isRegistered.getAndSet(true)) {
         baseJaxsRsServlet = createBaseJaxsRsServlet(this);
         logger.trace("Register Servlet - [%s] - [%s]", this, baseJaxsRsServlet);
         try {
            String contextName = getServletContext();
            getHttpService().registerServlet(contextName, baseJaxsRsServlet, props, null);
         } catch (Exception ex) {
            throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR, "Error registering servlet [%s] ",
               servletContextName);
         }
      }
   }

   private void stopServlet() {
      if (isRegistered.getAndSet(false)) {
         String contextName = getServletContext();
         logger.trace("De-register Servlet - [%s] - [%s]", this, baseJaxsRsServlet);
         getHttpService().unregister(contextName);
         baseJaxsRsServlet = null;
      }
   }

   @Override
   public String toString() {
      return "JaxRsContainerImpl [id=" + Integer.toHexString(hashCode()) + ", contextName=" + servletContextName + "]";
   }
}