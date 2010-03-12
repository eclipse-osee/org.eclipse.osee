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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Theron Virgin
 */
public class ArtifactConflictBuilder extends ConflictBuilder {
   private final int sourceModType;
   private final int destModType;
   private final int artTypeId;

   public ArtifactConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionRecord toTransactionId, Branch sourceBranch, Branch destBranch, int sourceModType, int destModType, int artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, sourceBranch, destBranch);
      this.artTypeId = artTypeId;
      this.sourceModType = sourceModType;
      this.destModType = destModType;
   }

   @Override
   public Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws OseeCoreException {
      return new ArtifactConflict(sourceGamma, destGamma, artId, toTransactionId, mergeBranch, sourceBranch,
            destBranch, sourceModType, destModType, artTypeId);
   }

}
