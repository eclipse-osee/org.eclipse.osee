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
import org.eclipse.osee.ats.core.review.DecisionReviewState;
import org.eclipse.osee.ats.core.review.XDecisionOptions;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
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
   public Result transitioning(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<IBasicUser> toAssignees) throws OseeCoreException {
      if (sma.isOfType(AtsArtifactTypes.DecisionReview) && fromState.getPageName().equals(
         DecisionReviewState.Prepare.getPageName()) && toState.getPageName().equals(
         DecisionReviewState.Decision.getPageName())) {
         XDecisionOptions decOptions = new XDecisionOptions(sma);
         return decOptions.validateDecisionOptions();
      }
      return Result.TrueResult;
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<IBasicUser> toAssignees, SkynetTransaction transaction) {
      // do nothing
   }

}
