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
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.DecisionReviewState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightDecisionReviewRule extends AtsStatePercentCompleteWeightRule {

   public final static String ID = "atsStatePercentCompleteWeight.DecisionReview";

   public AtsStatePercentCompleteWeightDecisionReviewRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule for Decision Review.");
      addWorkDataKeyValue(DecisionReviewState.Prepare.getPageName(), ".20");
      addWorkDataKeyValue(DecisionReviewState.Decision.getPageName(), ".69");
      addWorkDataKeyValue(DecisionReviewState.Followup.getPageName(), ".09");
      addWorkDataKeyValue(DecisionReviewState.Completed.getPageName(), ".01");
   }

}
