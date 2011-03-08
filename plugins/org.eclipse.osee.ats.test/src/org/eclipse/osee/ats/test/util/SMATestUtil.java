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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.internal.workflow.SMAState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class SMATestUtil {

   public static void validateSMA(AbstractWorkflowArtifact sma, String stateName, int totalPercent, double hoursSpent) throws Exception {
      validateSMAs(Arrays.asList(sma), stateName, totalPercent, hoursSpent);

   }

   public static void validateSMAs(Collection<? extends AbstractWorkflowArtifact> awas, String stateName, int totalPercent, double hoursSpent) throws Exception {
      for (AbstractWorkflowArtifact awa : awas) {
         Assert.assertEquals("Current State wrong for " + awa.getHumanReadableId(),
            awa.getStateMgr().getCurrentStateName(), stateName);
         if (awa.isCompletedOrCancelled()) {
            Assert.assertEquals("ats.CurrentState wrong " + awa.getHumanReadableId(),
               awa.getStateMgr().getCurrentStateName() + ";;;",
               awa.getSoleAttributeValue(AtsAttributeTypes.CurrentState));
         }
         Assert.assertEquals("Percent wrong for " + awa.getHumanReadableId(),
            PercentCompleteTotalColumn.getPercentCompleteTotal(awa), totalPercent);
         Assert.assertEquals("Hours Spent wrong for " + awa.getHumanReadableId(),
            HoursSpentTotalColumn.getHoursSpentTotal(awa), hoursSpent, 0.0);

         for (String stateValue : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
            SMAState smaState = new SMAState();
            smaState.setFromXml(stateValue);
            boolean isCompletedCancelledState = isCompletedCancelledState(awa, smaState.getName());
            if (isCompletedCancelledState) {
               Assert.assertTrue(
                  "completed/cancelled ats.State [" + stateValue + "] wrong " + awa.getHumanReadableId(),
                  stateValue.endsWith(";;;"));
            }
         }
      }
   }

   public static boolean isCompletedCancelledState(AbstractWorkflowArtifact aba, String stateName) throws OseeCoreException {
      return aba.getWorkDefinition().getStateByName(stateName).isCompletedOrCancelledPage();
   }
}
