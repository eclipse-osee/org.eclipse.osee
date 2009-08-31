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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 */
public abstract class ConflictBuilder {

   protected int sourceGamma;
   protected int destGamma;
   protected int artId;
   protected TransactionId toTransactionId;
   protected Branch sourceBranch;
   protected Branch destBranch;

   public ConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, Branch sourceBranch, Branch destBranch) {
      super();
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
   }

   public abstract Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws OseeCoreException;

}
