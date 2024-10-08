/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author John R. Misinco
 */
public class ArtifactEventFilter implements IEventFilter {

   private final ArtifactToken artifact;

   public ArtifactEventFilter(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   @Override
   public boolean isMatch(BranchId branch) {
      return artifact.isOnBranch(branch);
   }

   @Override
   public boolean isMatchArtifacts(List<? extends DefaultBasicGuidArtifact> guidArts) {
      for (DefaultBasicGuidArtifact art : guidArts) {
         if (art.equals(artifact)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends IBasicGuidRelation> relations) {
      for (IBasicGuidRelation relation : relations) {
         if (relation.getArtA().equals(artifact) || relation.getArtB().equals(artifact)) {
            return true;
         }
      }
      return false;
   }
}