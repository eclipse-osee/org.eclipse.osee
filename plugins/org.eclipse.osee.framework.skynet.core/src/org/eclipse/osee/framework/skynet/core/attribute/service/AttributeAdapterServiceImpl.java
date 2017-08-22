/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.attribute.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class AttributeAdapterServiceImpl implements AttributeAdapterService {

   private final Map<AttributeTypeId, AttributeAdapter<?>> adapterByType;

   private final Map<String, AttributeAdapter<?>> registered;
   private final List<ServiceReference<AttributeAdapter<?>>> pending;
   private volatile boolean ready = false;
   private Thread thread;
   private final ArtifactAttributeAdapter defaultAdapter = new ArtifactAttributeAdapter();

   public AttributeAdapterServiceImpl() {
      registered = new ConcurrentHashMap<>();
      pending = new CopyOnWriteArrayList<>();
      adapterByType = new ConcurrentHashMap<>();
   }

   public void start() {
      ready = true;
      thread = new Thread("Register Pending Attribute Adapter Services") {
         @Override
         public void run() {
            for (ServiceReference<AttributeAdapter<?>> reference : pending) {
               try {
                  register(reference);
               } catch (OseeCoreException ex) {
                  OseeLog.log(AttributeAdapterServiceImpl.class, Level.SEVERE, ex);
               }
            }
            pending.clear();
         }
      };
      thread.start();
   }

   public void stop() {
      ready = false;
      if (thread != null && thread.isAlive()) {
         thread.interrupt();
      }
   }

   private boolean isReady() {
      return ready;
   }

   public void addAdapter(ServiceReference<AttributeAdapter<?>> reference) throws OseeCoreException {
      if (isReady()) {
         register(reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeAdapter(ServiceReference<AttributeAdapter<?>> reference) {
      if (isReady()) {
         unregister(reference);
      } else {
         pending.remove(reference);
      }
   }

   private String generateKey(ServiceReference<AttributeAdapter<?>> reference) {
      return (String) reference.getProperty("component.name");
   }

   private void unregister(ServiceReference<AttributeAdapter<?>> reference) {
      String key = generateKey(reference);
      AttributeAdapter<?> adapter = registered.remove(key);
      for (AttributeTypeId type : adapter.getSupportedTypes()) {
         adapterByType.remove(type);
      }
   }

   private void register(ServiceReference<AttributeAdapter<?>> reference) throws OseeCoreException {
      Bundle bundle = reference.getBundle();
      AttributeAdapter<?> adapter = bundle.getBundleContext().getService(reference);
      Conditions.checkNotNull(adapter, "AttributeAdapter");

      String key = generateKey(reference);
      registered.put(key, adapter);

      for (AttributeTypeId type : adapter.getSupportedTypes()) {
         if (adapterByType.containsKey(type)) {
            String storedAdatperName = String.valueOf(adapterByType.get(type));
            throw new OseeArgumentException("Attribute type [%s] already in registry with adapter [%s]", type,
               storedAdatperName);
         }

         adapterByType.put(type, adapter);
      }
   }

   @Override
   public <T> T adapt(Attribute<?> attribute, Id identity) throws OseeCoreException {
      AttributeTypeId type = attribute.getAttributeType();
      AttributeAdapter<T> adapter = getAdapter(type);
      Conditions.checkNotNull(adapter, "adapter");
      return adapter.adapt(attribute, identity);
   }

   @SuppressWarnings("unchecked")
   public <T> AttributeAdapter<T> getAdapter(AttributeTypeId type) {
      AttributeAdapter<T> adapter = (AttributeAdapter<T>) adapterByType.get(type);
      if (adapter == null) {
         adapter = (AttributeAdapter<T>) defaultAdapter;
      }
      return adapter;
   }

}