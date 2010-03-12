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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * This utility class provides methods to filter out certain types of state machine artifacts based on criteria
 * 
 * @author Donald G. Dunne
 */
public class SMAUtil {

   public static Collection<StateMachineArtifact> getCompletedCancelled(Collection<StateMachineArtifact> smas) throws OseeCoreException {
      List<StateMachineArtifact> artifactsToReturn = new ArrayList<StateMachineArtifact>(smas.size());
      for (StateMachineArtifact sma : smas) {
         if (sma.isCancelledOrCompleted()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<StateMachineArtifact> getInWork(Collection<StateMachineArtifact> smas) throws OseeCoreException {
      List<StateMachineArtifact> artifactsToReturn = new ArrayList<StateMachineArtifact>(smas.size());
      for (StateMachineArtifact sma : smas) {
         if (!sma.isCancelledOrCompleted()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<StateMachineArtifact> filterOutState(Collection<StateMachineArtifact> smas, Collection<String> stateNames) throws OseeCoreException {
      List<StateMachineArtifact> artifactsToReturn = new ArrayList<StateMachineArtifact>(smas.size());
      for (StateMachineArtifact sma : smas) {
         if (!stateNames.contains(sma.getStateMgr().getCurrentStateName())) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<StateMachineArtifact> filterOutTypes(Collection<StateMachineArtifact> smas, Collection<Class<?>> classes) throws OseeCoreException {
      List<StateMachineArtifact> artifactsToReturn = new ArrayList<StateMachineArtifact>(smas.size());
      for (StateMachineArtifact sma : smas) {
         boolean found = false;
         for (Class<?> clazz : classes) {
            if (clazz.isInstance(sma)) {
               found = true;
            }
         }
         if (!found) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<StateMachineArtifact> getOpenAtDate(Date date, Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : artifacts) {
         Date createDate = sma.getWorldViewCreatedDate();
         Date completedCancelDate = null;
         if (sma.isCancelledOrCompleted()) {
            if (sma.isCancelled()) {
               completedCancelDate = sma.getWorldViewCancelledDate();
            } else {
               completedCancelDate = sma.getWorldViewCompletedDate();
            }
         }
         if (createDate.before(date) && (completedCancelDate == null || completedCancelDate.after(date))) {
            smas.add(sma);
         }
      }
      return smas;
   }

   public static Collection<StateMachineArtifact> getCompletedCancelledBetweenDate(Date startDate, Date endDate, Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : artifacts) {
         Date completedCancelDate = null;
         if (sma.isCancelledOrCompleted()) {
            if (sma.isCancelled()) {
               completedCancelDate = sma.getWorldViewCancelledDate();
            } else {
               completedCancelDate = sma.getWorldViewCompletedDate();
            }
         }
         if (completedCancelDate == null) {
            continue;
         }
         if (completedCancelDate.after(startDate) && completedCancelDate.before(endDate)) {
            smas.add(sma);
         }
      }
      return smas;
   }

   public static Double getHoursSpent(Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      Double hoursSpent = 0.0;
      for (StateMachineArtifact sma : artifacts) {
         hoursSpent += sma.getWorldViewHoursSpentTotal();
      }
      return hoursSpent;
   }

   public static Collection<StateMachineArtifact> getStateAtDate(Date date, Collection<String> states, Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : artifacts) {
         Date createDate = sma.getWorldViewCreatedDate();
         if (createDate.after(date)) {
            continue;
         }
         // Find state at date
         String currentState = sma.getStateMgr().getCurrentStateName();
         for (LogItem item : sma.getLog().getLogItems()) {
            if (item.getDate().before(date)) {
               currentState = item.getState();
            }
         }
         if (states.contains(currentState)) {
            smas.add(sma);
         }
      }
      return smas;
   }

   /**
    * Returns sma if change type, or parent team workflow's change type is in specified set
    * 
    * @param changeTypes
    * @param artifacts
    * @throws OseeCoreException
    */
   public static Collection<StateMachineArtifact> getChangeType(Collection<ChangeType> changeTypes, Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : artifacts) {
         TeamWorkFlowArtifact teamArt = sma.getParentTeamWorkflow();
         if (changeTypes.contains(teamArt.getChangeType())) {
            smas.add(sma);
         }
      }
      return smas;

   }

   /**
    * Returns sma if priority type, or parent team workflow's priority type is in specified set
    * 
    * @param priorityTypes
    * @param artifacts
    * @throws OseeCoreException
    */
   public static Collection<StateMachineArtifact> getPriorityType(Collection<PriorityType> priorityTypes, Collection<StateMachineArtifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : artifacts) {
         TeamWorkFlowArtifact teamArt = sma.getParentTeamWorkflow();
         if (priorityTypes.contains(teamArt.getPriority())) {
            smas.add(sma);
         }
      }
      return smas;

   }

   public static Collection<StateMachineArtifact> getTeamDefinitionWorkflows(Collection<? extends Artifact> artifacts, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      List<StateMachineArtifact> returnSmas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : getSMAs(artifacts)) {
         if (sma.getParentTeamWorkflow() == null) {
            continue;
         }
         if (teamDefs.contains(sma.getParentTeamWorkflow().getTeamDefinition())) {
            returnSmas.add(sma);
         }
      }
      return returnSmas;
   }

   public static Collection<StateMachineArtifact> getVersionWorkflows(Collection<? extends Artifact> artifacts, Collection<VersionArtifact> versionArts) throws OseeCoreException {
      List<StateMachineArtifact> returnSmas = new ArrayList<StateMachineArtifact>();
      for (StateMachineArtifact sma : getSMAs(artifacts)) {
         if (sma.getParentTeamWorkflow() == null) {
            continue;
         }
         if (sma.getWorldViewTargetedVersion() == null) {
            continue;
         }
         if (versionArts.contains(sma.getWorldViewTargetedVersion())) {
            returnSmas.add(sma);
         }
      }
      return returnSmas;
   }

   public static Collection<StateMachineArtifact> getSMAs(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      List<StateMachineArtifact> smas = new ArrayList<StateMachineArtifact>();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof StateMachineArtifact) {
            smas.add((StateMachineArtifact) artifact);
         }
      }
      return smas;
   }

}
