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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Jeff C. Phillips
 */
public final class RelationChange extends Change {
   private final static LoadChangeType changeType = LoadChangeType.relation;

   private final ArtifactId bArtId;
   private final Artifact endTxBArtifact;
   private final RelationId relLinkId;
   private final String rationale;
   private final String wasValue;
   private final RelationType relationType;

   public RelationChange(BranchId branch, GammaId sourceGamma, ArtifactId aArtId, TransactionDelta txDelta, ModificationType modType, ArtifactId bArtId, RelationId relLinkId, String rationale, String wasValue, RelationType relationType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta, Artifact endTxBArtifact) {
      super(branch, sourceGamma, aArtId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.relationType = relationType;
      this.endTxBArtifact = endTxBArtifact;
      this.wasValue = wasValue;
   }

   public ArtifactId getBArtId() {
      return bArtId;
   }

   public Artifact getEndTxBArtifact() {
      return endTxBArtifact;
   }

   public RelationId getRelLinkId() {
      return relLinkId;
   }

   public String getRationale() {
      return rationale;
   }

   public RelationType getRelationType() {
      return relationType;
   }

   @Override
   public LoadChangeType getChangeType() {
      return changeType;
   }

   @Override
   public String getName() {
      return String.format("%s <- [%s] -> %s", getArtifactName(), getItemTypeName(),
         getEndTxBArtifact().toStringWithId());
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
   public Id getItemTypeId() {
      return relationType;
   }

   @Override
   public Id getItemId() {
      return relLinkId;
   }
}
