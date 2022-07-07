/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Ryan T. Baldwin
 */
public class MimDifferenceItem {
   private final ArtifactId artId;
   private boolean added = false;
   private boolean deleted = false;
   private final Map<Long, String[]> diffs;
   private final Map<ArtifactId, MimRelationChange> relationChanges;

   public MimDifferenceItem(ArtifactId artId) {
      this.artId = artId;
      this.diffs = new HashMap<>();
      this.relationChanges = new HashMap<>();
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public Map<Long, String[]> getDiffs() {
      return diffs;
   }

   public Map<ArtifactId, MimRelationChange> getRelationChanges() {
      return relationChanges;
   }

   public boolean isAdded() {
      return added;
   }

   public void setAdded(boolean added) {
      this.added = added;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

   public void addAttributeChange(Long attrId, String oldValue, String newValue) {
      String[] vals = new String[2];
      vals[0] = oldValue;
      vals[1] = newValue;
      diffs.put(attrId, vals);
   }

   /**
    * @param relationId
    * @param artId The artIdB of the change item
    * @param added Used to specify if the relation represents an addition. Otherwise assume it represents a deletion.
    * Will need to modify to support merges and other types.
    */
   public void addRelationChange(Long relationId, ArtifactId artId, boolean added) {
      relationChanges.put(artId, new MimRelationChange(artId, relationId, added));
   }

}