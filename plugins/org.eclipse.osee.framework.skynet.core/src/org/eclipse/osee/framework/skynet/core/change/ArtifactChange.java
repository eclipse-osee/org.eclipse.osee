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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactChange extends Change {
   private static ChangeType changeType = ChangeType.Artifact;
   private final String isValue;
   private final String wasValue;

   public ArtifactChange(BranchToken branch, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, String isValue, String wasValue, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      super(branch, sourceGamma, artId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.isValue = isValue;
      this.wasValue = wasValue;
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getNameOrToken() {
      return getArtifactNameOrToken();
   }

   @Override
   public String getItemTypeName() {
      return getChangeArtifact().getArtifactType().getName();
   }

   @Override
   public String getIsValue() {
      return isValue;
   }

   @Override
   public String getItemKind() {
      return "Artifact";
   }

   @Override
   public String getWasValue() {
      return wasValue;
   }

   @Override
   public Id getItemTypeId() {
      return getChangeArtifact().getArtifactType();
   }

   @Override
   public ArtifactId getItemId() {
      return getArtId();
   }

   @Override
   public ChangeType getChangeType() {
      return changeType;
   }
}
