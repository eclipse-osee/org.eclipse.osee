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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;

/**
 * @author Roberto E. Escobar
 */
public final class OseeEnumTypeCache extends AbstractOseeCache<OseeEnumType> {

   private final HashCollection<OseeEnumType, OseeEnumEntry> enumTypeToEntryMap =
         new HashCollection<OseeEnumType, OseeEnumEntry>();
   private final Map<OseeEnumEntry, OseeEnumType> enumEntryToEnumType = new HashMap<OseeEnumEntry, OseeEnumType>();

   public OseeEnumTypeCache(IOseeTypeFactory factory, IOseeDataAccessor<OseeEnumType> dataAccessor) {
      super(factory, dataAccessor);
   }

   @Override
   public void decache(OseeEnumType type) throws OseeCoreException {
      super.decache(type);
      //      for (OseeEnumEntry entry : type.values()) {
      //         enumEntryToEnumType.remove(entry);
      //      }
      //      enumTypeToEntryMap.removeValues(type);
   }

   public void cacheEnumEntries(OseeEnumType oseeEnumType, Collection<OseeEnumEntry> oseeEnumEntries) throws OseeCoreException {
      checkEnumEntryIntegrity(oseeEnumEntries);
      List<OseeEnumEntry> newEntries = new ArrayList<OseeEnumEntry>();

      List<OseeEnumEntry> currentEntries = getEnumEntries(oseeEnumType);
      Set<OseeEnumEntry> toRemove = new HashSet<OseeEnumEntry>(currentEntries);
      for (OseeEnumEntry entry : oseeEnumEntries) {
         boolean wasFound = false;

         String nameToCheck = entry.getName();
         int ordinalToCheck = entry.ordinal();
         String guidToCheck = entry.getGuid();

         for (OseeEnumEntry existingEntry : currentEntries) {
            if (existingEntry.getGuid().equals(guidToCheck)) {
               wasFound = true;
               existingEntry.setName(nameToCheck);
               existingEntry.setOrdinal(ordinalToCheck);
            } else if (existingEntry.getName().equals(nameToCheck)) {
               wasFound = true;
               //               existingEntry.setOrdinal(ordinalToCheck);
            }
            //            else if (existingEntry.ordinal() == ordinalToCheck) {
            //               wasFound = true;
            //               existingEntry.setName(nameToCheck);
            //            }

            if (wasFound) {
               toRemove.remove(existingEntry);
               break;
            }
         }
         if (!wasFound) {
            newEntries.add(entry);
         }
      }
      if (!toRemove.isEmpty()) {
         Collection<OseeEnumEntry> entriesRemoved = enumTypeToEntryMap.getValues(oseeEnumType);
         if (entriesRemoved != null) {
            for (OseeEnumEntry entry : toRemove) {
               entriesRemoved.remove(entry);
            }
         }
      }
      for (OseeEnumEntry entry : newEntries) {
         enumTypeToEntryMap.put(oseeEnumType, entry);
         enumEntryToEnumType.put(entry, oseeEnumType);
      }
   }

   private void checkEnumEntryIntegrity(Collection<OseeEnumEntry> oseeEnumEntries) throws OseeArgumentException {
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
   }

   public OseeEnumType getEnumType(OseeEnumEntry oseeEnumEntry) throws OseeCoreException {
      ensurePopulated();
      return enumEntryToEnumType.get(oseeEnumEntry);
   }

   public List<OseeEnumEntry> getEnumEntries(OseeEnumType oseeEnumType) throws OseeCoreException {
      ensurePopulated();
      List<OseeEnumEntry> itemsToReturn = new ArrayList<OseeEnumEntry>();
      Collection<OseeEnumEntry> entries = enumTypeToEntryMap.getValues(oseeEnumType);
      if (entries != null) {
         itemsToReturn.addAll(entries);
      }
      return itemsToReturn;
   }

   public OseeEnumType createType(String guid, String enumTypeName) throws OseeCoreException {
      OseeEnumType oseeEnumType = getByGuid(guid);
      if (oseeEnumType == null) {
         oseeEnumType = getDataFactory().createEnumType(this, guid, enumTypeName);
      } else {
         decache(oseeEnumType);
         oseeEnumType.setName(enumTypeName);
      }
      cache(oseeEnumType);
      return oseeEnumType;
   }

   public OseeEnumEntry createEntry(String guid, String name, int ordinal) throws OseeCoreException {
      return getDataFactory().createEnumEntry(this, guid, name, ordinal);
   }
}
