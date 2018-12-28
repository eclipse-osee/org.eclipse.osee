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
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public abstract class JaxRsDynamicServiceManager<T> {

   private final List<ServiceReference<T>> pendingReg = new ArrayList<>();
   private final AtomicReference<JaxRsApplicationRegistry> registryRef =
      new AtomicReference<>();

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      registryRef.set(registry);
   }

   public void unsetJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      registryRef.set(null);
   }

   protected JaxRsApplicationRegistry getRegistry() {
      return registryRef.get();
   }

   public void start(Map<String, Object> props) {
      update(props);
      JaxRsApplicationRegistry registry = getRegistry();
      synchronized (pendingReg) {
         Iterator<ServiceReference<T>> iterator = pendingReg.iterator();
         while (iterator.hasNext()) {
            ServiceReference<T> reference = iterator.next();
            register(registry, reference);
            iterator.remove();
         }
      }
   }

   public void stop(Map<String, Object> props) {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         synchronized (pendingReg) {
            Iterator<ServiceReference<T>> iterator = pendingReg.iterator();
            while (iterator.hasNext()) {
               ServiceReference<T> reference = iterator.next();
               deregister(registry, reference);
               iterator.remove();
            }
         }
      }
   }

   public void update(Map<String, Object> props) {
      //
   }

   public void addService(ServiceReference<T> reference) {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         register(registry, reference);
      } else {
         synchronized (pendingReg) {
            pendingReg.add(reference);
         }
      }
   }

   public void removeService(ServiceReference<T> reference) {
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         deregister(registry, reference);
      } else {
         synchronized (pendingReg) {
            pendingReg.remove(reference);
         }
      }
   }

   public abstract void register(JaxRsApplicationRegistry registry, ServiceReference<T> reference);

   public abstract void deregister(JaxRsApplicationRegistry registry, ServiceReference<T> reference);

}