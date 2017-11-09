/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class BranchIdEventFilter implements IEventFilter {

   private final BranchId branchToken;

   /**
    * Provide branch of events that should be passed through. All other branches will be ignored.
    */
   public BranchIdEventFilter(BranchId branchToken) {
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
   public boolean isMatchArtifacts(List<? extends DefaultBasicGuidArtifact> guidArts) {
      if (guidArts.isEmpty()) {
         return true;
      }
      for (DefaultBasicGuidArtifact guidArt : guidArts) {
         if (guidArt.isOnBranch(branchToken)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends IBasicGuidRelation> relations) {
      if (relations.isEmpty()) {
         return true;
      }
      for (IBasicGuidRelation rel : relations) {
         if (rel.getArtA().isOnBranch(branchToken) || rel.getArtB().isOnBranch(branchToken)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return String.valueOf(branchToken.getId());
   }
}
