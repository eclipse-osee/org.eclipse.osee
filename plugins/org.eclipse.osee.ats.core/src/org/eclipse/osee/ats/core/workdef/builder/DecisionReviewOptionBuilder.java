/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.builder;

import java.util.Collection;
import org.eclipse.osee.ats.api.review.DecisionReviewOption;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewOptionBuilder {

   private final DecisionReviewOption decRevOpt;
   private final DecisionReviewDefinitionBuilder decRevBldr;

   public DecisionReviewOptionBuilder(String name, DecisionReviewDefinitionBuilder decRevBldr) {
      this.decRevOpt = new DecisionReviewOption(name);
      decRevBldr.getReviewDefinition().getOptions().add(this.decRevOpt);
      this.decRevBldr = decRevBldr;
   }

   public void setName(String name) {
      decRevOpt.setName(name);
   }

   public void setUserIds(Collection<String> userIds) {
      decRevOpt.getUserIds().addAll(userIds);
   }

   public void setFollowup(boolean followup) {
      decRevOpt.setFollowupRequired(true);
   }

   public DecisionReviewOptionBuilder toCompleted() {
      decRevOpt.setFollowupRequired(false);
      return this;
   }

   public DecisionReviewOptionBuilder toFollowup() {
      decRevOpt.setFollowupRequired(true);
      return this;
   }

   public DecisionReviewOptionBuilder andAssignees(IAtsUser... users) {
      for (IAtsUser user : users) {
         decRevOpt.getUserIds().add(user.getUserId());
      }
      return this;
   }

   public DecisionReviewOptionBuilder andAssignees(UserToken... users) {
      for (UserToken user : users) {
         decRevOpt.getUserIds().add(user.getUserId());
      }
      return this;
   }

   public DecisionReviewDefinitionBuilder done() {
      return decRevBldr;
   }

}
