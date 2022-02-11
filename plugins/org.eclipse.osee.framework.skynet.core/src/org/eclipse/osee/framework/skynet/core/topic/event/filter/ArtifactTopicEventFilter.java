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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;

/**
 * @author David W. Miller
 */
public class ArtifactTopicEventFilter implements ITopicEventFilter {

   private final ArtifactToken artifact;

   public ArtifactTopicEventFilter(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   @Override
   public boolean isMatch(BranchId branch) {
      return artifact.isOnBranch(branch);
   }

   @Override
   public boolean isMatchArtifacts(List<? extends EventTopicArtifactTransfer> transferArts) {
      for (EventTopicArtifactTransfer art : transferArts) {
         if (art.getArtifactToken().equals(artifact)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends EventTopicRelationTransfer> transferRelations) {
      for (EventTopicRelationTransfer relation : transferRelations) {
         if (relation.getArtAId().equals(artifact) || relation.getArtBId().equals(artifact)) {
            return true;
         }
      }
      return false;
   }
}