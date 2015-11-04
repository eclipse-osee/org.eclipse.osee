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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class BranchUuidEventFilter implements IEventFilter {

   private final IOseeBranch branchToken;

   /**
    * Provide branch of events that should be passed through. All other branches will be ignored.
    */
   public BranchUuidEventFilter(IOseeBranch branchToken) {
      this.branchToken = branchToken;
   }

   @Override
   public boolean isMatch(Long branchUuid) {
      if (branchToken != null) {
         if (branchUuid.equals(branchToken.getUuid())) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatchArtifacts(List<? extends IBasicGuidArtifact> guidArts) {
      if (guidArts.isEmpty()) {
         return true;
      }
      for (IBasicGuidArtifact guidArt : guidArts) {
         if (guidArt.getBranchId().equals(branchToken.getUuid())) {
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
         if (rel.getArtA().getBranchId().equals(branchToken.getUuid()) ||
         //
         rel.getArtB().getBranchId().equals(branchToken.getUuid())) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return branchToken.toString();
   }
}
