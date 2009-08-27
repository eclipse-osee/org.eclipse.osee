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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public abstract class XStateAssigneesDam extends XTextDam {

   protected final String attributeTypeName;
   private WeakReference<StateMachineArtifact> smaRef;

   public XStateAssigneesDam(StateMachineArtifact sma, String attributeName) {
      super(attributeName);
      this.smaRef = new WeakReference<StateMachineArtifact>(sma);
      this.attributeTypeName = attributeName;
   }

   public StateMachineArtifact getSma() throws OseeStateException {
      if (smaRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return smaRef.get();
   }

   public SMAState getState(String stateName, boolean create) {
      try {
         for (String stateXml : getSma().getAttributesToStringList(attributeTypeName)) {
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
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error parsing state data for " + getSma().getGuid(), ex);
         } catch (OseeStateException ex1) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
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
         XCurrentStateDam.logMetrics(getSma(), getSma().getPercentCompleteSMATotal() + "",
               AtsUtil.doubleToI18nString(getSma().getHoursSpentSMATotal()), stateName, UserManager.getUser(),
               new Date());
      }
   }

   public void setMetrics(String stateName, double hours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      SMAState currState = getState(stateName, false);
      currState.setHoursSpent(hours);
      currState.setPercentComplete(percentComplete);
      setState(currState);
      if (logMetrics) {
         XCurrentStateDam.logMetrics(getSma(), getSma().getPercentCompleteSMATotal() + "",
               AtsUtil.doubleToI18nString(getSma().getHoursSpentSMATotal()), stateName, UserManager.getUser(),
               new Date());
      }
   }

   public Set<SMAState> getStates() {
      Set<SMAState> states = new HashSet<SMAState>();
      try {
         for (String stateXml : getSma().getAttributesToStringList(attributeTypeName)) {
            SMAState state = new SMAState();
            state.setFromXml(stateXml);
            states.add(state);
         }
      } catch (Exception ex) {
         try {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error parsing state data for " + getSma().getGuid(), ex);
         } catch (OseeStateException ex1) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return states;
   }

   public void setState(SMAState state) throws OseeCoreException {
      // Update attribute if it already exists
      try {
         Collection<Attribute<String>> attrs = getSma().getAttributes(attributeTypeName);
         for (Attribute<String> attr : attrs) {
            SMAState storedState = new SMAState();
            storedState.setFromXml(attr.getValue());
            if (state.getName().equals(storedState.getName())) {
               attr.setValue(state.toXml());
               return;
            }
         }
         // Else, doesn't exist yet, create
         getSma().addAttribute(attributeTypeName, state.toXml());
         StateManager.updateAssigneeRelations(getSma().getSmaMgr());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error setting state data for " + getSma().getGuid(), ex);
      }
   }
}
