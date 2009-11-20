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
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManager {

   private OseeEnumTypeManager() {
   }

   public static AbstractOseeCache<OseeEnumType> getCache() {
      return Activator.getInstance().getOseeCacheService().getEnumTypeCache();
   }

   public static OseeEnumType getType(int enumTypeId) throws OseeCoreException {
      OseeEnumType oseeEnumType = getCache().getById(enumTypeId);
      if (oseeEnumType == null) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with id:[%s] does not exist.", enumTypeId));
      }
      return oseeEnumType;
   }

   public static OseeEnumType getType(String enumTypeName) throws OseeCoreException {
      OseeEnumType itemsFound = getCache().getUniqueByName(enumTypeName);
      if (itemsFound == null) {
         throw new OseeTypeDoesNotExist(
               String.format("OSEE enum types matching [%s] name does not exist", enumTypeName));
      }
      return itemsFound;
   }

   public static Collection<String> getAllTypeNames() throws OseeCoreException {
      List<String> items = new ArrayList<String>();
      for (OseeEnumType types : getAllTypes()) {
         items.add(types.getName());
      }
      return items;
   }

   public static Collection<OseeEnumType> getAllTypes() throws OseeCoreException {
      return getCache().getAll();
   }

   public static boolean typeExist(String enumTypeName) throws OseeCoreException {
      OseeEnumType itemsFound = getCache().getUniqueByName(enumTypeName);
      return itemsFound != null;
   }

   public static int getDefaultEnumTypeId() {
      return -1;
   }

   public static void persist() throws OseeCoreException {
      getCache().storeAllModified();
   }
}