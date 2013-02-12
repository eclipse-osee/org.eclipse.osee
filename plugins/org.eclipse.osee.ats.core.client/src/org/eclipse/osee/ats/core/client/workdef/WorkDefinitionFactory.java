/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.util.WorkItemUtil;
import org.eclipse.osee.ats.core.client.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionFactory {

   // Cache the WorkDefinition used for each AbstractWorkflowId so don't have to recompute each time
   final Map<String, WorkDefinitionMatch> awaArtIdToWorkDefinition = new HashMap<String, WorkDefinitionMatch>();
   // Cache the WorkDefinition object for each WorkDefinition id so don't have to reload
   // This grows as WorkDefinitions are requested/loaded
   final Map<String, WorkDefinitionMatch> workDefIdToWorkDefintion = new HashMap<String, WorkDefinitionMatch>();
   public static final String TaskWorkflowDefinitionId = "WorkDef_Task_Default";
   public static final String GoalWorkflowDefinitionId = "WorkDef_Goal";
   public static final String PeerToPeerDefaultWorkflowDefinitionId = "WorkDef_Review_PeerToPeer";
   public static final String DecisionWorkflowDefinitionId = "WorkDef_Review_Decision";
   public static final String TeamWorkflowDefaultDefinitionId = "WorkDef_Team_Default";
   private final IAtsTeamDefinitionService teamDefService;
   private final IAtsWorkItemService workItemService;
   private final IAtsWorkDefinitionService workDefinitionService;
   private final List<ITeamWorkflowProvider> teamWorkflowProviders;

   public WorkDefinitionFactory(IAtsTeamDefinitionService teamDefService, IAtsWorkItemService workItemService, IAtsWorkDefinitionService workDefinitionService, List<ITeamWorkflowProvider> teamWorkflowProviders) {
      this.teamDefService = teamDefService;
      this.workItemService = workItemService;
      this.workDefinitionService = workDefinitionService;
      this.teamWorkflowProviders = teamWorkflowProviders;
   }

   public void clearCaches() {
      awaArtIdToWorkDefinition.clear();
      workDefIdToWorkDefintion.clear();
   }

   public void addWorkDefinition(IAtsWorkDefinition workDef) {
      WorkDefinitionMatch match =
         new WorkDefinitionMatch(workDef.getId(), "programatically added via WorkDefinitionFactory.addWorkDefinition");
      match.setWorkDefinition(workDef);
      workDefIdToWorkDefintion.put(workDef.getName(), match);
   }

   public void removeWorkDefinition(IAtsWorkDefinition workDef) {
      workDefIdToWorkDefintion.remove(workDef.getName());
   }

   public WorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      if (!awaArtIdToWorkDefinition.containsKey(workItem.getHumanReadableId())) {
         WorkDefinitionMatch match = getWorkDefinitionNew(workItem);
         awaArtIdToWorkDefinition.put(workItem.getHumanReadableId(), match);
      }
      return awaArtIdToWorkDefinition.get(workItem.getHumanReadableId());
   }

   public Collection<IAtsWorkDefinition> getLoadedWorkDefinitions() {
      List<IAtsWorkDefinition> workDefs = new ArrayList<IAtsWorkDefinition>();
      for (WorkDefinitionMatch match : workDefIdToWorkDefintion.values()) {
         if (match.getWorkDefinition() != null) {
            workDefs.add(match.getWorkDefinition());
         }
      }
      return workDefs;
   }

   public WorkDefinitionMatch getWorkDefinition(String id) {
      if (!workDefIdToWorkDefintion.containsKey(id)) {
         WorkDefinitionMatch match = new WorkDefinitionMatch();
         // Try to get from new DSL provider if configured to use it
         if (!match.isMatched()) {
            try {
               XResultData resultData = new XResultData(false);
               if (workDefinitionService == null) {
                  throw new IllegalStateException("ATS Work Definition Service is not found.");
               }
               IAtsWorkDefinition workDef = workDefinitionService.getWorkDef(id, resultData);
               if (workDef != null) {
                  match.setWorkDefinition(workDef);
                  if (!resultData.isEmpty()) {
                     match.addTrace((String.format("from DSL provider loaded id [%s] [%s]", id, resultData.toString())));
                  } else {
                     match.addTrace((String.format("from DSL provider loaded id [%s]", id)));
                  }
               }
            } catch (Exception ex) {
               return new WorkDefinitionMatch(null, ex.getLocalizedMessage());
            }
         }
         if (match.isMatched()) {
            OseeLog.logf(Activator.class, Level.INFO, "Loaded Work Definition [%s]", match);
            workDefIdToWorkDefintion.put(id, match);
         } else {
            OseeLog.logf(Activator.class, Level.INFO, "Unable to load Work Definition [%s]", id);
         }
      }
      WorkDefinitionMatch match = workDefIdToWorkDefintion.get(id);
      if (match == null) {
         match = new WorkDefinitionMatch();
      }
      return match;
   }

   WorkDefinitionMatch getWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getWorkflowDefinition();
      if (Strings.isValid(workFlowDefId)) {
         WorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", teamDef, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   WorkDefinitionMatch getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getRelatedTaskWorkDefinition();
      if (Strings.isValid(workFlowDefId)) {
         WorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", teamDef, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   WorkDefinitionMatch getWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = null;
      Collection<Object> attributeValues =
         workItemService.getAttributeValues(workItem, AtsAttributeTypes.WorkflowDefinition);
      if (!attributeValues.isEmpty()) {
         workFlowDefId = (String) attributeValues.iterator().next();
      }
      if (Strings.isValid(workFlowDefId)) {
         WorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", workItem, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   WorkDefinitionMatch getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = null;
      Collection<Object> attributeValues =
         workItemService.getAttributeValues(workItem, AtsAttributeTypes.RelatedTaskWorkDefinition);
      if (!attributeValues.isEmpty()) {
         workFlowDefId = (String) attributeValues.iterator().next();
      }
      if (Strings.isValid(workFlowDefId)) {
         WorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", workItem, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   /**
    * Look at team def's attribute for Work Definition setting, otherwise, walk up team tree for setting
    */
   protected WorkDefinitionMatch getWorkDefinitionFromTeamDefinitionAttributeInherited(IAtsTeamDefinition teamDef) throws OseeCoreException {
      WorkDefinitionMatch match = getWorkDefinitionFromArtifactsAttributeValue(teamDef);
      if (match.isMatched()) {
         return match;
      }
      IAtsTeamDefinition parentArt = teamDef.getParentTeamDef();
      if (parentArt != null) {
         return getWorkDefinitionFromTeamDefinitionAttributeInherited(parentArt);
      }
      return new WorkDefinitionMatch();
   }

   public WorkDefinitionMatch getWorkDefinitionForTask(IAtsTask task) throws OseeCoreException {
      IAtsTeamWorkflow teamWf = WorkItemUtil.getParentTeamWorkflow(task);
      return getWorkDefinitionForTask(teamWf, task);
   }

   /**
    * Return the WorkDefinition that would be assigned to a new Task. This is not necessarily the actual WorkDefinition
    * used because it can be overridden once the Task artifact is created.
    */
   public WorkDefinitionMatch getWorkDefinitionForTaskNotYetCreated(TeamWorkFlowArtifact teamWf) throws OseeCoreException {
      return getWorkDefinitionForTask(teamWf, null);
   }

   /**
    * @param task - if null, returned WorkDefinition will be proposed; else returned will be actual
    */
   WorkDefinitionMatch getWorkDefinitionForTask(IAtsTeamWorkflow teamWf, IAtsTask task) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      for (ITeamWorkflowProvider provider : teamWorkflowProviders) {
         String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
         if (Strings.isValid(workFlowDefId)) {
            match = getWorkDefinition(workFlowDefId);
            match.addTrace((String.format("from provider [%s] for id [%s]", provider.getClass().getSimpleName(),
               workFlowDefId)));
            break;
         }
      }
      if (!match.isMatched() && task != null) {
         // If task specifies it's own workflow id, use it
         match = getWorkDefinitionFromArtifactsAttributeValue(task);
      }
      if (!match.isMatched()) {
         // Else If parent SMA has a related task definition workflow id specified, use it
         WorkDefinitionMatch match2 = getTaskWorkDefinitionFromArtifactsAttributeValue(teamWf);
         if (match2.isMatched()) {
            match2.addTrace(String.format("from task parent SMA [%s]", teamWf));
            match = match2;
         }
      }
      if (!match.isMatched()) {
         // Else If parent TeamWorkflow's IAtsTeamDefinition has a related task definition workflow id, use it
         match = getTaskWorkDefinitionFromArtifactsAttributeValue(teamDefService.getTeamDefinition(teamWf));
      }
      if (!match.isMatched()) {
         match = getWorkDefinition(TaskWorkflowDefinitionId);
         match.addTrace("WorkDefinitionFactory - Default Task");
      }
      return match;
   }

   WorkDefinitionMatch getWorkDefinitionNew(IAtsWorkItem workItem) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      if (workItem instanceof IAtsTask) {
         match = getWorkDefinitionForTask((IAtsTask) workItem);
      }
      if (!match.isMatched()) {
         // Check extensions for definition handling
         for (ITeamWorkflowProvider provider : teamWorkflowProviders) {
            String workFlowDefId = provider.getWorkflowDefinitionId(workItem);
            if (Strings.isValid(workFlowDefId)) {
               match = getWorkDefinition(workFlowDefId);
            }
         }
         if (!match.isMatched()) {
            // If this artifact specifies it's own workflow definition, use it
            match = getWorkDefinitionFromArtifactsAttributeValue(workItem);
            if (!match.isMatched()) {
               // Otherwise, use workflow defined by attribute of WorkflowDefinition
               // Note: This is new.  Old TeamDefs got workflow off relation
               if (workItem instanceof IAtsTeamWorkflow) {
                  IAtsTeamDefinition teamDef = teamDefService.getTeamDefinition(workItem);
                  match = getWorkDefinitionFromTeamDefinitionAttributeInherited(teamDef);
               } else if (workItem instanceof IAtsGoal) {
                  match = getWorkDefinition(GoalWorkflowDefinitionId);
                  match.addTrace("WorkDefinitionFactory - GoalWorkflowDefinitionId");
               } else if (workItem instanceof IAtsPeerToPeerReview) {
                  match = getWorkDefinition(PeerToPeerDefaultWorkflowDefinitionId);
                  match.addTrace("WorkDefinitionFactory - PeerToPeerWorkflowDefinitionId");
               } else if (workItem instanceof IAtsDecisionReview) {
                  match = getWorkDefinition(DecisionWorkflowDefinitionId);
                  match.addTrace("WorkDefinitionFactory - DecisionWorkflowDefinitionId");
               }
            }
         }
      }
      return match;
   }
   public Set<String> errorDisplayed = new HashSet<String>();

   public Set<IAtsWorkDefinition> loadAllDefinitions() throws OseeCoreException {
      ElapsedTime time = new ElapsedTime("  - Load All Work Definitions");

      Set<IAtsWorkDefinition> workDefs = new HashSet<IAtsWorkDefinition>();
      // This load is faster than loading each by artifact type
      for (Artifact art : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.WorkDefinition,
         AtsUtilCore.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED)) {
         try {
            XResultData resultData = new XResultData(false);
            IAtsWorkDefinition workDef = workDefinitionService.getWorkDef(art.getName(), resultData);
            if (!resultData.isEmpty()) {
               OseeLog.log(Activator.class, Level.SEVERE, resultData.toString());
            }
            if (workDef == null) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Null WorkDef loaded for Artifact [%s]",
                  art.toStringWithId());
            } else {
               workDefs.add(workDef);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Error loading WorkDefinition from artifact " + art.toStringWithId(), ex);
         }
      }
      time.end();
      return workDefs;
   }

   public boolean isTaskOverridingItsWorkDefinition(TaskArtifact taskArt) throws MultipleAttributesExist, OseeCoreException {
      return taskArt.getSoleAttributeValueAsString(AtsAttributeTypes.WorkflowDefinition, null) != null;
   }

   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      return workDefinitionService.copyWorkDefinition(newName, workDef, resultData,
         AtsWorkDefinitionStore.getInstance().getAttributeResolver(),
         AtsWorkDefinitionStore.getInstance().getUserResolver());
   }

   /**
    * @return WorkDefinitionMatch for Peer Review either from attribute value or default
    */
   protected WorkDefinitionMatch getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) throws OseeCoreException {
      Conditions.notNull(review, WorkDefinitionFactory.class.getSimpleName());
      WorkDefinitionMatch match = getWorkDefinitionFromArtifactsAttributeValue(review);
      if (!match.isMatched()) {
         match = getDefaultPeerToPeerWorkflowDefinitionMatch();
      }
      return match;
   }

   public WorkDefinitionMatch getDefaultPeerToPeerWorkflowDefinitionMatch() {
      WorkDefinitionMatch match = getWorkDefinition(PeerToPeerDefaultWorkflowDefinitionId);
      match.addTrace("WorkDefinitionFactory - Default PeerToPeer");
      return match;
   }

   /**
    * @return WorkDefinitionMatch for peer review off created teamWf. Will use configured value off team definitions
    * with recurse or return default review work definition
    */
   public WorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      Conditions.notNull(teamWf, WorkDefinitionFactory.class.getSimpleName());
      IAtsTeamDefinition teamDefinition = teamDefService.getTeamDefinition(teamWf);
      WorkDefinitionMatch match = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(teamDefinition);
      if (!match.isMatched()) {
         match = getDefaultPeerToPeerWorkflowDefinitionMatch();
      }
      return match;
   }

   /**
    * @return WorkDefinitionMatch of peer review from team definition related to actionableItem or return default review
    * work definition
    */
   public WorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) throws OseeCoreException {
      Conditions.notNull(actionableItem, WorkDefinitionFactory.class.getSimpleName());
      WorkDefinitionMatch match =
         getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(actionableItem.getTeamDefinitionInherited());
      if (!match.isMatched()) {
         match = getDefaultPeerToPeerWorkflowDefinitionMatch();
      }
      return match;
   }

   /**
    * @return WorkDefinitionMatch of teamDefinition configured with RelatedPeerWorkflowDefinition attribute with recurse
    * up to top teamDefinition or will return no match
    */
   protected WorkDefinitionMatch getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(IAtsTeamDefinition teamDefinition) throws OseeCoreException {
      Conditions.notNull(teamDefinition, WorkDefinitionFactory.class.getSimpleName());
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      String workDefId = teamDefinition.getRelatedPeerWorkDefinition();
      if (!Strings.isValid(workDefId)) {
         IAtsTeamDefinition parentTeamDef = teamDefinition.getParentTeamDef();
         if (parentTeamDef != null) {
            match = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(parentTeamDef);
         }
      } else {
         match = getWorkDefinition(workDefId);
         match.addTrace("PeerToPeer from Team Definition");
      }
      return match;
   }

}
