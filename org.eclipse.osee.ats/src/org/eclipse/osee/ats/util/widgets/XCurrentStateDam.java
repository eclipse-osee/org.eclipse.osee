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
package org.eclipse.osee.ats.util.widgets;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class XCurrentStateDam extends XStateAssigneesDam {

   public XCurrentStateDam(StateMachineArtifact sma) {
      super(sma, ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName());
   }

   public SMAState getState() {
      Set<SMAState> states = getStates();
      if (states.size() != 1) throw new IllegalArgumentException(
            "Must be one current state.  Found " + states.size() + " for " + sma.getHumanReadableId());
      return states.iterator().next();
   }

   @Override
   public void setState(SMAState state) throws OseeCoreException, SQLException {
      sma.setSoleAttributeValue(attributeTypeName, state.toXml());
   }

   public void logMetrics() throws OseeCoreException, SQLException {
      if (sma instanceof TaskArtifact) logMetrics(sma, sma.getPercentCompleteSMATotal() + "",
            AtsLib.doubleToStrString(sma.getHoursSpentSMATotal()), SkynetAuthentication.getUser(), new Date());
   }

   public static void logMetrics(StateMachineArtifact sma, String percent, String hours, User user, Date date) throws SQLException, MultipleAttributesExist {
      LogItem logItem =
            new LogItem(LogType.Metrics, date, user, "", String.format("Percent %s Hours %s", percent, hours));
      sma.getLog().addLogItem(logItem);
   }

   /**
    * Set hours spent on the current state
    * 
    * @param hoursSpent The hoursSpent to set.
    * @throws Exception
    */
   public void setHoursSpent(double hoursSpent) throws OseeCoreException, SQLException {
      SMAState currState = getState();
      currState.setHoursSpent(hoursSpent);
      setState(currState);
      logMetrics();
   }

   /**
    * Add hours spent on the current state
    * 
    * @param hoursSpent The hoursSpent to set.
    * @throws Exception
    */
   public void addHoursSpent(double hoursSpent) throws OseeCoreException, SQLException {
      SMAState currState = getState();
      currState.setHoursSpent(hoursSpent + currState.getHoursSpent());
      setState(currState);
      logMetrics();
   }

   /**
    * Set percent complete on the current state
    * 
    * @param percentComplete The percentComplete to set.
    * @throws Exception
    */
   public void setPercentComplete(int percentComplete) throws OseeCoreException, SQLException {
      SMAState currState = getState();
      currState.setPercentComplete(percentComplete);
      setState(currState);
      logMetrics();
   }

}
