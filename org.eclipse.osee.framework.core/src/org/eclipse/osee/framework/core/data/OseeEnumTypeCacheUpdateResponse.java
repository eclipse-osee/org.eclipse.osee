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
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class OseeEnumTypeCacheUpdateResponse {
   private final List<String[]> enumTypeRows;
   private final List<String[]> enumEntryRows;

   public OseeEnumTypeCacheUpdateResponse(List<String[]> enumTypeRows, List<String[]> enumEntryRows) {
      this.enumTypeRows = enumTypeRows;
      this.enumEntryRows = enumEntryRows;
   }

   public List<String[]> getEnumTypeRows() {
      return enumTypeRows;
   }

   public List<String[]> getEnumEntryRows() {
      return enumEntryRows;
   }

   public static OseeEnumTypeCacheUpdateResponse fromCache(Collection<OseeEnumType> types) throws OseeCoreException {
      List<String[]> enumTypeRows = new ArrayList<String[]>();
      List<String[]> enumEntryRows = new ArrayList<String[]>();
      for (OseeEnumType type : types) {
         enumTypeRows.add(new String[] {String.valueOf(type.getId()), type.getModificationType().toString(),
               type.getGuid(), type.getName()});
         for (OseeEnumEntry entry : type.values()) {
            enumEntryRows.add(new String[] {type.getGuid(), entry.getGuid(), entry.getName(),
                  String.valueOf(entry.ordinal())});
         }
      }
      return new OseeEnumTypeCacheUpdateResponse(enumTypeRows, enumEntryRows);
   }
}
