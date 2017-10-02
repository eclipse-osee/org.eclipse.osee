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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Theron Virgin
 */
public abstract class ConflictBuilder {

   protected int sourceGamma;
   protected int destGamma;
   protected ArtifactId artId;
   protected TransactionToken toTransactionId;
   protected IOseeBranch sourceBranch;
   protected IOseeBranch destBranch;

   public ConflictBuilder(int sourceGamma, int destGamma, ArtifactId artId, TransactionToken toTransactionId, IOseeBranch sourceBranch, IOseeBranch destBranch) {
      super();
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
   }

   public abstract Conflict getConflict(BranchId mergeBranch, Set<ArtifactId> artIdSet) ;

}
