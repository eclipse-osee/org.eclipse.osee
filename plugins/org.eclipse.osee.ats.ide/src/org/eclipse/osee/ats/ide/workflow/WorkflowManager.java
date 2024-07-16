/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class WorkflowManager {

   public static boolean isAssigneeEditable(AbstractWorkflowArtifact awa) {
      return !awa.isCompletedOrCancelled() && //
         !awa.isReadOnly() && awa.isAccessControlWrite();
   }

   public static List<TeamWorkFlowArtifact> getAllTeamWorkflowArtifacts() {
      List<TeamWorkFlowArtifact> result = new ArrayList<>();
      for (ArtifactTypeToken artType : AtsArtifactTypes.TeamWorkflow.getAllDescendantTypes()) {
         List<TeamWorkFlowArtifact> teamArts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
            ArtifactQuery.getArtifactListFromType(artType, AtsApiService.get().getAtsBranch()));
         result.addAll(teamArts);
      }
      return result;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutState(Collection<AbstractWorkflowArtifact> awas, Collection<String> stateNames) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<>(awas.size());
      for (AbstractWorkflowArtifact awa : awas) {
         if (!stateNames.contains(awa.getStateMgr().getCurrentStateName())) {
            artifactsToReturn.add(awa);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutCompleted(Collection<AbstractWorkflowArtifact> awas) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<>(awas.size());
      for (AbstractWorkflowArtifact awa : awas) {
         if (!awa.isCompleted()) {
            artifactsToReturn.add(awa);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutCancelled(Collection<AbstractWorkflowArtifact> awas) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<>(awas.size());
      for (AbstractWorkflowArtifact awa : awas) {
         if (!awa.isCancelled()) {
            artifactsToReturn.add(awa);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<Artifact> filterState(String selectedState, Collection<? extends Artifact> awas) {
      List<Artifact> artifactsToReturn = new ArrayList<>(awas.size());
      if (!Strings.isValid(selectedState)) {
         artifactsToReturn.addAll(awas);
      } else {
         for (Artifact awa : awas) {
            if (awa instanceof IAtsWorkItem && ((IAtsWorkItem) awa).getStateMgr().getCurrentStateName().equals(
               selectedState)) {
               artifactsToReturn.add(awa);
            }
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> filterOutTypes(Collection<AbstractWorkflowArtifact> awas, Collection<Class<?>> classes) {
      List<AbstractWorkflowArtifact> artifactsToReturn = new ArrayList<>(awas.size());
      for (AbstractWorkflowArtifact awa : awas) {
         boolean found = false;
         for (Class<?> clazz : classes) {
            if (clazz.isInstance(awa)) {
               found = true;
            }
         }
         if (!found) {
            artifactsToReturn.add(awa);
         }
      }
      return artifactsToReturn;
   }

   public static Collection<AbstractWorkflowArtifact> getTeamDefinitionWorkflows(Collection<? extends Artifact> artifacts, Collection<IAtsTeamDefinition> teamDefs) {
      List<AbstractWorkflowArtifact> returnawas = new ArrayList<>();
      for (AbstractWorkflowArtifact awa : getAwas(artifacts)) {
         if (awa.getParentTeamWorkflow() == null) {
            continue;
         }
         if (teamDefs.contains(awa.getParentTeamWorkflow().getTeamDefinition())) {
            returnawas.add(awa);
         }
      }
      return returnawas;
   }

   public static Collection<AbstractWorkflowArtifact> getVersionWorkflows(Collection<? extends Artifact> artifacts, Collection<IAtsVersion> versionArts) {
      List<AbstractWorkflowArtifact> returnawas = new ArrayList<>();
      for (AbstractWorkflowArtifact awa : getAwas(artifacts)) {
         if (awa.getParentTeamWorkflow() == null) {
            continue;
         }
         if (!AtsApiService.get().getVersionService().hasTargetedVersion(awa)) {
            continue;
         }
         if (versionArts.contains(AtsApiService.get().getVersionService().getTargetedVersion(awa))) {
            returnawas.add(awa);
         }
      }
      return returnawas;
   }

   public static Collection<AbstractWorkflowArtifact> getAwas(Collection<? extends Artifact> artifacts) {
      return Collections.castMatching(AbstractWorkflowArtifact.class, artifacts);
   }

   public static IAtsStateManager getStateManager(Artifact artifact) {
      return cast(artifact).getStateMgr();
   }

   public static AbstractWorkflowArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         return (AbstractWorkflowArtifact) artifact;
      }
      return null;
   }

   public static StateXWidgetPage getCurrentAtsWorkPage(AbstractWorkflowArtifact awa) {
      for (StateXWidgetPage statePage : getStatePagesOrderedByOrdinal(awa)) {
         if (awa.getStateMgr().getCurrentStateName().equals(statePage.getName())) {
            return statePage;
         }
      }
      return null;
   }

   public static List<StateXWidgetPage> getStatePagesOrderedByOrdinal(IAtsWorkItem workItem) {
      List<StateXWidgetPage> statePages = new ArrayList<>();
      if (workItem != null) {
         WorkDefinition workDef = workItem.getWorkDefinition();
         ATSXWidgetOptionResolver optionResolver = ATSXWidgetOptionResolver.getInstance();
         for (StateDefinition stateDefinition : AtsApiService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
            workDef)) {
            try {
               StateXWidgetPage statePage = new StateXWidgetPage(workDef, stateDefinition, null, optionResolver,
                  (AbstractWorkflowArtifact) workItem);
               statePages.add(statePage);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      return statePages;

   }

}
