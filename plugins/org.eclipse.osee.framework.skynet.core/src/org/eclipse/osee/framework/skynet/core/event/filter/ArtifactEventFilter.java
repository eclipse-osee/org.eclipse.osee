/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author John R. Misinco
 */
public class ArtifactEventFilter implements IEventFilter {

   private final String filterArtifactGuid;
   private final BranchId filterBranch;

   public ArtifactEventFilter(ArtifactToken artifact) {
      filterArtifactGuid = artifact.getGuid();
      filterBranch = artifact.getBranch();
   }

   @Override
   public boolean isMatch(BranchId branch) {
      return branch.equals(filterBranch);
   }

   @Override
   public boolean isMatchArtifacts(List<? extends IBasicGuidArtifact> guidArts) {
      for (IBasicGuidArtifact art : guidArts) {
         if (art.getGuid().equals(filterArtifactGuid) && art.isOnBranch(filterBranch)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends IBasicGuidRelation> relations) {
      for (IBasicGuidRelation relation : relations) {
         if (isMatchArtifacts(Arrays.asList(relation.getArtA(), relation.getArtB()))) {
            return true;
         }
      }
      return false;
   }
}