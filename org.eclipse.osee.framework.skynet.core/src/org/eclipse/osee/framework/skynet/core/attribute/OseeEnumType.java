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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType {

   private final int enumTypeId;
   private final String enumTypeName;

   private final List<OseeEnumEntry> enumSet;

   protected OseeEnumType(int enumTypeId, String enumTypeName) {
      this.enumTypeId = enumTypeId;
      this.enumTypeName = enumTypeName;
      this.enumSet = new ArrayList<OseeEnumEntry>();
   }

   protected void addEnum(String name, int ordinal) throws OseeArgumentException {
      checkEnumEntryName(name);
      checkOrdinal(ordinal);
      OseeEnumEntry entry = new OseeEnumEntry(name, ordinal);
      enumSet.add(entry);
   }

   public OseeEnumEntry[] values() {
      return enumSet.toArray(new OseeEnumEntry[enumSet.size()]);
   }

   public Set<String> valuesAsOrderedStringSet() {
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

   public OseeEnumEntry valueOf(String entryName) {
      for (OseeEnumEntry entry : enumSet) {
         if (entry.name().equals(entryName)) {
            return entry;
         }
      }
      return null;
   }

   public OseeEnumEntry valueOf(int ordinal) {
      for (OseeEnumEntry entry : enumSet) {
         if (entry.ordinal() == ordinal) {
            return entry;
         }
      }
      return null;
   }

   private void checkEnumEntryName(String name) throws OseeArgumentException {
      if (!Strings.isValid(name)) throw new OseeArgumentException("Enum entry name cannot be null");
      OseeEnumEntry entry = valueOf(name);
      if (entry != null) throw new OseeArgumentException(String.format(
            "Unique enum entry name violation - [%s] already exists.", entry));
   }

   private void checkOrdinal(int ordinal) throws OseeArgumentException {
      if (ordinal < 0) throw new OseeArgumentException("Enum entry ordinal cannot be of negative value");
      OseeEnumEntry entry = valueOf(ordinal);
      if (entry != null) throw new OseeArgumentException(String.format(
            "Unique enum entry ordinal violation - [%s] already exists.", entry));
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
         return result & (getEnumTypeId() == other.getEnumTypeId());
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

      public OseeEnumEntry(String name, int ordinal) {
         this.name = name;
         this.ordinal = ordinal;
      }

      public String name() {
         return name;
      }

      public int ordinal() {
         return ordinal;
      }

      public String getTypeName() {
         return getTypeName();
      }

      public int getEnumTypeId() {
         return getEnumTypeId();
      }

      public OseeEnumType getDeclaringClass() {
         return OseeEnumType.this;
      }

      public OseeEnumEntry[] values() {
         return values();
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
            return result & (ordinal == other.ordinal) & getDeclaringClass().equals(other.getDeclaringClass());
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
         return String.format("<%s:%s>", name, ordinal);
      }

      /* (non-Javadoc)
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      @Override
      public int compareTo(OseeEnumEntry o) {
         return 0;
      }
   }
}