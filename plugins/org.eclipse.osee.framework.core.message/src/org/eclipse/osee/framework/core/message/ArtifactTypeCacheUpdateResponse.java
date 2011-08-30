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
package org.eclipse.osee.framework.core.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheUpdateResponse {

   private final List<ArtifactTypeRow> rows;
   private final Map<Integer, Integer[]> baseToSuper;
   private final List<Triplet<Long, String, Long>> artAttrs;

   public ArtifactTypeCacheUpdateResponse(List<ArtifactTypeRow> rows, Map<Integer, Integer[]> baseToSuper, List<Triplet<Long, String, Long>> artAttrs) {
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

   public List<Triplet<Long, String, Long>> getAttributeTypes() {
      return artAttrs;
   }

   public static final class ArtifactTypeRow {
      private final int id;
      private final String name;
      private final Long guid;
      private final boolean isAbstract;
      private StorageState storageState;

      protected ArtifactTypeRow(int id, Long guid, String name, boolean isAbstract, StorageState storageState) {
         this.id = id;
         this.guid = guid;
         this.name = name;
         this.isAbstract = isAbstract;
         this.storageState = storageState;
      }

      public int getId() {
         return id;
      }

      public String getName() {
         return name;
      }

      public Long getGuid() {
         return guid;
      }

      public boolean isAbstract() {
         return isAbstract;
      }

      public StorageState getStorageState() {
         return storageState;
      }

      public void setStorageState(StorageState storageState) {
         this.storageState = storageState;
      }

      public String[] toArray() {
         return new String[] {
            String.valueOf(getId()),
            String.valueOf(getGuid()),
            getName(),
            String.valueOf(isAbstract()),
            getStorageState().name()};
      }

      public static ArtifactTypeRow fromArray(String[] data) {
         int id = Integer.valueOf(data[0]);
         long remoteId = Long.parseLong(data[1]);
         String name = data[2];
         boolean isAbstract = Boolean.valueOf(data[3]);
         StorageState storageState = StorageState.valueOf(data[4]);
         return new ArtifactTypeRow(id, remoteId, name, isAbstract, storageState);
      }

      @Override
      public String toString() {
         return String.format("%s (%s)", name, guid);
      }
   }

   public static ArtifactTypeCacheUpdateResponse fromCache(Collection<ArtifactType> types) throws OseeCoreException {
      List<ArtifactTypeRow> rows = new ArrayList<ArtifactTypeRow>();
      Map<Integer, Integer[]> baseToSuper = new HashMap<Integer, Integer[]>();
      List<Triplet<Long, String, Long>> artAttrs = new ArrayList<Triplet<Long, String, Long>>();
      for (ArtifactType artType : types) {
         rows.add(new ArtifactTypeRow(artType.getId(), artType.getGuid(), artType.getName(), artType.isAbstract(),
            artType.getStorageState()));

         Integer artId = artType.getId();

         Collection<ArtifactType> superTypes = artType.getSuperArtifactTypes();
         if (!superTypes.isEmpty()) {
            Integer[] intSuperTypes = new Integer[superTypes.size()];
            int index = 0;
            for (ArtifactType superType : superTypes) {
               intSuperTypes[index++] = superType.getId();
            }
            baseToSuper.put(artId, intSuperTypes);
         }

         for (Entry<IOseeBranch, Collection<AttributeType>> entry : artType.getLocalAttributeTypes().entrySet()) {
            IOseeBranch branch = entry.getKey();
            Collection<AttributeType> attrTypes = entry.getValue();
            for (AttributeType type : attrTypes) {
               artAttrs.add(new Triplet<Long, String, Long>(artType.getGuid(), branch.getGuid(), type.getGuid()));
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
