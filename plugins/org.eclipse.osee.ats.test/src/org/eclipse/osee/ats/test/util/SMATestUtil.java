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
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
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
         Assert.assertEquals("Current State wrong for " + sma.getHumanReadableId(),
            sma.getStateMgr().getCurrentStateName(), stateName);
         if (sma.isCancelledOrCompleted()) {
            Assert.assertEquals("ats.CurrentState wrong " + sma.getHumanReadableId(),
               sma.getStateMgr().getCurrentStateName() + ";;;",
               sma.getSoleAttributeValue(AtsAttributeTypes.ATS_CURRENT_STATE));
         }
         Assert.assertEquals("Percent wrong for " + sma.getHumanReadableId(), sma.getWorldViewPercentCompleteTotal(),
            totalPercent);
         Assert.assertEquals("Hours Spent wrong for " + sma.getHumanReadableId(), sma.getWorldViewHoursSpentTotal(),
            hoursSpent);
         for (String stateValue : sma.getAttributesToStringList(AtsAttributeTypes.ATS_CURRENT_STATE)) {
            if (stateValue.startsWith(DefaultTeamState.Completed.name())) {
               Assert.assertEquals("ats.State wrong " + sma.getHumanReadableId(), stateValue, "Completed;;;");
            } else if (stateValue.startsWith(DefaultTeamState.Cancelled.name())) {
               Assert.assertEquals("ats.State wrong " + sma.getHumanReadableId(), stateValue, "Cancelled;;;");
            }
         }
      }
   }
}
