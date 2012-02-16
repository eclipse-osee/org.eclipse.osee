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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.HoursSpentUtil;
import org.eclipse.osee.ats.core.client.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.client.workflow.SMAState;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.framework.core.util.WorkPageType;
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
            PercentCompleteTotalUtil.getPercentCompleteTotal(awa), totalPercent);
         Assert.assertEquals("Hours Spent wrong for " + awa.getHumanReadableId(),
            HoursSpentUtil.getHoursSpentTotal(awa), hoursSpent, 0.0);

         for (String xml : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
            SMAState smaState = new SMAState(awa, xml);
            String pageName = smaState.getName();
            StateDefinition stateDef = awa.getStateDefinitionByName(pageName);
            if (stateDef != null) {
               smaState.setWorkPageType(stateDef.getWorkPageType());
            } else {
               smaState.setWorkPageType(WorkPageType.Working);
            }
            boolean isCompletedCancelledState = isCompletedCancelledState(awa, smaState.getName());
            if (isCompletedCancelledState) {
               Assert.assertTrue("completed/cancelled ats.State [" + xml + "] wrong " + awa.getHumanReadableId(),
                  xml.endsWith(";;;"));
            }
         }
      }
   }

   public static boolean isCompletedCancelledState(AbstractWorkflowArtifact aba, String stateName) {
      return aba.getWorkDefinition().getStateByName(stateName).isCompletedOrCancelledPage();
   }
}
