/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Jeff C. Phillips
 */
public final class RelationChange extends Change {
   private final static LoadChangeType changeType = LoadChangeType.relation;

   private final int bArtId;
   private final Artifact endTxBArtifact;
   private final int relLinkId;
   private final String rationale;
   private final String wasValue;
   private final RelationType relationType;

   public RelationChange(BranchId branch, long sourceGamma, int aArtId, TransactionDelta txDelta, ModificationType modType, int bArtId, int relLinkId, String rationale, String wasValue, RelationType relationType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta, Artifact endTxBArtifact) {
      super(branch, sourceGamma, aArtId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.relationType = relationType;
      this.endTxBArtifact = endTxBArtifact;
      this.wasValue = wasValue;
   }

   /**
    * @return the bArtId
    */
   public int getBArtId() {
      return bArtId;
   }

   public Artifact getEndTxBArtifact() {
      return endTxBArtifact;
   }

   /**
    * @return the relLinkId
    */
   public int getRelLinkId() {
      return relLinkId;
   }

   /**
    * @return the rationale
    */
   public String getRationale() {
      return rationale;
   }

   /**
    * @return the relationType
    */
   public RelationType getRelationType() {
      return relationType;
   }

   @Override
   public LoadChangeType getChangeType() {
      return changeType;
   }

   @Override
   public String getName() {
      return getArtifactName() + " <-> " + getEndTxBArtifact().getName();
   }

   @Override
   public String getItemTypeName() {
      return relationType.getName();
   }

   @Override
   public String getIsValue() {
      return getRationale();
   }

   @Override
   public String getItemKind() {
      return "Relation";
   }

   @Override
   public String getWasValue() {
      return wasValue;
   }

   @Override
   public long getItemTypeId() {
      return relationType.getId();
   }

   @Override
   public int getItemId() {
      return relLinkId;
   }
}
