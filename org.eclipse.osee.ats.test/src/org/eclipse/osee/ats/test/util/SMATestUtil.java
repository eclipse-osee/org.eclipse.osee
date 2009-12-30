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
               sma.getStateMgr().getCurrentStateName(), stateName);
         if (sma.isCancelledOrCompleted()) {
            TestCase.assertEquals("ats.CurrentState wrong " + sma.getHumanReadableId(),
                  sma.getStateMgr().getCurrentStateName() + ";;;",
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
