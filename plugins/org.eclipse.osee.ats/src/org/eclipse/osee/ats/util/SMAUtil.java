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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.column.CancelledDateColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CompletedDateColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * This utility class provides methods to filter out certain types of state machine artifacts based on criteria
 * 
 * @author Donald G. Dunne
 */
public class SMAUtil {

   public static Collection<AbstractWorkflowArtifact> getCompletedCancelled(Collection<AbstractWorkflowArtifact> smas) throws OseeCoreException {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
         if (sma.isCompletedOrCancelled()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> getInWork(Collection<AbstractWorkflowArtifact> smas) throws OseeCoreException {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
         if (!sma.isCompletedOrCancelled()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutState(Collection<AbstractWorkflowArtifact> smas, Collection<String> stateNames) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
         if (!stateNames.contains(sma.getStateMgr().getCurrentStateName())) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutCompleted(Collection<AbstractWorkflowArtifact> smas) throws OseeCoreException {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
         if (!sma.isCompleted()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutCancelled(Collection<AbstractWorkflowArtifact> smas) throws OseeCoreException {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
         if (!sma.isCancelled()) {
            artifactsToReturn.add(sma);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutTypes(Collection<AbstractWorkflowArtifact> smas, Collection<Class<?>> classes) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<AbstractWorkflowArtifact>(smas.size());
      for (AbstractWorkflowArtifact sma : smas) {
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

   public static Collection<AbstractWorkflowArtifact> getOpenAtDate(Date date, Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> smas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : artifacts) {
         Date createDate = CreatedDateColumn.getDate(sma);
         Date completedCancelDate = null;
         if (sma.isCompletedOrCancelled()) {
            if (sma.isCancelled()) {
               completedCancelDate = CancelledDateColumn.getDate(sma);
            } else {
               completedCancelDate = CompletedDateColumn.getDate(sma);
            }
         }
         if (createDate.before(date) && (completedCancelDate == null || completedCancelDate.after(date))) {
            smas.add(sma);
         }
      }
      return smas;
   }

   public static Collection<AbstractWorkflowArtifact> getCompletedCancelledBetweenDate(Date startDate, Date endDate, Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> smas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : artifacts) {
         Date completedCancelDate = null;
         if (sma.isCompletedOrCancelled()) {
            if (sma.isCancelled()) {
               completedCancelDate = CancelledDateColumn.getDate(sma);
            } else {
               completedCancelDate = CompletedDateColumn.getDate(sma);
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

   public static Double getHoursSpent(Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      Double hoursSpent = 0.0;
      for (AbstractWorkflowArtifact sma : artifacts) {
         hoursSpent += sma.getWorldViewHoursSpentTotal();
      }
      return hoursSpent;
   }

   public static Collection<AbstractWorkflowArtifact> getStateAtDate(Date date, Collection<String> states, Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> smas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : artifacts) {
         Date createDate = CreatedDateColumn.getDate(sma);
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
    */
   public static Collection<AbstractWorkflowArtifact> getChangeType(Collection<ChangeType> changeTypes, Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> smas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : artifacts) {
         TeamWorkFlowArtifact teamArt = sma.getParentTeamWorkflow();
         if (changeTypes.contains(ChangeTypeColumn.getChangeType(teamArt))) {
            smas.add(sma);
         }
      }
      return smas;

   }

   /**
    * Returns sma if priority type, or parent team workflow's priority type is in specified set
    */
   public static Collection<AbstractWorkflowArtifact> getPriorityType(Collection<String> priorityTypes, Collection<AbstractWorkflowArtifact> artifacts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> smas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : artifacts) {
         TeamWorkFlowArtifact teamArt = sma.getParentTeamWorkflow();
         if (priorityTypes.contains(PriorityColumn.getPriorityStr(teamArt))) {
            smas.add(sma);
         }
      }
      return smas;

   }

   public static Collection<AbstractWorkflowArtifact> getTeamDefinitionWorkflows(Collection<? extends Artifact> artifacts, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      List<AbstractWorkflowArtifact> returnSmas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : getSMAs(artifacts)) {
         if (sma.getParentTeamWorkflow() == null) {
            continue;
         }
         if (teamDefs.contains(sma.getParentTeamWorkflow().getTeamDefinition())) {
            returnSmas.add(sma);
         }
      }
      return returnSmas;
   }

   public static Collection<AbstractWorkflowArtifact> getVersionWorkflows(Collection<? extends Artifact> artifacts, Collection<VersionArtifact> versionArts) throws OseeCoreException {
      List<AbstractWorkflowArtifact> returnSmas = new ArrayList<AbstractWorkflowArtifact>();
      for (AbstractWorkflowArtifact sma : getSMAs(artifacts)) {
         if (sma.getParentTeamWorkflow() == null) {
            continue;
         }
         if (sma.getTargetedVersion() == null) {
            continue;
         }
         if (versionArts.contains(sma.getTargetedVersion())) {
            returnSmas.add(sma);
         }
      }
      return returnSmas;
   }

   public static Collection<AbstractWorkflowArtifact> getSMAs(Collection<? extends Artifact> artifacts) {
      return Collections.castMatching(AbstractWorkflowArtifact.class, artifacts);
   }

}
