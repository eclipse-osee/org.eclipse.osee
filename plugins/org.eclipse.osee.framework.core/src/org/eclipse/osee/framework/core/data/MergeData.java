/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;

public class MergeData {
   private final ArtifactId artId;
   private final ArtifactTypeToken artType;
   private final String name;
   private final ConflictType conflictType;
   private final ConflictStatus conflictStatus;
   private final Long conflictId;
   private final AttributeMergeData attrMergeData;

   public MergeData() {
      // Needed for jax-rs
      this.artId = ArtifactId.SENTINEL;
      this.artType = ArtifactTypeToken.SENTINEL;
      this.name = "";
      this.conflictType = ConflictType.ATTRIBUTE;
      this.conflictStatus = ConflictStatus.UNTOUCHED;
      this.conflictId = -1L;
      this.attrMergeData = new AttributeMergeData();
   }

   public MergeData(ArtifactId artId, ArtifactTypeToken artType, String name, ConflictType conflictType, ConflictStatus conflictStatus, Long conflictId, AttributeMergeData attrMergeData) {
      this.artId = artId;
      this.artType = artType;
      this.name = name;
      this.conflictType = conflictType;
      this.conflictStatus = conflictStatus;
      this.conflictId = conflictId;
      this.attrMergeData = attrMergeData;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public String getName() {
      return name;
   }

   public ConflictType getConflictType() {
      return conflictType;
   }

   public ConflictStatus getConflictStatus() {
      return conflictStatus;
   }

   public Long getConflictId() {
      return conflictId;
   }

   public AttributeMergeData getAttrMergeData() {
      return attrMergeData;
   }

}
