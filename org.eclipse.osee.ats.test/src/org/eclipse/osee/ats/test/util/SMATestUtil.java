/*
 * Created on May 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.util;

import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;

/**
 * @author Donald G. Dunne
 */
public class SMATestUtil {

   public static void validateSMA(StateMachineArtifact sma, String stateName, int totalPercent, double hoursSpent) throws Exception {
      validateSMAs(Arrays.asList(sma), stateName, totalPercent, hoursSpent);

   }

   public static void validateSMAs(Collection<? extends StateMachineArtifact> smas, String stateName, int totalPercent, double hoursSpent) throws Exception {
      for (StateMachineArtifact sma : smas) {
         TestCase.assertEquals("Current State wronf for " + sma.getHumanReadableId(),
               sma.getSmaMgr().getStateMgr().getCurrentStateName(), stateName);
         if (sma.getSmaMgr().isCancelledOrCompleted()) {
            TestCase.assertEquals("ats.CurrentState wrong " + sma.getHumanReadableId(),
                  sma.getSmaMgr().getStateMgr().getCurrentStateName() + ";;;",
                  sma.getSoleAttributeValue(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName()));
         }
         TestCase.assertEquals("Percent wrong for " + sma.getHumanReadableId(), sma.getWorldViewPercentCompleteTotal(),
               totalPercent);
         TestCase.assertEquals("Hours Spent wrong for " + sma.getHumanReadableId(), sma.getWorldViewHoursSpentTotal(),
               hoursSpent);
         for (String stateValue : sma.getAttributesToStringList(ATSAttributes.STATE_ATTRIBUTE.getStoreName())) {
            if (stateValue.startsWith(DefaultTeamState.Completed.name())) {
               TestCase.assertEquals("ats.State wrong " + sma.getHumanReadableId(), stateValue, "Completed;;;");
            } else if (stateValue.startsWith(DefaultTeamState.Cancelled.name())) {
               TestCase.assertEquals("ats.State wrong " + sma.getHumanReadableId(), stateValue, "Cancelled;;;");
            }
         }
      }
   }
}
