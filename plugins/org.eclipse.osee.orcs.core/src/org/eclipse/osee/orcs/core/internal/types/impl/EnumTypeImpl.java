/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.data.EnumType;

/**
 * @author Roberto E. Escobar
 */
public final class EnumTypeImpl extends NamedIdBase implements EnumType {

   private final List<EnumEntry> entries;

   public EnumTypeImpl(Long uuid, String name, List<EnumEntry> entries) {
      super(uuid, name);
      this.entries = entries;
   }

   private List<EnumEntry> getValues() {
      return entries;
   }

   @Override
   public EnumEntry[] values() {
      List<EnumEntry> values = getValues();
      return values.toArray(new EnumEntry[values.size()]);
   }

   @Override
   public EnumEntry getEntryByName(String entryName) {
      EnumEntry toReturn = null;
      for (EnumEntry entry : getValues()) {
         if (entry.getName().equals(entryName)) {
            toReturn = entry;
            break;
         }
      }
      return toReturn;
   }

   @Override
   public Set<String> valuesAsOrderedStringSet() {
      Set<String> values = new LinkedHashSet<>();
      for (EnumEntry oseeEnumEntry : values()) {
         values.add(oseeEnumEntry.getName());
      }
      return values;
   }

}
