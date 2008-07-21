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

import java.sql.SQLException;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 */
public class ArtifactConflictBuilder extends ConflictBuilder {
   private int sourceModType;
   private int destModType;
   private int artTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param sourceBranch
    * @param destBranch
    */
   public ArtifactConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, Branch sourceBranch, Branch destBranch, int sourceModType, int destModType, int artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, sourceBranch, destBranch);
      this.artTypeId = artTypeId;
      this.sourceModType = sourceModType;
      this.destModType = destModType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder#getConflict(org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   @Override
   public Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws SQLException, OseeCoreException {
      return new ArtifactConflict(sourceGamma, destGamma, artId, toTransactionId, mergeBranch, sourceBranch,
            destBranch, sourceModType, destModType, artTypeId);
   }

}
