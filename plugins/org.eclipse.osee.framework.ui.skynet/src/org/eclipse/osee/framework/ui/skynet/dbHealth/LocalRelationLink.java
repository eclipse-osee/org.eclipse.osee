/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;

public class LocalRelationLink {
   public RelationId relLinkId;
   public ArtifactId aArtId, bArtId;
   public TransactionId transIdForArtifactDeletion, commitTrans;
   public int modType;
   public BranchId branch;
   public GammaId gammaId;
   public TransactionId relTransId;

   public LocalRelationLink(RelationId relLinkId, GammaId gammaId, TransactionId transactionId, BranchId branch, ArtifactId aArtId, ArtifactId bArtId, TransactionId transIdForArtifactDeletion, TransactionId commitTrans, int modType) {
      this.aArtId = aArtId;
      this.bArtId = bArtId;
      this.branch = branch;
      this.gammaId = gammaId;
      this.relLinkId = relLinkId;
      this.relTransId = transactionId;
      this.transIdForArtifactDeletion = transIdForArtifactDeletion;
      this.commitTrans = commitTrans;
      this.modType = modType;
   }

   @Override
   public String toString() {
      return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", gammaId, relTransId.getIdString(),
         relLinkId.getIdString(), branch.getIdString(), aArtId.getIdString(), bArtId.getIdString(),
         transIdForArtifactDeletion.getIdString(), commitTrans.getIdString(), modType);
   }
}