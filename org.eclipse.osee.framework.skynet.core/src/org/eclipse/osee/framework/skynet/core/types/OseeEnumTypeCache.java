/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;

/**
 * @author Roberto E. Escobar
 */
public final class OseeEnumTypeCache extends AbstractOseeTypeCache<OseeEnumType> {

   private final HashCollection<OseeEnumType, OseeEnumEntry> enumTypeToEntryMap =
         new HashCollection<OseeEnumType, OseeEnumEntry>();
   private final Map<OseeEnumEntry, OseeEnumType> enumEntryToEnumType = new HashMap<OseeEnumEntry, OseeEnumType>();

   public OseeEnumTypeCache(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor<OseeEnumType> dataAccessor) {
      super(cache, factory, dataAccessor);
   }

   public void cacheEnumEntries(OseeEnumType oseeEnumType, Collection<OseeEnumEntry> oseeEnumEntries) throws OseeCoreException {
      for (OseeEnumEntry entry : oseeEnumEntries) {
         if (entry.getName() == null) {
            throw new OseeArgumentException("EnumEntry name violation - null is not allowed");
         }
         if (entry.ordinal() < 0) {
            throw new OseeArgumentException("EnumEntry ordinal must be greater than zero");
         }
         for (OseeEnumEntry existingEntry : oseeEnumEntries) {
            if (!entry.equals(existingEntry)) {
               if (entry.getName().equals(existingEntry.getName())) {
                  throw new OseeArgumentException(String.format("Unique enumEntry name violation - %s already exists.",
                        entry));
               }
               if (entry.ordinal() == existingEntry.ordinal()) {
                  throw new OseeArgumentException(
                        String.format(
                              "Unique enumEntry ordinal violation - ordinal [%d] is used by existing entry:[%s] and new entry:[%s]",
                              entry.ordinal(), existingEntry, entry));
               }
            }
         }
      }
      List<OseeEnumEntry> existingEntries = getEnumEntries(oseeEnumType);
      for (OseeEnumEntry entries : Collections.setComplement(existingEntries, oseeEnumEntries)) {
         entries.setModificationType(ModificationType.DELETED);
      }
      for (OseeEnumEntry entry : oseeEnumEntries) {
         enumTypeToEntryMap.put(oseeEnumType, entry);
         enumEntryToEnumType.put(entry, oseeEnumType);
      }
   }

   public OseeEnumType getEnumType(OseeEnumEntry oseeEnumEntry) throws OseeCoreException {
      //      getCache().ensurePopulated();
      return enumEntryToEnumType.get(oseeEnumEntry);
   }

   public List<OseeEnumEntry> getEnumEntries(OseeEnumType oseeEnumType) throws OseeCoreException {
      getCache().ensurePopulated();
      List<OseeEnumEntry> itemsToReturn = new ArrayList<OseeEnumEntry>();
      Collection<OseeEnumEntry> entries = enumTypeToEntryMap.getValues(oseeEnumType);
      if (entries != null) {
         itemsToReturn.addAll(entries);
      }
      return itemsToReturn;
   }

   public OseeEnumType createType(String guid, String enumTypeName) throws OseeCoreException {
      OseeEnumType oseeEnumType = getTypeByGuid(guid);
      if (oseeEnumType == null) {
         oseeEnumType = getDataFactory().createEnumType(this, guid, enumTypeName);
      } else {
         decacheType(oseeEnumType);
         oseeEnumType.setName(enumTypeName);
      }
      cacheType(oseeEnumType);
      return oseeEnumType;
   }

   public OseeEnumEntry createEntry(String guid, String name, int ordinal) throws OseeCoreException {
      return getDataFactory().createEnumEntry(this, guid, name, ordinal);
   }
}
