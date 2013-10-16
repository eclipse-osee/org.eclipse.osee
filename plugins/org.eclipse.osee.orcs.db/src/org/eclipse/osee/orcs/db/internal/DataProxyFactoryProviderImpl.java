/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxyFactory;
import org.eclipse.osee.orcs.db.internal.loader.DataProxyFactoryProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class DataProxyFactoryProviderImpl implements DataProxyFactoryProvider {

   private final Map<String, DataProxyFactory> proxyClassMap = new ConcurrentHashMap<String, DataProxyFactory>();
   private final List<ServiceReference<DataProxyFactory>> pending =
      new CopyOnWriteArrayList<ServiceReference<DataProxyFactory>>();

   private Log logger;
   private Thread thread;
   private boolean isReady;

   public DataProxyFactoryProviderImpl() {
      isReady = false;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void start() {
      isReady = true;
      thread = new Thread("Register Pending DataProxyFactories") {
         @Override
         public void run() {
            for (ServiceReference<DataProxyFactory> reference : pending) {
               try {
                  register(reference);
               } catch (Throwable ex) {
                  getLogger().error(ex, "Error registering pending data proxy factories");
               }
            }
            pending.clear();
         }
      };
      thread.start();
   }

   public void stop() {
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
      isReady = false;
   }

   public void addFactory(ServiceReference<DataProxyFactory> reference) throws Exception {
      if (isReady) {
         register(reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeFactory(ServiceReference<DataProxyFactory> reference) throws Exception {
      if (isReady) {
         unregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private void unregister(ServiceReference<DataProxyFactory> reference) throws OseeCoreException {
      String[] aliases = getAliases(reference);
      for (String alias : aliases) {
         proxyClassMap.remove(alias);
      }
   }

   private void register(ServiceReference<DataProxyFactory> reference) {
      try {
         Bundle bundle = reference.getBundle();
         DataProxyFactory factory = bundle.getBundleContext().getService(reference);
         Conditions.checkNotNull(factory, "DataProxyFactory");
         String[] aliases = getAliases(reference);
         for (String alias : aliases) {
            proxyClassMap.put(alias, factory);
         }
      } catch (Throwable th) {
         getLogger().error(th, "Error registering data proxy [%s]", reference.getProperty("component.name"));
      }
   }

   private String[] getAliases(ServiceReference<DataProxyFactory> reference) throws OseeCoreException {
      Object value = reference.getProperty(DataProxyFactory.PROXY_FACTORY_ALIAS);
      String[] aliases = null;
      if (value instanceof String[]) {
         aliases = (String[]) value;
      }
      Conditions.checkNotNull(aliases, "Aliases", "Error getting [%s]", DataProxyFactory.PROXY_FACTORY_ALIAS);
      return aliases;
   }

   @Override
   public DataProxyFactory getFactory(String factoryAlias) {
      return proxyClassMap.get(factoryAlias);
   }

}
