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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;

public class AttributeAdapterServiceImpl implements AttributeAdapterService {

   private final Map<IAttributeType, AttributeAdapter<?>> registry;

   public AttributeAdapterServiceImpl() {
      registry = new ConcurrentHashMap<IAttributeType, AttributeAdapter<?>>();
   }

   public void addAdapter(AttributeAdapter<?> adapter) throws OseeArgumentException {
      for (IAttributeType type : adapter.getSupportedTypes()) {
         if (registry.containsKey(type)) {
            String storedAdatperName = String.valueOf(registry.get(type));
            throw new OseeArgumentException("Attribute type [%s] already in registry with adapter [%s]", type,
               storedAdatperName);
         }

         registry.put(type, adapter);
      }
   }

   public void removeAdapter(AttributeAdapter<?> adapter) {
      for (IAttributeType type : adapter.getSupportedTypes()) {
         registry.remove(type);
      }
   }

   @Override
   public <T> T adapt(Attribute<?> attribute, Identity<String> identity) throws OseeCoreException {
      IAttributeType type = attribute.getAttributeType();

      AttributeAdapter<T> adapter = getAdapter(type);
      Conditions.checkNotNull(adapter, "adapter");
      return adapter.adapt(attribute, identity);
   }

   @SuppressWarnings("unchecked")
   public <T> AttributeAdapter<T> getAdapter(IAttributeType type) {
      return (AttributeAdapter<T>) registry.get(type);
   }
}