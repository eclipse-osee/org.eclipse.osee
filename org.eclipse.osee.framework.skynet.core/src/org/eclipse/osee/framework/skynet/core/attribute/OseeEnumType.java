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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType {

   private final int enumTypeId;
   private final String enumTypeName;

   private final List<OseeEnumEntry> enumSet;
   private boolean isDeleted;

   protected OseeEnumType(int enumTypeId, String enumTypeName) {
      this.enumTypeId = enumTypeId;
      this.enumTypeName = enumTypeName;
      this.enumSet = new ArrayList<OseeEnumEntry>();
      this.isDeleted = false;
   }

   protected void internalSetDeleted(boolean deleted) {
      this.isDeleted = deleted;
   }

   protected synchronized void internalAddEnum(String name, int ordinal) throws OseeArgumentException {
      checkEnumEntryName(name);
      checkOrdinal(ordinal);
      OseeEnumEntry entry = new OseeEnumEntry(name, ordinal);
      enumSet.add(entry);
   }

   protected void internalAddEnum(ObjectPair<String, Integer> entry) throws OseeArgumentException {
      internalAddEnum(entry.object1, entry.object2);
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

   public int getEnumTypeId() {
      return enumTypeId;
   }

   public String getEnumTypeName() {
      return enumTypeName;
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
         throw new OseeArgumentException(String.format("No enum const [%s].[%s]", getEnumTypeName(), entryName));
      }
      return toReturn;
   }

   public synchronized OseeEnumEntry valueOf(int ordinal) throws OseeArgumentException {
      OseeEnumEntry toReturn = valueOfAllowNullReturn(ordinal);
      if (toReturn == null) {
         throw new OseeArgumentException(String.format("No enum const [%s] - ordinal [%s]", getEnumTypeName(), ordinal));
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

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof OseeEnumType) {
         final OseeEnumType other = (OseeEnumType) obj;
         boolean result = true;
         if (other.getEnumTypeName() != null && getEnumTypeName() != null) {
            result &= other.getEnumTypeName().equals(getEnumTypeName());
         } else {
            result &= other.getEnumTypeName() == null && getEnumTypeName() == null;
         }
         return result & getEnumTypeId() == other.getEnumTypeId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 37;
      int result = prime * 17 + (getEnumTypeName() != null ? getEnumTypeName().hashCode() : 0);
      return prime * result + getEnumTypeId();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return enumTypeName;
   }

   public final class OseeEnumEntry implements Comparable<OseeEnumEntry> {
      private final int ordinal;
      private final String name;

      private OseeEnumEntry(String name, int ordinal) {
         this.name = name;
         this.ordinal = ordinal;
      }

      public String name() {
         return name;
      }

      public int ordinal() {
         return ordinal;
      }

      public String getEnumTypeName() {
         return OseeEnumType.this.getEnumTypeName();
      }

      public int getEnumTypeId() {
         return OseeEnumType.this.getEnumTypeId();
      }

      public OseeEnumType getDeclaringClass() {
         return OseeEnumType.this;
      }

      public OseeEnumEntry[] values() {
         return OseeEnumType.this.values();
      }

      public ObjectPair<String, Integer> asObjectPair() {
         return new ObjectPair<String, Integer>(name(), ordinal());
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
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
            return result & ordinal == other.ordinal & getDeclaringClass().equals(other.getDeclaringClass());
         }
         return false;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
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

      /* (non-Javadoc)
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      @Override
      public int compareTo(OseeEnumEntry other) {
         return this.ordinal() - other.ordinal();
      }
   }
}