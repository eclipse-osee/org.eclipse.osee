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
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheUpdateResponse {

   private final List<ArtifactTypeRow> rows;
   private final Map<Integer, Integer[]> baseToSuper;
   private final List<Triplet<Integer, Integer, Integer>> artAttrs;

   public ArtifactTypeCacheUpdateResponse(List<ArtifactTypeRow> rows, Map<Integer, Integer[]> baseToSuper, List<Triplet<Integer, Integer, Integer>> artAttrs) {
      this.rows = rows;
      this.baseToSuper = baseToSuper;
      this.artAttrs = artAttrs;
   }

   public List<ArtifactTypeRow> getArtTypeRows() {
      return rows;
   }

   public Map<Integer, Integer[]> getBaseToSuperTypes() {
      return baseToSuper;
   }

   public List<Triplet<Integer, Integer, Integer>> getAttributeTypes() {
      return artAttrs;
   }

   public static final class ArtifactTypeRow {
      private final int id;
      private final String name;
      private final String guid;
      private final boolean isAbstract;
      private final ModificationType modType;

      protected ArtifactTypeRow(int id, String guid, String name, boolean isAbstract, ModificationType modType) {
         super();
         this.id = id;
         this.guid = guid;
         this.name = name;
         this.isAbstract = isAbstract;
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

      public boolean isAbstract() {
         return isAbstract;
      }

      public ModificationType getModType() {
         return modType;
      }

      public String[] toArray() {
         return new String[] {String.valueOf(getId()), getGuid(), getName(), String.valueOf(isAbstract()),
               getModType().name()};
      }

      public static ArtifactTypeRow fromArray(String[] data) {
         int id = Integer.valueOf(data[0]);
         String guid = data[1];
         String name = data[2];
         boolean isAbstract = Boolean.valueOf(data[3]);
         ModificationType modType = ModificationType.valueOf(data[4]);
         return new ArtifactTypeRow(id, guid, name, isAbstract, modType);
      }

      @Override
      public String toString() {
         return String.format("%s (%s)", name, guid);
      }
   }

   public static ArtifactTypeCacheUpdateResponse fromCache(Collection<ArtifactType> types) throws OseeCoreException {
      List<ArtifactTypeRow> rows = new ArrayList<ArtifactTypeRow>();
      Map<Integer, Integer[]> baseToSuper = new HashMap<Integer, Integer[]>();
      List<Triplet<Integer, Integer, Integer>> artAttrs = new ArrayList<Triplet<Integer, Integer, Integer>>();
      for (ArtifactType art : types) {
         rows.add(new ArtifactTypeRow(art.getId(), art.getGuid(), art.getName(), art.isAbstract(),
               art.getModificationType()));

         Integer artId = art.getId();
         Collection<ArtifactType> superTypes = art.getSuperArtifactTypes();
         if (!superTypes.isEmpty()) {
            Integer[] intSuperTypes = new Integer[superTypes.size()];
            int index = 0;
            for (ArtifactType superType : superTypes) {
               intSuperTypes[index++] = superType.getId();
            }
            baseToSuper.put(artId, intSuperTypes);
         }

         for (Entry<Branch, Collection<AttributeType>> entry : art.getLocalAttributeTypes().entrySet()) {
            Integer branchId = entry.getKey().getId();
            Collection<AttributeType> attrTypes = entry.getValue();
            for (AttributeType type : attrTypes) {
               artAttrs.add(new Triplet<Integer, Integer, Integer>(artId, branchId, type.getId()));
            }

         }
      }
      return new ArtifactTypeCacheUpdateResponse(rows, baseToSuper, artAttrs);
   }

   @Override
   public String toString() {
      return "ArtifactTypeCacheUpdateResponse [artAttrs=" + artAttrs + ", baseToSuper=" + baseToSuper + ", rows=" + rows + "]";
   }

}
