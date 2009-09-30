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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.field.EnumEntryField;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType extends AbstractOseeType implements Comparable<OseeEnumType> {

   public static final String OSEE_ENUM_TYPE_ENTRIES_FIELD = "osee.enum.type.entries.field";

   public OseeEnumType(AbstractOseeCache<OseeEnumType> cache, String guid, String enumTypeName) {
      super(cache, guid, enumTypeName);
   }

   @Override
   protected void initializeFields() {
      addField(OSEE_ENUM_TYPE_ENTRIES_FIELD, new EnumEntryField(getCache(), this));
   }

   @Override
   protected OseeEnumTypeCache getCache() {
      return (OseeEnumTypeCache) super.getCache();
   }

   @Override
   public String toString() {
      List<String> data = new ArrayList<String>();
      try {
         for (OseeEnumEntry entry : values()) {
            data.add(entry.toString());
         }
      } catch (OseeCoreException ex) {
         data.add("Error");
      }
      return String.format("[%s] - %s", getName(), data);
   }

   public OseeEnumEntry[] values() throws OseeCoreException {
      List<OseeEnumEntry> entries = getFieldValue(OSEE_ENUM_TYPE_ENTRIES_FIELD);
      Collections.sort(entries);
      return entries.toArray(new OseeEnumEntry[entries.size()]);
   }

   public Set<String> valuesAsOrderedStringSet() throws OseeCoreException {
      Set<String> values = new LinkedHashSet<String>();
      for (OseeEnumEntry oseeEnumEntry : values()) {
         values.add(oseeEnumEntry.getName());
      }
      return values;
   }

   public OseeEnumEntry valueOf(int ordinal) throws OseeCoreException {
      OseeEnumEntry toReturn = null;
      for (OseeEnumEntry oseeEnumEntry : values()) {
         if (oseeEnumEntry.ordinal() == ordinal) {
            toReturn = oseeEnumEntry;
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException(String.format("No enum const [%s] - ordinal [%s]", getName(), ordinal));
      }
      return toReturn;
   }

   public OseeEnumEntry valueOf(String entryName) throws OseeCoreException {
      OseeEnumEntry toReturn = null;
      for (OseeEnumEntry oseeEnumEntry : values()) {
         if (oseeEnumEntry.getName().equals(entryName)) {
            toReturn = oseeEnumEntry;
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException(String.format("No enum const [%s].[%s]", getName(), entryName));
      }
      return toReturn;
   }

   public void setEntries(List<OseeEnumEntry> entries) throws OseeCoreException {
      setField(OSEE_ENUM_TYPE_ENTRIES_FIELD, entries);
   }

   public void addEntry(OseeEnumEntry entry) throws OseeCoreException {
      List<OseeEnumEntry> entries = new ArrayList<OseeEnumEntry>();
      entries.addAll(Arrays.asList(values()));
      entries.add(entry);
      setEntries(entries);
   }

   public void removeEntry(OseeEnumEntry entry) throws OseeCoreException {
      List<OseeEnumEntry> entries = new ArrayList<OseeEnumEntry>();
      entries.addAll(Arrays.asList(values()));
      if (!entries.remove(entry)) {
         throw new OseeArgumentException(String.format("OseeEnumEntry[%s] does not exist on OseeEnumType[%s]", entry,
               this));
      }
      setEntries(entries);
   }

   @Override
   public int compareTo(OseeEnumType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }
}