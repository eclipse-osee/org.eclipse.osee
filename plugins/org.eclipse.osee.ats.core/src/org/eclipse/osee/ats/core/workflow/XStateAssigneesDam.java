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
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public abstract class XStateAssigneesDam {

   protected final IAttributeType attributeType;
   protected final AbstractWorkflowArtifact awa;

   public XStateAssigneesDam(AbstractWorkflowArtifact awa, IAttributeType attributeType) {
      this.awa = awa;
      this.attributeType = attributeType;
   }

   public SMAState getState(IWorkPage state, boolean create) {
      try {
         for (String stateXml : awa.getAttributesToStringList(attributeType)) {
            if (stateXml.startsWith(state.getPageName() + ";")) {
               return new SMAState(state, stateXml);
            }
         }
         if (create) {
            return new SMAState(state);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error parsing state data for " + awa.getGuid(), ex);
      }
      return null;
   }

   public void updateMetrics(IWorkPage state, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      SMAState smaState = getState(state, false);
      smaState.setHoursSpent(smaState.getHoursSpent() + additionalHours);
      smaState.setPercentComplete(percentComplete);
      setState(smaState);
      if (logMetrics) {
         logMetrics(state, UserManager.getUser(), new Date());
      }
   }

   public void setMetrics(IWorkPage state, double hours, int percentComplete, boolean logMetrics, User user, Date date) throws OseeCoreException {
      SMAState currState = getState(state, false);
      currState.setHoursSpent(hours);
      currState.setPercentComplete(percentComplete);
      setState(currState);
      if (logMetrics) {
         logMetrics(state, user, date);
      }
   }

   protected void logMetrics(IWorkPage state, User user, Date date) throws OseeCoreException {
      String hoursSpent = AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentTotal(awa));
      XCurrentStateDam.logMetrics(awa, PercentCompleteTotalUtil.getPercentCompleteTotal(awa) + "", hoursSpent, state,
         user, date);
   }

   public Set<SMAState> getStates() {
      Set<SMAState> states = new HashSet<SMAState>();
      try {
         for (String stateXml : awa.getAttributesToStringList(attributeType)) {
            SMAState smaState = new SMAState(awa, stateXml);
            states.add(smaState);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error parsing state data for " + awa.getGuid(), ex);
      }
      return states;
   }

   @SuppressWarnings("unused")
   public void setState(SMAState state) throws OseeCoreException {
      // Update attribute if it already exists
      try {
         Collection<Attribute<String>> attrs = awa.getAttributes(attributeType);
         for (Attribute<String> attr : attrs) {
            SMAState storedState = new SMAState(awa, attr.getValue());
            if (state.getName().equals(storedState.getName())) {
               attr.setValue(state.toXml());
               return;
            }
         }
         // Else, doesn't exist yet, create
         awa.addAttribute(attributeType, state.toXml());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error setting state data for " + awa.getGuid(), ex);
      }
   }
}
