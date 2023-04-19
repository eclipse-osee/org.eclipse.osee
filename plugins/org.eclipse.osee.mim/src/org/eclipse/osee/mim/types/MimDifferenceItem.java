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
   private final String name;
   private final Long artTypeId;
   private final String artTypeName;
   private boolean added = false;
   private boolean deleted = false;
   private final Map<Long, MimAttributeChange> attributeChanges;
   private final Map<ArtifactId, MimRelationChange> relationChanges;

   public MimDifferenceItem(ArtifactId artId, String name, Long artTypeId, String artTypeName) {
      this.artId = artId;
      this.name = name;
      this.artTypeId = artTypeId;
      this.artTypeName = artTypeName;
      this.attributeChanges = new HashMap<>();
      this.relationChanges = new HashMap<>();
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public String getName() {
      return name;
   }

   public Long getArtType() {
      return artTypeId;
   }

   public String getArtTypeName() {
      return artTypeName;
   }

   public Map<Long, MimAttributeChange> getAttributeChanges() {
      return attributeChanges;
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

   public void addAttributeChange(Long attrId, String attrName, String oldValue, String newValue) {
      attributeChanges.put(attrId, new MimAttributeChange(attrId, attrName, oldValue, newValue));
   }

   /**
    * @param relationId
    * @param artId The artIdB of the change item
    * @param added Used to specify if the relation represents an addition. Otherwise assume it represents a deletion.
    * Will need to modify to support merges and other types.
    */
   public void addRelationChange(Long relationTypeId, ArtifactId artIdA, ArtifactId artIdB, String artBName, boolean added) {
      relationChanges.put(artId, new MimRelationChange(relationTypeId, artIdA, artIdB, artBName, added));
   }

}