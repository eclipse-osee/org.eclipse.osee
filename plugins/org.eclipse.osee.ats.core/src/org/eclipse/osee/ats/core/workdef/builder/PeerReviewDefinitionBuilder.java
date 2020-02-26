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

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.model.PeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;

/**
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionBuilder {

   PeerReviewDefinition peerRev = new PeerReviewDefinition();

   public PeerReviewDefinitionBuilder(String name) {
      peerRev.setName(name);
   }

   public PeerReviewDefinitionBuilder andTitle(String title) {
      peerRev.setReviewTitle(title);
      return this;
   }

   public PeerReviewDefinitionBuilder andDescription(String description) {
      peerRev.setDescription(description);
      return this;
   }

   public PeerReviewDefinitionBuilder andRelatedToState(StateToken state) {
      peerRev.setRelatedToState(state.getName());
      return this;
   }

   public PeerReviewDefinitionBuilder andBlockingType(ReviewBlockType reviewBlockType) {
      peerRev.setBlockingType(reviewBlockType);
      return this;
   }

   public PeerReviewDefinitionBuilder andEvent(StateEventType stateEventType) {
      peerRev.setStateEventType(stateEventType);
      return this;
   }

   public PeerReviewDefinitionBuilder andAssignees(AtsUser... assignees) {
      for (AtsUser assignee : assignees) {
         peerRev.addAssignee(assignee);
      }
      return this;
   }

   public PeerReviewDefinition getReviewDefinition() {
      return peerRev;
   }

}
