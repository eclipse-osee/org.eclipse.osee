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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Theron Virgin
 */
public abstract class ConflictBuilder {

   protected GammaId sourceGamma;
   protected GammaId destGamma;
   protected ArtifactId artId;
   protected TransactionToken toTransactionId;
   protected BranchToken sourceBranch;
   protected BranchToken destBranch;

   public ConflictBuilder(GammaId sourceGamma, GammaId destGamma, ArtifactId artId, TransactionToken toTransactionId, BranchToken sourceBranch, BranchToken destBranch) {
      super();
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
   }

   public abstract Conflict getConflict(BranchId mergeBranch, Set<ArtifactId> artIdSet);

}
