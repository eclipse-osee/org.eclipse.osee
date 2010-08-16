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
package org.eclipse.osee.framework.skynet.core.event2.filter;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class BranchGuidEventFilter implements IEventFilter {

   private final IOseeBranch branchToken;

   /**
    * Provide branch of events that should be passed through. All other branches will be ignored.
    */
   public BranchGuidEventFilter(IOseeBranch branchToken) {
      this.branchToken = branchToken;
   }

   @Override
   public boolean isMatch(String branchGuid) {
      if (branchToken != null) {
         if (branchToken.getGuid().equals(branchGuid)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatch(IBasicGuidArtifact guidArt) {
      return branchToken.getGuid().equals(guidArt.getBranchGuid());
   }

   @Override
   public boolean isMatch(IBasicGuidRelation relArt) {
      return branchToken.getGuid().equals(relArt.getArtA().getBranchGuid()) ||
      //
      branchToken.getGuid().equals(relArt.getArtB().getBranchGuid());
   }

   @Override
   public String toString() {
      return branchToken.toString();
   }
}
