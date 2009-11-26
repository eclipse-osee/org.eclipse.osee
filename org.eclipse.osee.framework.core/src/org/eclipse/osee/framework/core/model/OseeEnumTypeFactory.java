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
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeFactory implements IOseeTypeFactory {

   public OseeEnumTypeFactory() {
   }

   public OseeEnumType createEnumType(String guid, String name) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "osee enum type name");
      String checkedGuid = Conditions.checkGuidCreateIfNeeded(guid);
      return new OseeEnumType(checkedGuid, name);
   }

   public OseeEnumEntry createEnumEntry(String guid, String name, int ordinal) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "osee enum entry name");
      Conditions.checkExpressionFailOnTrue(ordinal < 0, "ordinal must be greater than or equal to zero");
      String checkedGuid = Conditions.checkGuidCreateIfNeeded(guid);
      return new OseeEnumEntry(checkedGuid, name, ordinal);
   }

   //   public OseeEnumType createOrUpdate(AbstractOseeCache<OseeEnumType> cache, String guid, String enumTypeName) throws OseeCoreException {
   //      OseeEnumType oseeEnumType = cache.getByGuid(guid);
   //      if (oseeEnumType == null) {
   //         oseeEnumType = createEnumType(guid, enumTypeName);
   //      } else {
   //         cache.decache(oseeEnumType);
   //         oseeEnumType.setName(enumTypeName);
   //      }
   //      cache.cache(oseeEnumType);
   //      return oseeEnumType;
   //   }

   public OseeEnumType createOrUpdate(IOseeCache<OseeEnumType> cache, int uniqueId, ModificationType modificationType, String guid, String enumTypeName) throws OseeCoreException {
      OseeEnumType oseeEnumType = cache.getById(uniqueId);
      if (oseeEnumType == null) {
         oseeEnumType = createEnumType(guid, enumTypeName);
         oseeEnumType.setId(uniqueId);
         oseeEnumType.setModificationType(modificationType);
      } else {
         cache.decache(oseeEnumType);
         oseeEnumType.setName(enumTypeName);
      }
      cache.cache(oseeEnumType);
      return oseeEnumType;
   }
}
