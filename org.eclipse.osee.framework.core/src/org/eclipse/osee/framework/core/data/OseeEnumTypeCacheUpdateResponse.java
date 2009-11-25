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
import java.util.List;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeEnumType;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeCacheUpdateResponse {

   private final List<OseeEnumTypeRow> rows;

   public OseeEnumTypeCacheUpdateResponse(List<OseeEnumTypeRow> rows) {
      this.rows = rows;
   }

   public List<OseeEnumTypeRow> getAttrTypeRows() {
      return rows;
   }

   public static final class OseeEnumTypeRow {
      private final int id;
      private final String name;
      private final String guid;
      private final ModificationType modType;

      protected OseeEnumTypeRow(int id, String guid, String name, ModificationType modType) {
         super();
         this.id = id;
         this.guid = guid;
         this.name = name;
         this.modType = modType;
      }

      public int getId() {
         return id;
      }

      public String getName() {
         return name;
      }

      public String getGuid() {
         return guid;
      }

      public ModificationType getModType() {
         return modType;
      }

      public String[] toArray() {
         return new String[] {String.valueOf(getId()), getGuid(), getName(), getModType().name()};
      }

      public static OseeEnumTypeRow fromArray(String[] data) {
         int id = Integer.valueOf(data[0]);
         String guid = data[1];
         String name = data[2];
         ModificationType modType = ModificationType.valueOf(data[3]);
         return new OseeEnumTypeRow(id, guid, name, modType);
      }
   }

   public static OseeEnumTypeCacheUpdateResponse fromCache(IOseeCache<OseeEnumType> cache) throws OseeCoreException {
      List<OseeEnumTypeRow> rows = new ArrayList<OseeEnumTypeRow>();
      for (OseeEnumType item : cache.getAll()) {

         rows.add(new OseeEnumTypeRow(item.getId(), item.getGuid(), item.getName(), item.getModificationType()));
      }
      return new OseeEnumTypeCacheUpdateResponse(rows);
   }
}
