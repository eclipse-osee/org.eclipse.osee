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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactChange extends Change {
   private static LoadChangeType changeType = LoadChangeType.artifact;
   private static Class<ArtifactChangeWorker> worker = ArtifactChangeWorker.class;
   private final String isValue;
   private final String wasValue;

   public ArtifactChange(BranchId branch, long sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, String isValue, String wasValue, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      super(branch, sourceGamma, artId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.isValue = isValue;
      this.wasValue = wasValue;
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getItemTypeName() {
      return getArtifactType().getName();
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
   public long getItemTypeId() {
      return getArtifactType().getId();
   }

   @Override
   public int getItemId() {
      return getArtId();
   }

   @Override
   public LoadChangeType getChangeType() {
      return changeType;
   }

   @Override
   public Class<? extends IChangeWorker> getWorker() {
      return worker;
   }
}
