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
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionDslService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.workdef.NullRuleDefinition;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImpl implements IAtsWorkDefinitionService {

   private final Cache<String, IAtsRuleDefinition> ruleDefinitionCache =
      CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
   private final LoadingCache<Long, IAtsWorkDefinition> workDefIdToWorkDef;
   private final LoadingCache<IAtsWorkItem, IAtsWorkDefinition> workItemToWorkDef;
   private final AtsApi atsApi;
   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final IAtsWorkDefinitionDslService workDefinitionDslService;
   private final IAtsWorkDefinitionStringProvider workDefinitionStringProvider;
   private final AtsWorkDefinitionStoreService workDefinitionStore;

   public AtsWorkDefinitionServiceImpl(AtsApi atsApi, AtsWorkDefinitionStoreService workDefinitionStore, IAtsWorkDefinitionStringProvider workDefinitionStringProvider, IAtsWorkDefinitionDslService workDefinitionDslService, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.atsApi = atsApi;
      this.workDefinitionStore = workDefinitionStore;
      this.workDefinitionStringProvider = workDefinitionStringProvider;
      this.workDefinitionDslService = workDefinitionDslService;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
      workDefIdToWorkDef = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(workDefLoader);
      workItemToWorkDef = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(workItemWorkDefLoader);
   }

   private final CacheLoader<Long, IAtsWorkDefinition> workDefLoader = new CacheLoader<Long, IAtsWorkDefinition>() {
      @Override
      public IAtsWorkDefinition load(Long id) {
         return getWorkDefinition(id);
      }
   };

   private final CacheLoader<IAtsWorkItem, IAtsWorkDefinition> workItemWorkDefLoader =
      new CacheLoader<IAtsWorkItem, IAtsWorkDefinition>() {
         @Override
         public IAtsWorkDefinition load(IAtsWorkItem workItem) {
            return computeWorkDefinition(workItem);
         }
      };

   @Override
   public void clearCaches() {
      workDefIdToWorkDef.invalidateAll();
      workItemToWorkDef.invalidateAll();
      ruleDefinitionCache.invalidateAll();
   }

   @Override
   public void addWorkDefinition(IAtsWorkDefinition workDef) {
      workDefIdToWorkDef.put(workDef.getId(), workDef);
   }

   @Override
   public void removeWorkDefinition(IAtsWorkDefinition workDef) {
      workDefIdToWorkDef.invalidate(workDef.getId());
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(IAtsWorkItem workItem) {
      try {
         return workItemToWorkDef.get(workItem);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error getting work definition for work item %s",
            workItem.toStringWithId());
      }
   }

   @Override
   public IAtsRuleDefinition getRuleDefinition(String name) {
      IAtsRuleDefinition ruleDef = null;
      try {
         ruleDef = ruleDefinitionCache.get(name, new Callable<IAtsRuleDefinition>() {
            @Override
            public IAtsRuleDefinition call() {
               List<IAtsRuleDefinition> ruleDefinitions =
                  workDefinitionDslService.getRuleDefinitions(workDefinitionStore.loadRuleDefinitionString());
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
         OseeLog.logf(AtsWorkDefinitionServiceImpl.class, Level.WARNING, "Could not load Rule Definition [%s]", name);
      }
      return ruleDef;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(String name) {
      for (Entry<Long, IAtsWorkDefinition> entry : workDefIdToWorkDef.asMap().entrySet()) {
         if (entry.getValue().getName().equals(name)) {
            return entry.getValue();
         }
      }
      XResultData resultData = new XResultData(false);
      IAtsWorkDefinition workDef = null;
      try {
         workDef = getWorkDefinition(name, resultData);
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Exception getting work definition [%s]", name);
      }
      if (workDef != null) {
         workDefIdToWorkDef.put(workDef.getId(), workDef);
      }
      return workDef;
   }

   private IAtsWorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) {
      IAtsWorkDefinition workDef = null;
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = null;
      Collection<Object> attributeValues =
         atsApi.getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.WorkflowDefinition);
      if (!attributeValues.isEmpty()) {
         workFlowDefId = (String) attributeValues.iterator().next();
      }
      if (Strings.isValid(workFlowDefId)) {
         workDef = getWorkDefinition(workFlowDefId);
      }
      return workDef;
   }

   private IAtsWorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDef = null;
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getWorkflowDefinition();
      if (Strings.isValid(workFlowDefId)) {
         workDef = getWorkDefinition(workFlowDefId);
      }
      return workDef;
   }

   private IAtsWorkDefinition getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDef = null;
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = teamDef.getRelatedTaskWorkDefinition();
      if (Strings.isValid(workFlowDefId)) {
         workDef = getWorkDefinition(workFlowDefId);
      }
      return workDef;
   }

   /**
    * Look at team def's attribute for Work Definition setting, otherwise, walk up team tree for setting
    */
   private IAtsWorkDefinition getWorkDefinitionFromTeamDefinitionAttributeInherited(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(teamDef);
      if (workDef != null) {
         return workDef;
      }
      IAtsTeamDefinition parentArt = teamDef.getParentTeamDef();
      if (parentArt != null) {
         workDef = getWorkDefinitionFromTeamDefinitionAttributeInherited(parentArt);
      }
      return workDef;
   }

   @Override
   public IAtsWorkDefinition computetWorkDefinitionForTask(IAtsTask task) {
      IAtsTeamWorkflow teamWf = task.getParentTeamWorkflow();
      return getWorkDefinitionForTask(teamWf, task);
   }

   /**
    * Return the WorkDefinition that would be assigned to a new Task. This is not necessarily the actual WorkDefinition
    * used because it can be overridden once the Task artifact is created.
    */
   @Override
   public IAtsWorkDefinition computedWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf) {
      return getWorkDefinitionForTask(teamWf, null);
   }

   /**
    * @param task - if null, returned WorkDefinition will be proposed; else returned will be actual
    */
   private IAtsWorkDefinition getWorkDefinitionForTask(IAtsTeamWorkflow teamWf, IAtsTask task) {
      IAtsWorkDefinition workDef = null;
      // If task specifies it's own workflow id, use it
      if (task != null) {
         workDef = getWorkDefinitionFromArtifactsAttributeValue(task);
      }
      if (workDef == null && teamWf != null) {
         for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getTeamWorkflowProviders()) {
            String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
            if (Strings.isValid(workFlowDefId)) {
               workDef = getWorkDefinition(workFlowDefId);
               break;
            }
         }
         if (workDef == null) {
            // Else If parent TeamWorkflow's IAtsTeamDefinition has a related task definition workflow id, use it
            workDef = getTaskWorkDefinitionFromArtifactsAttributeValue(teamWf.getTeamDefinition());
         }
      }
      if (workDef == null) {
         workDef = getWorkDefinition(TaskWorkflowDefinitionId);
      }
      return workDef;
   }

   @Override
   public IAtsWorkDefinition computeWorkDefinition(IAtsWorkItem workItem) {
      IAtsWorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(workItem);
      if (workDef == null && workItem instanceof IAtsTask) {
         workDef = computetWorkDefinitionForTask((IAtsTask) workItem);
      }
      if (workDef == null) {
         // Check extensions for definition handling
         for (ITeamWorkflowProvider provider : teamWorkflowProvidersLazy.getProviders()) {
            String workFlowDefId = provider.getWorkflowDefinitionId(workItem);
            if (Strings.isValid(workFlowDefId)) {
               workDef = getWorkDefinition(workFlowDefId);
            }
         }
         if (workDef == null) {
            // If this artifact specifies it's own workflow definition, use it
            workDef = getWorkDefinitionFromArtifactsAttributeValue(workItem);
            // Otherwise, use workflow defined by attribute of WorkflowDefinition
            // Note: This is new.  Old TeamDefs got workflow off relation
            if (workItem instanceof IAtsTeamWorkflow) {
               IAtsTeamDefinition teamDef = ((IAtsTeamWorkflow) workItem).getTeamDefinition();
               workDef = getWorkDefinitionFromTeamDefinitionAttributeInherited(teamDef);
            } else if (workItem instanceof IAtsGoal) {
               workDef = getWorkDefinition(GoalWorkflowDefinitionId);
            } else if (workItem instanceof IAgileBacklog) {
               workDef = getWorkDefinition(GoalWorkflowDefinitionId);
            } else if (workItem instanceof IAgileSprint) {
               workDef = getWorkDefinition(SprintWorkflowDefinitionId);
            } else if (workItem instanceof IAtsPeerToPeerReview) {
               workDef = getWorkDefinition(PeerToPeerDefaultWorkflowDefinitionId);
            } else if (workItem instanceof IAtsDecisionReview) {
               workDef = getWorkDefinition(DecisionWorkflowDefinitionId);
            }
         }
      }
      return workDef;
   }

   /**
    * @return WorkDefinitionMatch for Peer Review either from attribute value or default
    */
   @Override
   public IAtsWorkDefinition getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) {
      Conditions.notNull(review, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      IAtsWorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(review);
      if (workDef == null) {
         workDef = getDefaultPeerToPeerWorkflowDefinition();
      }
      return workDef;
   }

   @Override
   public IAtsWorkDefinition getDefaultPeerToPeerWorkflowDefinition() {
      return getWorkDefinition(PeerToPeerDefaultWorkflowDefinitionId);
   }

   /**
    * @return WorkDefinitionMatch for peer review off created teamWf. Will use configured value off team definitions
    * with recurse or return default review work definition
    */
   @Override
   public IAtsWorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) {
      Conditions.notNull(teamWf, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      IAtsTeamDefinition teamDefinition = teamWf.getTeamDefinition();
      IAtsWorkDefinition workDef = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(teamDefinition);
      if (workDef == null) {
         workDef = getDefaultPeerToPeerWorkflowDefinition();
      }

      return workDef;
   }

   /**
    * @return WorkDefinitionMatch of peer review from team definition related to actionableItem or return default review
    * work definition
    */
   @Override
   public IAtsWorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) {
      Conditions.notNull(actionableItem, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      IAtsWorkDefinition workDef = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(
         actionableItem.getTeamDefinitionInherited());
      if (workDef == null) {
         workDef = getDefaultPeerToPeerWorkflowDefinition();
      }
      return workDef;
   }

   /**
    * @return WorkDefinitionMatch of teamDefinition configured with RelatedPeerWorkflowDefinition attribute with recurse
    * up to top teamDefinition or will return no match
    */
   public IAtsWorkDefinition getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(IAtsTeamDefinition teamDefinition) {
      Conditions.notNull(teamDefinition, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      IAtsWorkDefinition workDef = null;
      String workDefId = teamDefinition.getRelatedPeerWorkDefinition();
      if (!Strings.isValid(workDefId)) {
         IAtsTeamDefinition parentTeamDef = teamDefinition.getParentTeamDef();
         if (parentTeamDef != null) {
            workDef = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(parentTeamDef);
         }
      } else {
         workDef = getWorkDefinition(workDefId);
      }
      return workDef;
   }

   @Override
   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      return workDefinitionDslService.copyWorkDefinition(newName, workDef, resultData);
   }

   @Override
   public List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef) {
      List<IAtsStateDefinition> orderedPages = new ArrayList<>();
      List<IAtsStateDefinition> unOrderedPages = new ArrayList<>();
      for (int x = 1; x < workDef.getStates().size() + 1; x++) {
         for (IAtsStateDefinition state : workDef.getStates()) {
            if (state.getOrdinal() == x) {
               orderedPages.add(state);
            } else if (state.getOrdinal() == 0 && !unOrderedPages.contains(state)) {
               unOrderedPages.add(state);
            }
         }
      }
      orderedPages.addAll(unOrderedPages);
      return orderedPages;
   }

   @Override
   public void getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef, IAtsStateDefinition stateDefinition, List<IAtsStateDefinition> pages) {
      if (pages.contains(stateDefinition)) {
         return;
      }
      // Add this page first
      pages.add(stateDefinition);
      // Add default page
      IAtsStateDefinition defaultToState = stateDefinition.getDefaultToState();
      if (defaultToState != null && !defaultToState.getName().equals(stateDefinition.getName())) {
         getStatesOrderedByDefaultToState(workDef, stateDefinition.getDefaultToState(), pages);
      }
      // Add remaining pages
      for (IAtsStateDefinition stateDef : stateDefinition.getToStates()) {
         if (!pages.contains(stateDef)) {
            getStatesOrderedByDefaultToState(workDef, stateDef, pages);
         }
      }
   }

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   @Override
   public List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef) {
      List<IAtsWidgetDefinition> widgets = new ArrayList<>();
      getWidgets(stateDef, widgets, stateDef.getLayoutItems());
      return widgets;
   }

   private static void getWidgets(IAtsStateDefinition stateDef, List<IAtsWidgetDefinition> widgets, List<IAtsLayoutItem> stateItems) {
      for (IAtsLayoutItem stateItem : stateItems) {
         if (stateItem instanceof IAtsCompositeLayoutItem) {
            getWidgets(stateDef, widgets, ((IAtsCompositeLayoutItem) stateItem).getaLayoutItems());
         } else if (stateItem instanceof IAtsWidgetDefinition) {
            widgets.add((IAtsWidgetDefinition) stateItem);
         }
      }
   }

   @Override
   public boolean hasWidgetNamed(IAtsStateDefinition stateDef, String name) {
      for (IAtsWidgetDefinition widgetDef : getWidgetsFromLayoutItems(stateDef)) {
         if (widgetDef.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Collection<String> getStateNames(IAtsWorkDefinition workDef) {
      List<String> names = new ArrayList<>();
      for (IAtsStateDefinition state : workDef.getStates()) {
         names.add(state.getName());
      }
      return names;
   }

   @Override
   public String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception {
      return workDefinitionDslService.getStorageString(workDef, resultData);
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(String workDefName, XResultData resultData) throws Exception {
      Conditions.checkNotNullOrEmpty(workDefName, "workDefName");
      WorkDefData workDefData = null;
      if (workDefinitionStringProvider != null && workDefinitionStringProvider.getWorkDefinitionsData() != null) {
         for (WorkDefData data : workDefinitionStringProvider.getWorkDefinitionsData()) {
            if (data.getName().equals(workDefName)) {
               workDefData = data;
               break;
            }
         }
      }
      if (workDefData == null) {
         workDefData = workDefinitionStore.loadWorkDefinitionString(workDefName);
      }
      IAtsWorkDefinition workDef = null;
      if (workDefData != null) {
         workDef = workDefinitionDslService.getWorkDefinition(workDefData.getId(), workDefData.getDsl());
      }
      return workDef;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(Long id) {
      IAtsWorkDefinition workDef = null;
      try {
         workDef = workDefIdToWorkDef.get(id);
      } catch (Exception ex) {
         // do nothing
      }
      if (workDef == null) {
         WorkDefData workDefData = null;
         if (workDefinitionStringProvider != null && workDefinitionStringProvider.getWorkDefinitionsData() != null) {
            for (WorkDefData data : workDefinitionStringProvider.getWorkDefinitionsData()) {
               if (data.getId().equals(id)) {
                  workDefData = data;
                  break;
               }
            }
         }
         if (workDefData == null) {
            workDefData = workDefinitionStore.loadWorkDefinitionString(id);
         }
         Conditions.checkNotNull(workDefData, "workDefData");
         workDef = workDefinitionDslService.getWorkDefinition(workDefData.getId(), workDefData.getDsl());
      }
      return workDef;
   }

   @Override
   public void reloadAll() {
      workDefIdToWorkDef.invalidateAll();
      for (WorkDefData data : workDefinitionStringProvider.getWorkDefinitionsData()) {
         IAtsWorkDefinition workDef = workDefinitionDslService.getWorkDefinition(data.getId(), data.getDsl());
         if (workDef != null) {
            workDefIdToWorkDef.put(workDef.getId(), workDef);
         }
      }
   }

   @Override
   public boolean isStateWeightingEnabled(IAtsWorkDefinition workDef) {
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         if (stateDef.getStateWeight() != 0) {
            return true;
         }
      }
      return false;
   }

   @Override
   public IAtsStateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName) {
      return getWorkDefinition(workItem).getStateByName(stateName);
   }

   @Override
   public Collection<String> getAllValidStateNames(XResultData resultData) throws Exception {
      Set<String> allValidStateNames = new HashSet<>();
      for (IAtsWorkDefinition workDef : getAllWorkDefinitions(resultData)) {
         for (String stateName : getStateNames(workDef)) {
            if (!allValidStateNames.contains(stateName)) {
               allValidStateNames.add(stateName);
            }
         }
      }
      return allValidStateNames;
   }

   @Override
   public void clearRuleDefinitionsCache() {
      ruleDefinitionCache.invalidateAll();
   }

   @Override
   public Collection<IAtsRuleDefinition> getAllRuleDefinitions() {
      if (ruleDefinitionCache.size() == 0) {
         // If no rules exists then load all rules and put in the cache
         List<IAtsRuleDefinition> ruleDefinitions =
            workDefinitionDslService.getRuleDefinitions(workDefinitionStore.loadRuleDefinitionString());
         for (IAtsRuleDefinition ruleDef : ruleDefinitions) {
            ruleDefinitionCache.put(ruleDef.getName(), ruleDef);
         }
      }
      Collection<IAtsRuleDefinition> ruleDefs = ruleDefinitionCache.asMap().values();
      return ruleDefs;
   }

   @Override
   public void cache(IAtsWorkDefinition workDef) {
      workDefIdToWorkDef.put(workDef.getId(), workDef);
   }

   @Override
   public boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption option) {
      boolean hasRule = false;
      IAtsTeamWorkflow teamWf = null;
      try {
         if (workItem instanceof IAtsTeamWorkflow) {
            teamWf = (IAtsTeamWorkflow) workItem;
         } else if (workItem instanceof IAtsAbstractReview) {
            teamWf = ((IAtsAbstractReview) workItem).getParentTeamWorkflow();
         }
         if (teamWf != null) {
            hasRule = teamWf.getTeamDefinition().hasRule(option.name());
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error reading rule [%s] for workItem %s", option, workItem.toStringWithId());
      }
      return hasRule;
   }

   @Override
   public boolean isInState(IAtsWorkItem workItem, IAtsStateDefinition stateDef) {
      return workItem.getStateMgr().getCurrentStateName().equals(stateDef.getName());
   }

   @Override
   public Collection<IAtsWorkDefinition> getAllWorkDefinitions(XResultData resultData) {
      List<IAtsWorkDefinition> workDefs = new ArrayList<>();
      if (workDefinitionStringProvider != null) {
         for (WorkDefData data : workDefinitionStringProvider.getWorkDefinitionsData()) {
            IAtsWorkDefinition workDef = workDefinitionDslService.getWorkDefinition(data.getId(), data.getDsl());
            if (workDef != null) {
               workDefIdToWorkDef.put(workDef.getId(), workDef);
            }
         }
      } else {
         for (WorkDefData data : workDefinitionStore.getWorkDefinitionsData()) {
            IAtsWorkDefinition workDef = workDefinitionDslService.getWorkDefinition(data.getId(), data.getDsl());
            if (workDef != null) {
               workDefIdToWorkDef.put(workDef.getId(), workDef);
            }
         }
      }
      return workDefs;
   }

}
