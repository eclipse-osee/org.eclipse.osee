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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;

public class LocalRelationLink {
   public int relLinkId, aArtId, bArtId, transIdForArtifactDeletion, commitTrans, modType;
   public BranchId branch;
   public GammaId gammaId;
   public Long relTransId;

   public LocalRelationLink(int relLinkId, GammaId gammaId, Long transactionId, BranchId branch, int aArtId, int bArtId, int transIdForArtifactDeletion, int commitTrans, int modType) {
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
      return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", gammaId, relTransId, relLinkId, branch, aArtId, bArtId,
         transIdForArtifactDeletion, commitTrans, modType);
   }
}