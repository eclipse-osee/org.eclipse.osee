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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ClientAttributeTypeAccessor extends AbstractClientDataAccessor<AttributeType> {

   private final AbstractOseeCache<OseeEnumType> enumCache;

   public ClientAttributeTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<OseeEnumType> enumCache) {
      super(factoryProvider);
      this.enumCache = enumCache;
   }

   protected AttributeTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getAttributeTypeFactory();
   }

   @Override
   public void load(IOseeCache<AttributeType> cache) throws OseeCoreException {
      enumCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<AttributeType> updateCache(IOseeCache<AttributeType> cache) throws OseeCoreException {
      //      AttributeTypeFactory factory = getFactory();
      //      for (AttributeType srcType : items) {
      //         OseeEnumType oseeEnumType = enumCache.getById(srcType.getOseeEnumTypeId());
      //         factory.createOrUpdate(cache, srcType.getId(), srcType.getModificationType(), srcType.getGuid(),
      //               srcType.getName(), srcType.getBaseAttributeTypeId(), srcType.getAttributeProviderId(),
      //               srcType.getFileTypeExtension(), srcType.getDefaultValue(), oseeEnumType, srcType.getMinOccurrences(),
      //               srcType.getMaxOccurrences(), srcType.getDescription(), srcType.getTaggerId());
      //      }
      return null;
   }
}
