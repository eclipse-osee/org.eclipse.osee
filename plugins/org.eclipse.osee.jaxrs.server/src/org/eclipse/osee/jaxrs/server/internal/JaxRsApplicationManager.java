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
package org.eclipse.osee.jaxrs.server.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsApplicationManager {

   private final List<ServiceReference<Application>> pendingReg = new ArrayList<ServiceReference<Application>>();
   private final AtomicReference<JaxRsApplicationRegistry> registryRef =
      new AtomicReference<JaxRsApplicationRegistry>();
   private volatile JaxRsConfiguration config;

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      registryRef.set(registry);
   }

   public void unsetJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      registryRef.set(null);
   }

   private JaxRsApplicationRegistry getRegistry() {
      return registryRef.get();
   }

   public void start(Map<String, Object> props) throws Exception {
      update(props);
      final JaxRsApplicationRegistry registry = getRegistry();
      synchronized (pendingReg) {
         Iterator<ServiceReference<Application>> iterator = pendingReg.iterator();
         while (iterator.hasNext()) {
            ServiceReference<Application> reference = iterator.next();
            register(registry, reference);
            iterator.remove();
         }
      }
   }

   public void stop() {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         registry.deregisterAll();
      }
   }

   public void update(Map<String, Object> props) {
      config = JaxRsConfiguration.fromProperties(props).build();
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         registry.configure(config);
      }
   }

   public void addApplication(ServiceReference<Application> reference) throws Exception {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         register(registry, reference);
      } else {
         synchronized (pendingReg) {
            pendingReg.add(reference);
         }
      }
   }

   public void removeApplication(ServiceReference<Application> reference) {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         String componentName = JaxRsUtils.getComponentName(reference);
         registry.deregister(componentName);
      } else {
         synchronized (pendingReg) {
            pendingReg.remove(reference);
         }
      }
   }

   private void register(JaxRsApplicationRegistry registry, ServiceReference<Application> reference) throws Exception {
      String componentName = JaxRsUtils.getComponentName(reference);
      Bundle bundle = reference.getBundle();
      Application application = bundle.getBundleContext().getService(reference);
      String contextName = JaxRsUtils.getApplicationPath(componentName, application);
      registry.register(componentName, contextName, bundle, application);
   }

}
