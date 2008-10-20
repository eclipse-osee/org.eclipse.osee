/*
 * Created on Sep 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.workflow;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class DemoAddDecisionReviewRule extends AtsAddDecisionReviewRule {

   public static String ID = "atsAddDecisionReview.test.addDecisionReview";

   public DemoAddDecisionReviewRule(String forState, ReviewBlockType reviewBlockType, StateEventType stateEventType) {
      super(ID + "." + forState + "." + reviewBlockType.name() + "." + stateEventType,
            ID + "." + forState + "." + reviewBlockType.name() + "." + stateEventType);
      setDescription("This is a rule created to test the Review rules.");
      setDecisionParameterValue(this, DecisionParameter.title, "Auto-created Decision Review from ruleId: " + getId());
      setDecisionParameterValue(this, DecisionParameter.reviewBlockingType, reviewBlockType.name());
      setDecisionParameterValue(this, DecisionParameter.forState, forState);
      setDecisionParameterValue(this, DecisionParameter.forEvent, stateEventType.name());
      try {
         setDecisionParameterValue(this, DecisionParameter.assignees, "<99999997>");
         setDecisionParameterValue(this, DecisionParameter.options, "Completed;Completed;");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }
}
