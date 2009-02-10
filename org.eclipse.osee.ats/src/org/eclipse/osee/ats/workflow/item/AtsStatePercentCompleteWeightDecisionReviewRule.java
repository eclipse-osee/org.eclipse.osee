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

import org.eclipse.osee.ats.artifact.DecisionReviewArtifact.DecisionReviewState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightDecisionReviewRule extends AtsStatePercentCompleteWeightRule {

   public static String ID = "atsStatePercentCompleteWeight.DecisionReview";

   public AtsStatePercentCompleteWeightDecisionReviewRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule for Decision Review.");
      addWorkDataKeyValue(DecisionReviewState.Prepare.name(), ".20");
      addWorkDataKeyValue(DecisionReviewState.Decision.name(), ".69");
      addWorkDataKeyValue(DecisionReviewState.Followup.name(), ".09");
      addWorkDataKeyValue(DecisionReviewState.Completed.name(), ".01");
   }

}
