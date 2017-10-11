/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewRuleDefinition;
import org.eclipse.osee.ats.api.workdef.RunRuleResults;

/**
 * @author Mark Joy
 */
public class PeerReviewRuleDefinition extends RuleDefinition implements IAtsPeerReviewRuleDefinition {
   private String relatedState;
   private String location = "";
   private ReviewBlockType blockingType;

   @Override
   public String getRelatedToState() {
      return relatedState;
   }

   public void setRelatedToState(String relatedState) {
      this.relatedState = relatedState;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   @Override
   public ReviewBlockType getBlockingType() {
      return blockingType;
   }

   public void setBlockingType(ReviewBlockType blockingType) {
      this.blockingType = blockingType;
   }

   @Override
   public void execute(IAtsWorkItem workItem, AtsApi atsServices, IAtsChangeSet changes, RunRuleResults ruleResults) {
      //
   }

}
