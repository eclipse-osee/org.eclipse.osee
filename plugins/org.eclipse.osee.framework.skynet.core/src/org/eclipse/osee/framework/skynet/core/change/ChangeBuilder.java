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
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;

/**
 * @author Jeff C. Phillips
 */
public abstract class ChangeBuilder {
   private final int sourceGamma;
   private final ArtifactId artId;
   private final TransactionDelta txDelta;
   private ModificationType modType;
   private final BranchId branch;
   private final ArtifactTypeId artifactType;
   private final boolean isHistorical;

   public ChangeBuilder(BranchId branch, ArtifactTypeId artifactType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical) {
      super();
      this.sourceGamma = sourceGamma;
      this.artId = ArtifactId.valueOf(artId);
      this.txDelta = txDelta;
      this.modType = modType;
      this.branch = branch;
      this.artifactType = artifactType;
      this.isHistorical = isHistorical;
   }

   public int getSourceGamma() {
      return sourceGamma;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public TransactionDelta getTxDelta() {
      return txDelta;
   }

   public ModificationType getModType() {
      return modType;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public BranchId getBranch() {
      return branch;
   }

   public ArtifactTypeId getArtifactType() {
      return artifactType;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

}
