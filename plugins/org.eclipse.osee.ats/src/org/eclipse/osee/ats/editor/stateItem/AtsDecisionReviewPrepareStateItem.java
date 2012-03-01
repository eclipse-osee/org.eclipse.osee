/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.XDecisionOptions;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.workflow.IWorkPage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItem extends AtsStateItem implements ITransitionListener {

   public AtsDecisionReviewPrepareStateItem() {
      super(AtsDecisionReviewPrepareStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Add validation of Decision Review options prior to transitioning from Prepare to Decision.";
   }

   @Override
   public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IAtsUser> toAssignees) throws OseeCoreException {
      if (sma.isOfType(AtsArtifactTypes.DecisionReview) && fromState.getPageName().equals(
         DecisionReviewState.Prepare.getPageName()) && toState.getPageName().equals(
         DecisionReviewState.Decision.getPageName())) {
         XDecisionOptions decOptions = new XDecisionOptions(sma);
         decOptions.validateDecisionOptions(results);
      }
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IAtsUser> toAssignees, SkynetTransaction transaction) {
      // do nothing
   }

}
