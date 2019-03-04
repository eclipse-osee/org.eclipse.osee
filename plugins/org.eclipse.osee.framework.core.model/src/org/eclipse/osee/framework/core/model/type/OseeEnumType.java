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
package org.eclipse.osee.framework.core.model.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.internal.fields.EnumEntryField;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Test: @link OseeEnumTypeTest
 *
 * @author Roberto E. Escobar
 */
public class OseeEnumType extends AbstractOseeType {

   public static final String OSEE_ENUM_TYPE_ENTRIES_FIELD = "osee.enum.type.entries.field";

   private final Collection<OseeEnumEntry> enumEntries = new HashSet<>();

   public OseeEnumType(Long guid, String enumTypeName) {
      super(guid, enumTypeName);
      addField(OSEE_ENUM_TYPE_ENTRIES_FIELD, new EnumEntryField(enumEntries));
   }

   @Override
   public String toString() {
      List<String> data = new ArrayList<>();
      try {
         for (OseeEnumEntry entry : values()) {
            data.add(entry.toString());
         }
      } catch (OseeCoreException ex) {
         data.add("Error");
      }
      return String.format("[%s] - %s", getName(), data);
   }

   public OseeEnumEntry[] values() {
      Collection<OseeEnumEntry> values = getFieldValue(OSEE_ENUM_TYPE_ENTRIES_FIELD);
      List<OseeEnumEntry> entries = new ArrayList<>(values);
      Collections.sort(entries);
      return entries.toArray(new OseeEnumEntry[entries.size()]);
   }

   public OseeEnumEntry getEntryByName(String entryName) {
      Collection<OseeEnumEntry> values = getFieldValue(OSEE_ENUM_TYPE_ENTRIES_FIELD);
      for (OseeEnumEntry entry : values) {
         if (entry.getName().equals(entryName)) {
            return entry;
         }
      }
      return null;
   }

   public Set<String> valuesAsOrderedStringSet() {
      Set<String> values = new LinkedHashSet<>();
      for (OseeEnumEntry oseeEnumEntry : values()) {
         values.add(oseeEnumEntry.getName());
      }
      return values;
   }

   public OseeEnumEntry valueOf(int ordinal) {
      OseeEnumEntry toReturn = null;
      for (OseeEnumEntry oseeEnumEntry : values()) {
         if (oseeEnumEntry.ordinal() == ordinal) {
            toReturn = oseeEnumEntry;
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException("No enum const [%s] - ordinal [%s]", getName(), ordinal);
      }
      return toReturn;
   }

   /**
    * return the enum with the given name. Tolerates leading and trailing whitespace using trim()
    */
   public OseeEnumEntry valueOf(String entryName) {
      OseeEnumEntry toReturn = null;
      for (OseeEnumEntry oseeEnumEntry : values()) {
         if (oseeEnumEntry.getName().equals(entryName)) {
            toReturn = oseeEnumEntry;
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException("No enum const [%s].[%s]", getName(), entryName);
      }
      return toReturn;
   }

   public void setEntries(List<OseeEnumEntry> entries) {
      setField(OSEE_ENUM_TYPE_ENTRIES_FIELD, entries);
   }

   public void addEntry(OseeEnumEntry entry) {
      List<OseeEnumEntry> entries = new ArrayList<>();
      entries.addAll(Arrays.asList(values()));
      entries.add(entry);
      setEntries(entries);
   }

   public void removeEntry(OseeEnumEntry entry) {
      List<OseeEnumEntry> entries = new ArrayList<>();
      entries.addAll(Arrays.asList(values()));
      if (!entries.remove(entry)) {
         throw new OseeArgumentException("OseeEnumEntry[%s] does not exist on OseeEnumType[%s]", entry, this);
      }
      setEntries(entries);
   }
}