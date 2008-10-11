/*
 * Created on Sep 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
