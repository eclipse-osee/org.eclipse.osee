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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType extends BaseOseeType implements Comparable<OseeEnumType> {

   private final List<OseeEnumEntry> enumSet;
   private boolean isDeleted;
   private final OseeTypeCache cache;

   public OseeEnumType(String guid, String enumTypeName, OseeTypeCache cache) {
      super(guid, enumTypeName);
      this.enumSet = new ArrayList<OseeEnumEntry>();
      this.isDeleted = false;
      this.cache = cache;
   }

   @Override
   public int compareTo(OseeEnumType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   protected void internalSetDeleted(boolean deleted) {
      this.isDeleted = deleted;
   }

   public void removeEntry(OseeEnumEntry entry) {
      // TODO implement this
      //      checkNull(entries);
      //      if (entries.length > 0) {
      //         final List<OseeEnumEntry> itemsToRemove = Arrays.asList(entries);
      //         final List<Pair<String, Integer>> newEntries = new ArrayList<Pair<String, Integer>>();
      //         for (OseeEnumEntry entry : enumType.values()) {
      //            if (!itemsToRemove.contains(entry)) {
      //               newEntries.add(entry.asPair());
      //            }
      //         }
      //         UpdateEnumTx updateEnumTx = new UpdateEnumTx(enumType, newEntries);
      //         updateEnumTx.execute();
      //
      //         enumType.internalRemoveEnums(entries);
      //         // TODO Signal to other clients - Event here 
      //      }
   }

   public void addEntries(List<Pair<String, Integer>> entries) throws OseeCoreException {
      final List<Pair<String, Integer>> newEntries = getCombinedEntries(this, entries);
      cache.getEnumTypeData().cacheType(this);
      // TODO Fix this - may need to add a persist call to this class?
      //      UpdateEnumTx updateEnumTx = new UpdateEnumTx(this, newEntries);
      //      updateEnumTx.execute();
      //
      //      boolean wasCreated = false;
      //      if (getTypeById(oseeEnumType.getTypeId()) == null) {
      //         cacheType(oseeEnumType);
      //         wasCreated = true;
      //      }
      //      for (Pair<String, Integer> entry : entries) {
      //         oseeEnumType.internalAddEnum(entry);
      //      }
      //      // TODO Signal to other clients - Event here
      //      if (wasCreated) {
      //         // TODO Signal newly created - Event here
      //      }
   }

   private List<Pair<String, Integer>> getCombinedEntries(final OseeEnumType enumType, final List<Pair<String, Integer>> entries) {
      final List<Pair<String, Integer>> combinedList = new ArrayList<Pair<String, Integer>>();
      if (entries != null) {
         combinedList.addAll(entries);
      }
      for (OseeEnumEntry entry : enumType.values()) {
         combinedList.add(entry.asPair());
      }
      return combinedList;
   }

   public void addEntries(Pair<String, Integer>... entries) throws OseeCoreException {
      addEntries(Arrays.asList(entries));
   }

   protected synchronized void internalAddEnum(String guid, String name, int ordinal) throws OseeArgumentException {
      checkEnumEntryName(name);
      checkOrdinal(ordinal);
      OseeEnumEntry entry = new OseeEnumEntry(guid, name, ordinal);
      enumSet.add(entry);
   }

   protected void internalAddEnum(String guid, Pair<String, Integer> entry) throws OseeArgumentException {
      internalAddEnum(guid, entry.getFirst(), entry.getSecond());
   }

   protected synchronized void internalRemoveEnums(OseeEnumEntry... entries) {
      if (entries != null) {
         for (OseeEnumEntry entry : entries) {
            enumSet.remove(entry);
         }
      }
   }

   public boolean isDeleted() {
      return isDeleted;
   }

   public synchronized OseeEnumEntry[] values() {
      Collections.sort(enumSet);
      return enumSet.toArray(new OseeEnumEntry[enumSet.size()]);
   }

   public synchronized Set<String> valuesAsOrderedStringSet() {
      Set<String> values = new LinkedHashSet<String>();
      for (OseeEnumEntry oseeEnumEntry : enumSet) {
         values.add(oseeEnumEntry.name());
      }
      return values;
   }

   private synchronized OseeEnumEntry valueOfAllowNullReturn(String entryName) {
      if (entryName != null) {
         for (OseeEnumEntry entry : enumSet) {
            if (entry.name().equals(entryName)) {
               return entry;
            }
         }
      }
      return null;
   }

   private synchronized OseeEnumEntry valueOfAllowNullReturn(int ordinal) throws OseeArgumentException {
      for (OseeEnumEntry entry : enumSet) {
         if (entry.ordinal() == ordinal) {
            return entry;
         }
      }
      return null;
   }

   public synchronized OseeEnumEntry valueOf(String entryName) throws OseeArgumentException {
      OseeEnumEntry toReturn = valueOfAllowNullReturn(entryName);
      if (toReturn == null) {
         throw new OseeArgumentException(String.format("No enum const [%s].[%s]", getName(), entryName));
      }
      return toReturn;
   }

   public synchronized OseeEnumEntry valueOf(int ordinal) throws OseeArgumentException {
      OseeEnumEntry toReturn = valueOfAllowNullReturn(ordinal);
      if (toReturn == null) {
         throw new OseeArgumentException(String.format("No enum const [%s] - ordinal [%s]", getName(), ordinal));
      }
      return toReturn;
   }

   private void checkEnumEntryName(String name) throws OseeArgumentException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("Enum entry name cannot be null");
      }
      OseeEnumEntry entry = valueOfAllowNullReturn(name);
      if (entry != null) {
         throw new OseeArgumentException(String.format("Unique enum entry name violation - %s already exists.", entry));
      }
   }

   private void checkOrdinal(int ordinal) throws OseeArgumentException {
      if (ordinal < 0) {
         throw new OseeArgumentException("Enum entry ordinal cannot be of negative value");
      }
      OseeEnumEntry entry = valueOfAllowNullReturn(ordinal);
      if (entry != null) {
         throw new OseeArgumentException(String.format("Unique enum entry ordinal violation - %s already exists.",
               entry));
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof OseeEnumType) {
         return super.equals(obj);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public String toString() {
      return getName();
   }

   public final class OseeEnumEntry implements Comparable<OseeEnumEntry> {
      private final String guid;
      private final int ordinal;
      private final String name;

      private OseeEnumEntry(String guid, String name, int ordinal) {
         this.name = name;
         this.guid = guid;
         this.ordinal = ordinal;
      }

      public String getGuid() {
         return guid;
      }

      public String name() {
         return name;
      }

      public int ordinal() {
         return ordinal;
      }

      public String getEnumTypeName() {
         return OseeEnumType.this.getName();
      }

      public int getEnumTypeId() {
         return OseeEnumType.this.getTypeId();
      }

      public OseeEnumType getDeclaringClass() {
         return OseeEnumType.this;
      }

      public OseeEnumEntry[] values() {
         return OseeEnumType.this.values();
      }

      public Pair<String, Integer> asPair() {
         return new Pair<String, Integer>(name(), ordinal());
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof OseeEnumEntry) {
            final OseeEnumEntry other = (OseeEnumEntry) obj;
            boolean result = true;
            if (other.name != null && name != null) {
               result &= other.name.equals(name);
            } else {
               result &= other.name == null && name == null;
            }
            return result && ordinal == other.ordinal && getDeclaringClass().equals(other.getDeclaringClass());
         }
         return false;
      }

      @Override
      public int hashCode() {
         final int prime = 37;
         int result = prime * 17 + (name != null ? name.hashCode() : 0);
         result = prime * result + ordinal;
         result = prime * result + getDeclaringClass().hashCode();
         return result;
      }

      @Override
      public String toString() {
         return String.format("[%s].[%s:%s]", getEnumTypeName(), name, ordinal);
      }

      @Override
      public int compareTo(OseeEnumEntry other) {
         return this.ordinal() - other.ordinal();
      }
   }

}