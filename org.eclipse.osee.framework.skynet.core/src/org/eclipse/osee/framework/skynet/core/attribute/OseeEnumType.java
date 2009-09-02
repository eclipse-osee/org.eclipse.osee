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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BaseOseeType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumType extends BaseOseeType implements Comparable<OseeEnumType> {

   private final OseeTypeCache cache;

   public OseeEnumType(String guid, String enumTypeName, OseeTypeCache cache) {
      super(guid, enumTypeName);
      this.cache = cache;
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

   public OseeEnumEntry[] values() throws OseeCoreException {
      List<OseeEnumEntry> entries = cache.getEnumTypeData().getEnumEntries(this);
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

   public void setEntries(Collection<OseeEnumEntry> entries) throws OseeCoreException {
      List<OseeEnumEntry> oldEntries = cache.getEnumTypeData().getEnumEntries(this);
      cache.getEnumTypeData().cacheEnumEntries(this, entries);
      List<OseeEnumEntry> newEntries = cache.getEnumTypeData().getEnumEntries(this);
      boolean isEmpty1 =
            org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(oldEntries, newEntries).isEmpty();
      boolean isEmpty2 =
            org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(newEntries, oldEntries).isEmpty();
      if (getModificationType() != ModificationType.NEW && (!isEmpty1 || !isEmpty2)) {
         setModificationType(ModificationType.MODIFIED);
      }
   }

   @Override
   public int compareTo(OseeEnumType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   @Override
   public void setModificationType(ModificationType modificationType) {
      super.setModificationType(modificationType);
      if (modificationType.isDeleted()) {
         try {
            for (OseeEnumEntry entry : values()) {
               entry.setModificationType(modificationType);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

}