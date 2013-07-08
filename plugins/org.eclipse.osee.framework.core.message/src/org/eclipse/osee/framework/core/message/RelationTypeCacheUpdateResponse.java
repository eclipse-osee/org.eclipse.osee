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

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.StorageState;

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
      private final long guid;
      private StorageState storageState;

      private final String sideAName;
      private final String sideBName;
      private final int artifactTypeSideA;
      private final int artifactTypeSideB;
      private final RelationTypeMultiplicity multiplicity;
      private final String defaultOrderTypeGuid;

      public RelationTypeRow(int id, String name, long guid, StorageState storageState, String sideAName, String sideBName, int artifactTypeSideA, int artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) {
         super();
         this.id = id;
         this.name = name;
         this.guid = guid;
         this.storageState = storageState;
         this.sideAName = sideAName;
         this.sideBName = sideBName;
         this.artifactTypeSideA = artifactTypeSideA;
         this.artifactTypeSideB = artifactTypeSideB;
         this.multiplicity = multiplicity;
         this.defaultOrderTypeGuid = defaultOrderTypeGuid;
      }

      public int getArtifactTypeSideA() {
         return artifactTypeSideA;
      }

      public int getArtifactTypeSideB() {
         return artifactTypeSideB;
      }

      public int getId() {
         return id;
      }

      public String getName() {
         return name;
      }

      public long getGuid() {
         return guid;
      }

      public StorageState getStorageState() {
         return storageState;
      }

      public void setStorageState(StorageState storageState) {
         this.storageState = storageState;
      }

      public String getSideBName() {
         return sideBName;
      }

      public String getSideAName() {
         return sideAName;
      }

      public String getDefaultOrderTypeGuid() {
         return defaultOrderTypeGuid;
      }

      public RelationTypeMultiplicity getMultiplicity() {
         return multiplicity;
      }

      public String[] toArray() {
         return new String[] {
            String.valueOf(getId()),
            String.valueOf(getGuid()),
            getName(),
            getStorageState().name(),
            getSideAName(),
            getSideBName(),
            String.valueOf(getArtifactTypeSideA()),
            String.valueOf(getArtifactTypeSideB()),
            getMultiplicity().name(),
            getDefaultOrderTypeGuid()};
      }

      public static RelationTypeRow fromArray(String[] data) {
         int index = 0;

         int id = Integer.valueOf(data[index++]);
         long guid = Long.valueOf(data[index++]);
         String name = data[index++];
         StorageState storageState = StorageState.valueOf(data[index++]);

         String sideAName = data[index++];
         String sideBName = data[index++];
         int artifactTypeSideA = Integer.valueOf(data[index++]);
         int artifactTypeSideB = Integer.valueOf(data[index++]);
         RelationTypeMultiplicity multiplicity = RelationTypeMultiplicity.valueOf(data[index++]);
         String defaultOrderTypeGuid = data[index++];

         return new RelationTypeRow(id, name, guid, storageState, sideAName, sideBName, artifactTypeSideA,
            artifactTypeSideB, multiplicity, defaultOrderTypeGuid);
      }
   }

}
