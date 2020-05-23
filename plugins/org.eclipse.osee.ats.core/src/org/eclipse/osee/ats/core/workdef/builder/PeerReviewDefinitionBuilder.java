/*********************************************************************
 * Copyright (c) 2019 Boeing
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
