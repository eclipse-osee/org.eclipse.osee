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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public abstract class XStateAssigneesDam extends XTextDam {

   public XStateAssigneesDam(StateMachineArtifact sma, IAttributeType attributeType) throws OseeCoreException {
      super(attributeType.getName(), true);
      setAttributeType(sma, attributeType);
   }

   @SuppressWarnings("unused")
   @Override
   public void onAttributeTypeSet() throws OseeCoreException {
      // do nothing
   }

   @Override
   public StateMachineArtifact getArtifact() throws OseeCoreException {
      return (StateMachineArtifact) super.getArtifact();
   }

   public SMAState getState(String stateName, boolean create) {
      try {
         for (String stateXml : getArtifact().getAttributesToStringList(getAttributeType())) {
            if (stateXml.startsWith(stateName + ";")) {
               SMAState state = new SMAState();
               state.setFromXml(stateXml);
               return state;
            }
         }
         if (create) {
            return new SMAState(stateName);
         }
      } catch (Exception ex) {
         try {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error parsing state data for " + getArtifact().getGuid(), ex);
         } catch (OseeCoreException ex1) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   public void updateMetrics(String stateName, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      SMAState currState = getState(stateName, false);
      currState.setHoursSpent(currState.getHoursSpent() + additionalHours);
      currState.setPercentComplete(percentComplete);
      setState(currState);
      if (logMetrics) {
         logMetrics(stateName);
      }
   }

   public void setMetrics(String stateName, double hours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      SMAState currState = getState(stateName, false);
      currState.setHoursSpent(hours);
      currState.setPercentComplete(percentComplete);
      setState(currState);
      if (logMetrics) {
         logMetrics(stateName);
      }
   }

   protected void logMetrics(String stateName) throws OseeCoreException {
      StateMachineArtifact sma = getArtifact();
      String hoursSpent = AtsUtil.doubleToI18nString(sma.getHoursSpentSMATotal());
      XCurrentStateDam.logMetrics(sma, sma.getPercentCompleteSMATotal() + "", hoursSpent, stateName,
         UserManager.getUser(), new Date());
   }

   public Set<SMAState> getStates() {
      Set<SMAState> states = new HashSet<SMAState>();
      try {
         for (String stateXml : getArtifact().getAttributesToStringList(getAttributeType())) {
            SMAState state = new SMAState();
            state.setFromXml(stateXml);
            states.add(state);
         }
      } catch (Exception ex) {
         try {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error parsing state data for " + getArtifact().getGuid(), ex);
         } catch (OseeCoreException ex1) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return states;
   }

   public void setState(SMAState state) throws OseeCoreException {
      // Update attribute if it already exists
      StateMachineArtifact sma = getArtifact();
      try {
         Collection<Attribute<String>> attrs = sma.getAttributes(getAttributeType());
         for (Attribute<String> attr : attrs) {
            SMAState storedState = new SMAState();
            storedState.setFromXml(attr.getValue());
            if (state.getName().equals(storedState.getName())) {
               attr.setValue(state.toXml());
               return;
            }
         }
         // Else, doesn't exist yet, create
         sma.addAttribute(getAttributeType(), state.toXml());
         StateManager.updateAssigneeRelations(sma);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error setting state data for " + sma.getGuid(), ex);
      }
   }
}
