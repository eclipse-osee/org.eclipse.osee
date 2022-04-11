/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.skynet.core.topic.event.filter;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;

/**
 * @author David W. Miller
 */
public class BranchIdTopicEventFilter implements ITopicEventFilter {

   private final BranchId branchToken;

   /**
    * Provide branch of events that should be passed through. All other branches will be ignored.
    */
   public BranchIdTopicEventFilter(BranchId branchToken) {
      this.branchToken = branchToken;
   }

   @Override
   public boolean isMatch(BranchId branch) {
      if (branchToken != null) {
         return branchToken.equals(branch);
      }
      return false;
   }

   @Override
   public String toString() {
      return String.valueOf(branchToken.getId());
   }

   @Override
   public boolean isMatchArtifacts(List<? extends EventTopicArtifactTransfer> transferArts) {
      if (transferArts.isEmpty()) {
         return true;
      }
      for (EventTopicArtifactTransfer transferArt : transferArts) {
         if (transferArt.getArtifactToken().isOnBranch(branchToken)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends EventTopicRelationTransfer> transferRelations) {
      if (transferRelations.isEmpty()) {
         return true;
      }
      for (EventTopicRelationTransfer rel : transferRelations) {
         if (rel.getArtAToken().isOnBranch(branchToken) || rel.getArtBToken().isOnBranch(branchToken)) {
            return true;
         }
      }
      return false;
   }
}
