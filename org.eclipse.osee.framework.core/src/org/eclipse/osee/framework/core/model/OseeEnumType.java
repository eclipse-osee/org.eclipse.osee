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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.EnumEntryField;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType extends AbstractOseeType implements Comparable<OseeEnumType> {

   public static final String OSEE_ENUM_TYPE_ENTRIES_FIELD = "osee.enum.type.entries.field";

   private final Collection<OseeEnumEntry> enumEntries = new HashSet<OseeEnumEntry>();

   public OseeEnumType(String guid, String enumTypeName) {
      super(guid, enumTypeName);
      addField(OSEE_ENUM_TYPE_ENTRIES_FIELD, new EnumEntryField(enumEntries));
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
      Collection<OseeEnumEntry> values = getFieldValue(OSEE_ENUM_TYPE_ENTRIES_FIELD);
      List<OseeEnumEntry> entries = new ArrayList<OseeEnumEntry>(values);
      Collections.sort(entries);
      return entries.toArray(new OseeEnumEntry[entries.size()]);
   }

   public OseeEnumEntry getEntryByGuid(String entryGuid) throws OseeCoreException {
      Collection<OseeEnumEntry> values = getFieldValue(OSEE_ENUM_TYPE_ENTRIES_FIELD);
      for (OseeEnumEntry entry : values) {
         if (entry.getGuid().equals(entryGuid)) {
            return entry;
         }
      }
      return null;
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