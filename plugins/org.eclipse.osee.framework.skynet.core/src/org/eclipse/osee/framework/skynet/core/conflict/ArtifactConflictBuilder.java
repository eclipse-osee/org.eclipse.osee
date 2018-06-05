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
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Theron Virgin
 */
public class ArtifactConflictBuilder extends ConflictBuilder {
   private final ModificationType sourceModType;
   private final ModificationType destModType;
   private final long artTypeId;

   public ArtifactConflictBuilder(GammaId sourceGamma, GammaId destGamma, ArtifactId artId, TransactionToken toTransactionId, IOseeBranch sourceBranch, IOseeBranch destBranch, ModificationType sourceModType, ModificationType destModType, long artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, sourceBranch, destBranch);
      this.artTypeId = artTypeId;
      this.sourceModType = sourceModType;
      this.destModType = destModType;
   }

   @Override
   public Conflict getConflict(BranchId mergeBranch, Set<ArtifactId> artIdSet) {
      return new ArtifactConflict(sourceGamma, destGamma, artId, toTransactionId, mergeBranch, sourceBranch, destBranch,
         sourceModType, destModType, artTypeId);
   }

}
