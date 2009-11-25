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
import org.eclipse.osee.framework.core.model.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheUpdateResponse {

   private final List<RelationTypeRow> rows;

   public RelationTypeCacheUpdateResponse(List<RelationTypeRow> rows) {
      this.rows = rows;
   }

   public List<RelationTypeRow> getRelationTypeRows() {
      return rows;
   }

   public static final class RelationTypeRow {
      private final int id;
      private final String name;
      private final String guid;
      private final ModificationType modType;

      protected RelationTypeRow(int id, String guid, String name, ModificationType modType) {
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

      public static RelationTypeRow fromArray(String[] data) {
         int id = Integer.valueOf(data[0]);
         String guid = data[1];
         String name = data[2];
         ModificationType modType = ModificationType.valueOf(data[3]);
         return new RelationTypeRow(id, guid, name, modType);
      }
   }

   public static RelationTypeCacheUpdateResponse fromCache(IOseeCache<RelationType> cache) throws OseeCoreException {
      List<RelationTypeRow> rows = new ArrayList<RelationTypeRow>();
      for (RelationType item : cache.getAll()) {

         rows.add(new RelationTypeRow(item.getId(), item.getGuid(), item.getName(), item.getModificationType()));
      }
      return new RelationTypeCacheUpdateResponse(rows);
   }
}
