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
package org.eclipse.osee.framework.core.internal.fields;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class EnumEntryField extends CollectionField<OseeEnumEntry> {

   public EnumEntryField(Collection<OseeEnumEntry> enumEntries) {
      super(enumEntries);
   }

   @Override
   protected Collection<OseeEnumEntry> checkInput(Collection<OseeEnumEntry> input) throws OseeCoreException {
      checkEnumEntryIntegrity(input);
      Set<OseeEnumEntry> toReturn = new HashSet<OseeEnumEntry>();

      Set<OseeEnumEntry> newEntries = new HashSet<OseeEnumEntry>();
      Collection<OseeEnumEntry> currentEntries = get();
      for (OseeEnumEntry entry : input) {
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
            }
            if (wasFound) {
               toReturn.add(existingEntry);
               break;
            }
         }
         if (!wasFound) {
            newEntries.add(entry);
         }
      }
      toReturn.addAll(newEntries);
      return toReturn;
   }

   private void checkEnumEntryIntegrity(Collection<OseeEnumEntry> oseeEnumEntries) throws OseeCoreException {
      for (OseeEnumEntry entry : oseeEnumEntries) {
         Conditions.checkNotNullOrEmpty(entry.getName(), "Osee Enum Entry name");

         Conditions.checkExpressionFailOnTrue(entry.ordinal() < 0, "Osee Enum Entry ordinal must be greater than zero");

         for (OseeEnumEntry existingEntry : oseeEnumEntries) {
            if (!entry.equals(existingEntry)) {
               Conditions.checkExpressionFailOnTrue(entry.getName().equals(existingEntry.getName()),
                     "Unique enumEntry name violation - %s already exists.", entry);

               Conditions.checkExpressionFailOnTrue(
                     entry.ordinal() == existingEntry.ordinal(),
                     "Unique enumEntry ordinal violation - ordinal [%d] is used by existing entry:[%s] and new entry:[%s]",
                     entry.ordinal(), existingEntry, entry);
            }
         }
      }
   }

   @Override
   public void clearDirty() {
      super.clearDirty();
      try {
         for (OseeEnumEntry entry : get()) {
            entry.clearDirty();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public boolean isDirty() {
      boolean result = super.isDirty();
      if (!result) {
         try {
            for (OseeEnumEntry entry : get()) {
               if (entry.isDirty()) {
                  result = true;
                  break;
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return result;
   }
}
