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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.workflow.SMAState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class SMATestUtil {

   public static void validateSMA(AbstractWorkflowArtifact sma, String stateName, int totalPercent, double hoursSpent) throws Exception {
      validateSMAs(Arrays.asList(sma), stateName, totalPercent, hoursSpent);

   }

   public static void validateSMAs(Collection<? extends AbstractWorkflowArtifact> smas, String stateName, int totalPercent, double hoursSpent) throws Exception {
      for (AbstractWorkflowArtifact sma : smas) {
         Assert.assertEquals("Current State wrong for " + sma.getHumanReadableId(),
            sma.getStateMgr().getCurrentStateName(), stateName);
         if (sma.isCompletedOrCancelled()) {
            Assert.assertEquals("ats.CurrentState wrong " + sma.getHumanReadableId(),
               sma.getStateMgr().getCurrentStateName() + ";;;",
               sma.getSoleAttributeValue(AtsAttributeTypes.CurrentState));
         }
         Assert.assertEquals("Percent wrong for " + sma.getHumanReadableId(), sma.getWorldViewPercentCompleteTotal(),
            totalPercent);
         Assert.assertEquals("Hours Spent wrong for " + sma.getHumanReadableId(), sma.getWorldViewHoursSpentTotal(),
            hoursSpent);

         for (String stateValue : sma.getAttributesToStringList(AtsAttributeTypes.State)) {
            SMAState smaState = new SMAState();
            smaState.setFromXml(stateValue);
            boolean isCompletedCancelledState = isCompletedCancelledState(sma, smaState.getName());
            if (isCompletedCancelledState) {
               Assert.assertTrue(
                  "completed/cancelled ats.State [" + stateValue + "] wrong " + sma.getHumanReadableId(),
                  stateValue.endsWith(";;;"));
            }
         }
      }
   }

   public static boolean isCompletedCancelledState(AbstractWorkflowArtifact aba, String stateName) throws OseeCoreException {
      return aba.getWorkDefinition().getStateByName(stateName).isCompletedOrCancelledPage();
   }
}
