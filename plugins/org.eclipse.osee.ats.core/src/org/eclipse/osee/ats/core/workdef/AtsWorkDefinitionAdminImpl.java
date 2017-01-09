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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.ats.api.workdef.NullRuleDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionAdminImpl implements IAtsWorkDefinitionAdmin {

   private final AtsWorkDefinitionCache workDefCache;
   private final IAtsWorkDefinitionService workDefinitionService;
   private final IAttributeResolver attributeResolver;
   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final Cache<String, IAtsRuleDefinition> ruleDefinitionCache =
      CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

   public AtsWorkDefinitionAdminImpl(AtsWorkDefinitionCache workDefCache, IAtsWorkDefinitionService workDefinitionService, IAttributeResolver attributeResolver, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.workDefCache = workDefCache;
      this.workDefinitionService = workDefinitionService;
      this.attributeResolver = attributeResolver;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   private AtsWorkDefinitionCache getCache() throws OseeCoreException {
      return workDefCache;
   }

   @Override
   public void clearCaches() {
      workDefCache.invalidate();
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
   public IAtsRuleDefinition getRuleDefinition(String name) {
      IAtsRuleDefinition ruleDef = null;
      try {
         ruleDef = ruleDefinitionCache.get(name, new Callable<IAtsRuleDefinition>() {
            @Override
            public IAtsRuleDefinition call() {
               List<IAtsRuleDefinition> ruleDefinitions = workDefinitionService.getRuleDefinitions();
               IAtsRuleDefinition ruleDefinition = NullRuleDefinition.getInstance();
               for (IAtsRuleDefinition ruleDef : ruleDefinitions) {
                  if (!(ruleDef instanceof NullRuleDefinition)) {
                     ruleDefinitionCache.put(ruleDef.getName(), ruleDef);
                  }
                  if (ruleDef.getName().equals(name)) {
                     ruleDefinition = ruleDef;
                  }
               }
               return ruleDefinition;
            }
         });

      } catch (ExecutionException ex) {
         OseeLog.logf(AtsWorkDefinitionAdminImpl.class, Level.WARNING, "Could not load Rule Definition [%s]", name);
      }
      return ruleDef;
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
                     match.addTrace(String.format("from DSL provider loaded id [%s] [%s]", id, resultData.toString()));
                  } else {
                     match.addTrace(String.format("from DSL provider loaded id [%s]", id));
                  }
               }
            } catch (Exception ex) {
               return new WorkDefinitionMatch(null, ex.getLocalizedMessage());
            }
         }
         if (match.isMatched()) {
            OseeLog.logf(AtsWorkDefinitionAdminImpl.class, Level.FINE, "Loaded Work Definition [%s]", match);
            getCache().cache(id, match);
            toReturn = match;
         } else {
            OseeLog.logf(AtsWorkDefinitionAdminImpl.class, Level.SEVERE, "Unable to load Work Definition [%s]", id);
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
         attributeResolver.getAttributeValues(workItem, AtsAttributeTypes.WorkflowDefinition);
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
         attributeResolver.getAttributeValues(workItem, AtsAttributeTypes.RelatedTaskWorkDefinition);
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
      IAtsTeamWorkflow teamWf = task.getParentTeamWorkflow();
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
      // If task specifies it's own workflow id, use it
      IWorkDefinitionMatch match =
         task == null ? new WorkDefinitionMatch() : getWorkDefinitionFromArtifactsAttributeValue(task);
      if (teamWf != null) {
         if (!match.isMatched() && task != null) {
            for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getTeamWorkflowProviders()) {
               String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
               attributeResolver.getSoleAttributeValue(task.getStoreObject(), AtsAttributeTypes.WorkflowDefinition,
                  null);
               if (Strings.isValid(workFlowDefId)) {
                  match = getWorkDefinition(workFlowDefId);
                  match.addTrace(String.format("from provider [%s] for id [%s]", provider.getClass().getSimpleName(),
                     workFlowDefId));
                  break;
               }
            }
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
         for (ITeamWorkflowProvider provider : teamWorkflowProvidersLazy.getProviders()) {
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
               } else if (workItem instanceof IAgileBacklog) {
                  match = getWorkDefinition(GoalWorkflowDefinitionId);
                  match.addTrace("WorkDefinitionFactory - GoalWorkflowDefinitionId");
               } else if (workItem instanceof IAgileSprint) {
                  match = getWorkDefinition(SprintWorkflowDefinitionId);
                  match.addTrace("WorkDefinitionFactory - SprintWorkflowDefinitionId");
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
      IWorkDefinitionMatch match = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(
         actionableItem.getTeamDefinitionInherited());
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

   @Override
   public void clearRuleDefinitionsCache() {
      ruleDefinitionCache.invalidateAll();
   }

   @Override
   public Collection<IAtsRuleDefinition> getAllRuleDefinitions() {
      if (ruleDefinitionCache.size() == 0) {
         // If no rules exists then load all rules and put in the cache
         List<IAtsRuleDefinition> ruleDefinitions = workDefinitionService.getRuleDefinitions();
         for (IAtsRuleDefinition ruleDef : ruleDefinitions) {
            ruleDefinitionCache.put(ruleDef.getName(), ruleDef);
         }
      }
      Collection<IAtsRuleDefinition> ruleDefs = ruleDefinitionCache.asMap().values();
      return ruleDefs;
   }

}
