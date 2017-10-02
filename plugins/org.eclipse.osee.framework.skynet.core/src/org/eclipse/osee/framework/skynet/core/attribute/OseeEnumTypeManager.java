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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManager {

   public static AbstractOseeCache<OseeEnumType> getCache() {
      return ServiceUtil.getOseeCacheService().getEnumTypeCache();
   }

   public static OseeEnumType getType(long enumTypeId) {
      OseeEnumType oseeEnumType = getCache().getById(enumTypeId);
      if (oseeEnumType == null) {
         throw new OseeTypeDoesNotExist("Osee Enum Type with id:[%s] does not exist.", enumTypeId);
      }
      return oseeEnumType;
   }

   public static Collection<String> getAllTypeNames() {
      List<String> items = new ArrayList<>();
      for (OseeEnumType types : getAllTypes()) {
         items.add(types.getName());
      }
      return items;
   }

   public static Collection<OseeEnumType> getAllTypes() {
      return getCache().getAll();
   }

   public static int getDefaultEnumTypeId() {
      return -1;
   }
}