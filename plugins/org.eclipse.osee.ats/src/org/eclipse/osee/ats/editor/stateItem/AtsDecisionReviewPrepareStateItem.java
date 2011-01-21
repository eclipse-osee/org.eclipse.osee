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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewState;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItem extends AtsStateItem {

   public AtsDecisionReviewPrepareStateItem() {
      super(AtsDecisionReviewPrepareStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Add validation of Decision Review options prior to transitioning from Prepare to Decision.";
   }

   @Override
   public Result transitioning(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees) throws OseeCoreException {
      if (sma.isOfType(AtsArtifactTypes.DecisionReview) && fromState.getPageName().equals(
         DecisionReviewState.Prepare.getPageName()) && toState.getPageName().equals(
         DecisionReviewState.Decision.getPageName())) {
         XDecisionOptions decOptions = new XDecisionOptions(sma);
         return decOptions.validateDecisionOptions();
      }
      return Result.TrueResult;
   }

}
