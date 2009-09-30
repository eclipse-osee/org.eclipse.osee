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
package org.eclipse.osee.framework.skynet.core.types.field;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class EnumEntryField extends AbstractOseeField<List<OseeEnumEntry>> {

   private final OseeEnumTypeCache cache;
   private final OseeEnumType type;

   public EnumEntryField(OseeEnumTypeCache cache, OseeEnumType type) {
      super();
      this.type = type;
      this.cache = cache;
   }

   @Override
   public List<OseeEnumEntry> get() throws OseeCoreException {
      return cache.getEnumEntries(type);
   }

   @Override
   public void set(List<OseeEnumEntry> entries) throws OseeCoreException {
      List<OseeEnumEntry> oldEntries = get();
      cache.cacheEnumEntries(type, entries);
      List<OseeEnumEntry> newEntries = get();
      isDirty |= ChangeUtil.isDifferent(oldEntries, newEntries);
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
