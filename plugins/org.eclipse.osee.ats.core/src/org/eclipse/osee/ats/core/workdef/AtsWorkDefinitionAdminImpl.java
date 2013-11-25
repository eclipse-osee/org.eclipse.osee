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
package org.eclipse.osee.ats.core.workdef;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProviders;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionAdminImpl implements IAtsWorkDefinitionAdmin {

   private final CacheProvider<AtsWorkDefinitionCache> cacheProvider;
   private final IAtsWorkDefinitionService workDefinitionService;
   private final ITeamWorkflowProviders teamWorkflowProviders;
   private final IAtsWorkItemService workItemService;
   private final IAttributeResolver attributeResolver;

   public AtsWorkDefinitionAdminImpl(CacheProvider<AtsWorkDefinitionCache> workDefCacheProvider, IAtsWorkItemService workItemService, IAtsWorkDefinitionService workDefinitionService, ITeamWorkflowProviders teamWorkflowProviders, IAttributeResolver attributeResolver) {
      this.cacheProvider = workDefCacheProvider;
      this.workItemService = workItemService;
      this.workDefinitionService = workDefinitionService;
      this.teamWorkflowProviders = teamWorkflowProviders;
      this.attributeResolver = attributeResolver;
   }

   private AtsWorkDefinitionCache getCache() throws OseeCoreException {
      return cacheProvider.get();
   }

   @Override
   public void clearCaches() {
      cacheProvider.invalidate();
   }

   @Override
   public void addWorkDefinition(IAtsWorkDefinition workDef) throws OseeCoreException {
      WorkDefinitionMatch match =
         new WorkDefinitionMatch(workDef.getId(), "programatically added via WorkDefinitionFactory.addWorkDefinition");
      match.setWorkDefinition(workDef);
      getCache().cache(workDef, match);
   }

   @Override
   public void removeWorkDefinition(IAtsWorkDefinition workDef) throws OseeCoreException {
      getCache().invalidate(workDef);
   }

   @Override
   public IWorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      AtsWorkDefinitionCache cache = getCache();
      IWorkDefinitionMatch toReturn = cache.getWorkDefinition(workItem);
      if (toReturn == null) {
         toReturn = getWorkDefinitionNew(workItem);
         getCache().cache(workItem, toReturn);
      }
      return toReturn;
   }

   @Override
   public IWorkDefinitionMatch getWorkDefinition(String id) throws OseeCoreException {
      IWorkDefinitionMatch toReturn = getCache().getWorkDefinition(id);
      if (toReturn == null) {
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
            getCache().cache(id, match);
            toReturn = match;
         } else {
            OseeLog.logf(Activator.class, Level.INFO, "Unable to load Work Definition [%s]", id);
         }
      }
      if (toReturn == null) {
         toReturn = new WorkDefinitionMatch();
      }
      return toReturn;
   }

   private IWorkDefinitionMatch getWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getWorkflowDefinition();
      if (Strings.isValid(workFlowDefId)) {
         IWorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", teamDef, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   private IWorkDefinitionMatch getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getRelatedTaskWorkDefinition();
      if (Strings.isValid(workFlowDefId)) {
         IWorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", teamDef, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   private IWorkDefinitionMatch getWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = null;
      Collection<Object> attributeValues =
         workItemService.getAttributeValues(workItem, AtsAttributeTypes.WorkflowDefinition);
      if (!attributeValues.isEmpty()) {
         workFlowDefId = (String) attributeValues.iterator().next();
      }
      if (Strings.isValid(workFlowDefId)) {
         IWorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s]", workItem, workFlowDefId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   private IWorkDefinitionMatch getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = null;
      Collection<Object> attributeValues =
         workItemService.getAttributeValues(workItem, AtsAttributeTypes.RelatedTaskWorkDefinition);
      if (!attributeValues.isEmpty()) {
         workFlowDefId = (String) attributeValues.iterator().next();
      }
      if (Strings.isValid(workFlowDefId)) {
         IWorkDefinitionMatch match = getWorkDefinition(workFlowDefId);
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
   private IWorkDefinitionMatch getWorkDefinitionFromTeamDefinitionAttributeInherited(IAtsTeamDefinition teamDef) throws OseeCoreException {
      IWorkDefinitionMatch match = getWorkDefinitionFromArtifactsAttributeValue(teamDef);
      if (match.isMatched()) {
         return match;
      }
      IAtsTeamDefinition parentArt = teamDef.getParentTeamDef();
      if (parentArt != null) {
         return getWorkDefinitionFromTeamDefinitionAttributeInherited(parentArt);
      }
      return new WorkDefinitionMatch();
   }

   @Override
   public IWorkDefinitionMatch getWorkDefinitionForTask(IAtsTask task) throws OseeCoreException {
      IAtsTeamWorkflow teamWf = workItemService.getParentTeamWorkflow(task);
      return getWorkDefinitionForTask(teamWf, task);
   }

   /**
    * Return the WorkDefinition that would be assigned to a new Task. This is not necessarily the actual WorkDefinition
    * used because it can be overridden once the Task artifact is created.
    */
   @Override
   public IWorkDefinitionMatch getWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      return getWorkDefinitionForTask(teamWf, null);
   }

   /**
    * @param task - if null, returned WorkDefinition will be proposed; else returned will be actual
    */
   private IWorkDefinitionMatch getWorkDefinitionForTask(IAtsTeamWorkflow teamWf, IAtsTask task) throws OseeCoreException {
      IWorkDefinitionMatch match = new WorkDefinitionMatch();
      for (ITeamWorkflowProvider provider : teamWorkflowProviders.getTeamWorkflowProviders()) {
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
         IWorkDefinitionMatch match2 = getTaskWorkDefinitionFromArtifactsAttributeValue(teamWf);
         if (match2.isMatched()) {
            match2.addTrace(String.format("from task parent SMA [%s]", teamWf));
            match = match2;
         }
      }
      if (!match.isMatched()) {
         // Else If parent TeamWorkflow's IAtsTeamDefinition has a related task definition workflow id, use it
         match = getTaskWorkDefinitionFromArtifactsAttributeValue(teamWf.getTeamDefinition());
      }
      if (!match.isMatched()) {
         match = getWorkDefinition(TaskWorkflowDefinitionId);
         match.addTrace("WorkDefinitionFactory - Default Task");
      }
      return match;
   }

   private IWorkDefinitionMatch getWorkDefinitionNew(IAtsWorkItem workItem) throws OseeCoreException {
      IWorkDefinitionMatch match = new WorkDefinitionMatch();
      if (workItem instanceof IAtsTask) {
         match = getWorkDefinitionForTask((IAtsTask) workItem);
      }
      if (!match.isMatched()) {
         // Check extensions for definition handling
         for (ITeamWorkflowProvider provider : teamWorkflowProviders.getTeamWorkflowProviders()) {
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
                  IAtsTeamDefinition teamDef = ((IAtsTeamWorkflow) workItem).getTeamDefinition();
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

   @Override
   public boolean isTaskOverridingItsWorkDefinition(IAtsTask task) throws MultipleAttributesExist, OseeCoreException {
      return attributeResolver.getSoleAttributeValueAsString(task, AtsAttributeTypes.WorkflowDefinition, null) != null;
   }

   /**
    * @return WorkDefinitionMatch for Peer Review either from attribute value or default
    */
   @Override
   public IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) throws OseeCoreException {
      Conditions.notNull(review, AtsWorkDefinitionAdminImpl.class.getSimpleName());
      IWorkDefinitionMatch match = getWorkDefinitionFromArtifactsAttributeValue(review);
      if (!match.isMatched()) {
         match = getDefaultPeerToPeerWorkflowDefinitionMatch();
      }
      return match;
   }

   @Override
   public IWorkDefinitionMatch getDefaultPeerToPeerWorkflowDefinitionMatch() throws OseeCoreException {
      IWorkDefinitionMatch match = getWorkDefinition(PeerToPeerDefaultWorkflowDefinitionId);
      match.addTrace("WorkDefinitionFactory - Default PeerToPeer");
      return match;
   }

   /**
    * @return WorkDefinitionMatch for peer review off created teamWf. Will use configured value off team definitions
    * with recurse or return default review work definition
    */
   @Override
   public IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      Conditions.notNull(teamWf, AtsWorkDefinitionAdminImpl.class.getSimpleName());
      IAtsTeamDefinition teamDefinition = teamWf.getTeamDefinition();
      IWorkDefinitionMatch match = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(teamDefinition);
      if (!match.isMatched()) {
         match = getDefaultPeerToPeerWorkflowDefinitionMatch();
      }
      return match;
   }

   /**
    * @return WorkDefinitionMatch of peer review from team definition related to actionableItem or return default review
    * work definition
    */
   @Override
   public IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) throws OseeCoreException {
      Conditions.notNull(actionableItem, AtsWorkDefinitionAdminImpl.class.getSimpleName());
      IWorkDefinitionMatch match =
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
   public IWorkDefinitionMatch getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(IAtsTeamDefinition teamDefinition) throws OseeCoreException {
      Conditions.notNull(teamDefinition, AtsWorkDefinitionAdminImpl.class.getSimpleName());
      IWorkDefinitionMatch match = new WorkDefinitionMatch();
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

   @Override
   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      return workDefinitionService.copyWorkDefinition(newName, workDef, resultData);
   }

   @Override
   public List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef) {
      return workDefinitionService.getStatesOrderedByOrdinal(workDef);
   }

   @Override
   public Collection<String> getStateNames(IAtsWorkDefinition workDef) {
      return workDefinitionService.getStateNames(workDef);
   }

   @Override
   public String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception {
      return workDefinitionService.getStorageString(workDef, resultData);
   }

   @Override
   public IAtsWorkDefinition getWorkDef(String id, XResultData resultData) throws Exception {
      return workDefinitionService.getWorkDef(id, resultData);
   }

   @Override
   public List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef) {
      return workDefinitionService.getWidgetsFromLayoutItems(stateDef);
   }

   @Override
   public boolean isStateWeightingEnabled(IAtsWorkDefinition workDef) {
      return workDefinitionService.isStateWeightingEnabled(workDef);
   }

   @Override
   public IAtsStateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName) throws OseeCoreException {
      return getWorkDefinition(workItem).getWorkDefinition().getStateByName(stateName);
   }

   @Override
   public Collection<String> getAllValidStateNames(XResultData resultData) throws Exception {
      return workDefinitionService.getAllValidStateNames(resultData);
   }
}
