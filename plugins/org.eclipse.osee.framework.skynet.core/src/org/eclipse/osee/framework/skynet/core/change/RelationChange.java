/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public final class RelationChange extends Change {
   private final static ChangeType changeType = ChangeType.Relation;

   private final ArtifactId bArtId;
   private final Artifact endTxBArtifact;
   private final RelationId relLinkId;
   private final String rationale;
   private final String wasValue;
   private final RelationTypeToken relationType;

   public RelationChange(BranchToken branch, GammaId sourceGamma, ArtifactId aArtId, TransactionDelta txDelta, ModificationType modType, ArtifactId bArtId, RelationId relLinkId, String rationale, String wasValue, RelationTypeToken relationType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta, Artifact endTxBArtifact) {
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

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   @Override
   public ChangeType getChangeType() {
      return changeType;
   }

   @Override
   public String getName() {
      return String.format("%s <- [%s] -> %s", getArtifactName(), getItemTypeName(), getEndTxBArtifact().getName());
   }

   @Override
   public String getNameOrToken() {
      return String.format("%s <- [%s] -> %s", getArtifactNameOrToken(), getItemTypeName(),
         (UserManager.isShowTokenForChangeName() ? getEndTxBArtifact().toStringWithId() : getEndTxBArtifact().getName()));
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
