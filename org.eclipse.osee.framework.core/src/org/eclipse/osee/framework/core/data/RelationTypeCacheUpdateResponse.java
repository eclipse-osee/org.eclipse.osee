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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheUpdateResponse {

   private final List<RelationTypeRow> rows;
   private final Map<Integer, Integer[]> relToArtAB;

   public RelationTypeCacheUpdateResponse(List<RelationTypeRow> rows, Map<Integer, Integer[]> relToArtAB) {
      this.rows = rows;
      this.relToArtAB = relToArtAB;
   }

   public List<RelationTypeRow> getRelationTypeRows() {
      return rows;
   }

   public Map<Integer, Integer[]> getRelToArtType() {
      return relToArtAB;
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

      public ModificationType getModificationType() {
         return null;
      }

      public String getSideBName() {
         return null;
      }

      public String getSideAName() {
         return null;
      }

      public String getDefaultOrderTypeGuid() {
         return null;
      }

      public RelationTypeMultiplicity getMultiplicity() {
         return null;
      }
   }

   public static RelationTypeCacheUpdateResponse fromCache(Collection<RelationType> types) throws OseeCoreException {
      List<RelationTypeRow> rows = new ArrayList<RelationTypeRow>();
      Map<Integer, Integer[]> relToArtAB = new HashMap<Integer, Integer[]>();
      for (RelationType item : types) {
         rows.add(new RelationTypeRow(item.getId(), item.getGuid(), item.getName(), item.getModificationType()));

         relToArtAB.put(item.getId(), new Integer[] {item.getArtifactTypeSideA().getId(),
               item.getArtifactTypeSideB().getId()});
      }
      return new RelationTypeCacheUpdateResponse(rows, relToArtAB);
   }
}
