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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.util.ConvertAtsConfigGuidAttributesOperations;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImpl implements IAtsWorkDefinitionService {

   private final AtsApi atsApi;
   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final Map<String, IAtsWorkDefinition> workDefNameToWorkDef = new HashMap<>();
   private final Map<IAtsWorkItem, IAtsWorkDefinition> bootstrappingWorkItemToWorkDefCache = new HashMap<>();
   private final Map<String, IAtsRuleDefinition> ruleDefNameToRuleDef = new HashMap<>();

   public AtsWorkDefinitionServiceImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.atsApi = atsApi;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject, AttributeTypeToken workDefAttrTypeId) {
      IAtsWorkDefinition workDefinition = null;
      String workDefIdStr =
         atsApi.getAttributeResolver().getSoleAttributeValueAsString(atsObject, workDefAttrTypeId, "");
      if (Strings.isNumeric(workDefIdStr)) {
         workDefinition = getWorkDefinition(Long.valueOf(workDefIdStr));
      }
      return workDefinition;
   }

   private IAtsWorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject) {
      return getWorkDefinitionFromAsObject(atsObject, AtsAttributeTypes.WorkflowDefinitionReference);
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(IAtsWorkItem workItem) {
      // check cache used for initial creation of work item
      IAtsWorkDefinition workDefinition = bootstrappingWorkItemToWorkDefCache.get(workItem);
      if (workDefinition != null) {
         return workDefinition;
      }
      try {
         workDefinition = getWorkDefinitionFromAsObject(workItem);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error getting work definition for work item %s",
            workItem.toStringWithId());
      }
      if (workDefinition == null && atsApi.isWorkDefAsName()) {
         try {
            return computeWorkDefinition(workItem);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex, "Error getting work definition for work item %s",
               workItem.toStringWithId());
         }
      }
      return workDefinition;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(String name) {
      for (IAtsWorkDefinition workDef : atsApi.getWorkDefinitionProviderService().getAll()) {
         if (workDef.getName().equals(name)) {
            return workDef;
         }
      }
      return null;
   }

   private IAtsWorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) {
      IAtsWorkDefinition workDefinition = getWorkDefinitionFromAsObject(workItem);
      if (workDefinition == null && atsApi.isWorkDefAsName()) {
         // If this artifact specifies it's own workflow definition, use it
         String workFlowDefId = null;
         Collection<Object> attributeValues = atsApi.getAttributeResolver().getAttributeValues(workItem,
            ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition);
         if (!attributeValues.isEmpty()) {
            workFlowDefId = (String) attributeValues.iterator().next();
         }
         if (Strings.isValid(workFlowDefId)) {
            workDefinition = getWorkDefinition(workFlowDefId);
         }
      }
      return workDefinition;
   }

   private IAtsWorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDefinition = getWorkDefinitionFromAsObject(teamDef);
      if (workDefinition == null && atsApi.isWorkDefAsName()) {
         String workFlowDefId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef,
            ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition, "");
         if (Strings.isValid(workFlowDefId)) {
            workDefinition = getWorkDefinition(workFlowDefId);
         }
      }
      return workDefinition;
   }

   private IAtsWorkDefinition getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDef, AtsAttributeTypes.RelatedTaskWorkDefinitionReference);
      if (workDefinition == null && atsApi.isWorkDefAsName()) {
         // If this artifact specifies it's own workflow definition, use it
         String workFlowDefId = atsApi.getAttributeResolver().getSoleAttributeValueAsString(teamDef,
            ConvertAtsConfigGuidAttributesOperations.RelatedTaskWorkDefinition, "");
         if (Strings.isValid(workFlowDefId)) {
            workDefinition = getWorkDefinition(workFlowDefId);
         }
      }
      return workDefinition;
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

   /**
    * Return the WorkDefinition that would be assigned to a new Task. This is not necessarily the actual WorkDefinition
    * used because it can be overridden once the Task artifact is created.
    */
   @Override
   public IAtsWorkDefinition computedWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf) {
      Conditions.assertNotNull(teamWf, "Team Workflow can not be null");
      IAtsWorkDefinition workDefinition = null;
      for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getTeamWorkflowProviders()) {
         AtsWorkDefinitionToken workDefTok = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
         if (workDefTok != null && workDefTok.isValid()) {
            workDefinition = getWorkDefinition(workDefTok);
            break;
         }
         if (atsApi.isWorkDefAsName()) {
            AtsWorkDefinitionToken workDefT = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
            if (workDefT != null && workDefT.isValid()) {
               workDefinition = getWorkDefinition(workDefT);
               break;
            }
         }
      }
      if (workDefinition == null) {
         // Else If parent TeamWorkflow's IAtsTeamDefinition has a related task definition workflow id, use it
         workDefinition = getTaskWorkDefinitionFromArtifactsAttributeValue(teamWf.getTeamDefinition());
      }
      if (workDefinition == null) {
         workDefinition =
            atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Task_Default);
      }
      return workDefinition;
   }

   @Override
   public IAtsWorkDefinition computeWorkDefinition(IAtsWorkItem workItem) {
      // If this artifact specifies it's own workflow definition, use it
      IAtsWorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(workItem);
      if (workDef == null) {
         // Tasks should never be needed once a database is converted to store work def as attribute
         if (workItem instanceof IAtsTask && ((IAtsTask) workItem).getParentTeamWorkflow() != null) {
            workDef = computedWorkDefinitionForTaskNotYetCreated(((IAtsTask) workItem).getParentTeamWorkflow());
         }
         if (workDef == null) {
            // Check extensions for definition handling
            for (ITeamWorkflowProvider provider : teamWorkflowProvidersLazy.getProviders()) {
               AtsWorkDefinitionToken workFlowDefId = provider.getWorkflowDefinitionId(workItem);
               if (workFlowDefId != null && workFlowDefId.isValid()) {
                  workDef = getWorkDefinition(workFlowDefId);
               }
            }
            if (workDef == null) {
               // Otherwise, use workflow defined by attribute of WorkflowDefinition
               // Note: This is new.  Old TeamDefs got workflow off relation
               if (workItem instanceof IAtsTeamWorkflow) {
                  IAtsTeamDefinition teamDef = ((IAtsTeamWorkflow) workItem).getTeamDefinition();
                  Conditions.assertNotNull(teamDef, "Team Def can not be null for %s.  Re-convert?",
                     workItem.toStringWithId());
                  workDef = getWorkDefinitionFromTeamDefinitionAttributeInherited(teamDef);
               } else if (workItem instanceof IAtsGoal) {
                  workDef = atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
               } else if (workItem instanceof IAgileBacklog) {
                  workDef = atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Goal);
               } else if (workItem instanceof IAgileSprint) {
                  workDef = atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Sprint);
               } else if (workItem instanceof IAtsPeerToPeerReview) {
                  workDef = atsApi.getWorkDefinitionService().getWorkDefinition(
                     AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer);
               } else if (workItem instanceof IAtsDecisionReview) {
                  workDef = atsApi.getWorkDefinitionService().getWorkDefinition(
                     AtsWorkDefinitionTokens.WorkDef_Review_Decision);
               }
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
      return getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer);
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
      IAtsWorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDefinition, AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference);
      if (workDefinition == null || workDefinition.isInvalid()) {
         IAtsTeamDefinition parentTeamDef = teamDefinition.getParentTeamDef();
         if (parentTeamDef != null) {
            workDefinition = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(parentTeamDef);
         }
      }
      if (workDefinition == null && atsApi.isWorkDefAsName()) {
         String workDefId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDefinition,
            ConvertAtsConfigGuidAttributesOperations.RelatedPeerWorkflowDefinition, "");
         if (!Strings.isValid(workDefId)) {
            IAtsTeamDefinition parentTeamDef = teamDefinition.getParentTeamDef();
            if (parentTeamDef != null) {
               workDefinition = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(parentTeamDef);
            }
         } else {
            workDefinition = getWorkDefinition(workDefId);
         }
      }
      return workDefinition;
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
   public IAtsWorkDefinition getWorkDefinition(Long id) {
      Conditions.assertTrue(id > 0, "Id must be > 0, not %s", id);
      return atsApi.getWorkDefinitionProviderService().getWorkDefinition(id);
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
      for (IAtsWorkDefinition workDef : getAllWorkDefinitions()) {
         for (String stateName : getStateNames(workDef)) {
            if (!allValidStateNames.contains(stateName)) {
               allValidStateNames.add(stateName);
            }
         }
      }
      return allValidStateNames;
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
   public Collection<IAtsWorkDefinition> getAllWorkDefinitions() {
      return atsApi.getWorkDefinitionProviderService().getAll();
   }

   @Override
   public ArtifactToken getWorkDefArt(String workDefName) {
      return atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.WorkDefinition, workDefName);
   }

   @Override
   public IAtsWorkDefinition computeWorkDefinitionForTeamWfNotYetCreated(IAtsTeamWorkflow teamWf, INewActionListener newActionListener) {
      // If work def id is specified by listener, set as attribute
      IAtsWorkDefinition workDefinition = null;
      if (newActionListener != null) {
         AtsWorkDefinitionToken workDefTok = newActionListener.getOverrideWorkDefinitionId(teamWf);
         if (workDefTok != null) {
            workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefTok);
         }
      }
      // else if work def is specified by provider, set as attribute
      if (workDefinition == null) {
         for (ITeamWorkflowProvider provider : atsApi.getWorkItemService().getTeamWorkflowProviders().getProviders()) {
            AtsWorkDefinitionToken workDefT = provider.getOverrideWorkflowDefinitionId(teamWf);
            if (workDefT != null) {
               workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefT);
            }
         }
      }
      // else if work def is specified by teamDef
      if (workDefinition == null) {
         workDefinition = getWorkDefinitionForTeamWfFromTeamDef(teamWf.getTeamDefinition());
      }
      if (workDefinition == null) {
         throw new OseeStateException("Work Definition not computed for %s", teamWf.toStringWithId());
      }
      return workDefinition;
   }

   private IAtsWorkDefinition getWorkDefinitionForTeamWfFromTeamDef(IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDef, AtsAttributeTypes.WorkflowDefinitionReference);
      if (workDefinition != null && workDefinition.isValid()) {
         return workDefinition;
      }
      String workDefName = atsApi.getAttributeResolver().getSoleAttributeValueAsString(teamDef,
         ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition, null);
      if (Strings.isValid(workDefName)) {
         workDefinition = workDefNameToWorkDef.get(workDefName);
         if (workDefinition == null) {
            workDefinition = getWorkDefinition(workDefName);
            workDefNameToWorkDef.put(workDefName, workDefinition);
         }
         return workDefinition;
      }

      IAtsTeamDefinition parentTeamDef = teamDef.getParentTeamDef();
      if (parentTeamDef == null) {
         return atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Team_Default);
      }
      return getWorkDefinitionForTeamWfFromTeamDef(parentTeamDef);
   }

   @Override
   public IAtsWorkDefinition computeAndSetWorkDefinitionAttrs(IAtsWorkItem workItem, INewActionListener newActionListener, IAtsChangeSet changes) {
      IAtsWorkDefinition workDefinition = null;
      if (workItem.isTeamWorkflow()) {
         workDefinition = computeWorkDefinitionForTeamWfNotYetCreated((IAtsTeamWorkflow) workItem, newActionListener);
      } else if (workItem.isTask()) {
         workDefinition = computedWorkDefinitionForTaskNotYetCreated(workItem.getParentTeamWorkflow());
      } else {
         workDefinition = computeWorkDefinition(workItem);
      }
      Conditions.checkNotNull(workDefinition, "workDefinition");

      // set work definition attribute
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(workItem, workDefinition, changes);
      return workDefinition;
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamDefinition teamDef, IAtsWorkDefinition workDefinition, IAtsChangeSet changes) {
      setWorkDefinitionAttrs((IAtsObject) teamDef, workDefinition, changes);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsWorkItem workItem, IAtsWorkDefinition workDefinition, IAtsChangeSet changes) {
      setWorkDefinitionAttrs((IAtsObject) workItem, workDefinition, changes);
   }

   private void setWorkDefinitionAttrs(IAtsObject atsObject, IAtsWorkDefinition workDef, IAtsChangeSet changes) {
      Conditions.assertNotNull(workDef, "workDefArt");
      Conditions.assertNotSentinel(workDef, "workDefArt");
      changes.setSoleAttributeValue(atsObject, ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition,
         workDef.getName());
      changes.setSoleAttributeValue(atsObject, AtsAttributeTypes.WorkflowDefinitionReference, workDef);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamDefinition topTeam, NamedIdBase id, IAtsChangeSet changes) {
      Conditions.assertNotNull(topTeam, "topTeam");
      Conditions.assertNotSentinel(topTeam, "topTeam");
      Conditions.assertNotNull(id, "id");
      Conditions.assertNotSentinel(id, "id");
      changes.setSoleAttributeValue(topTeam, ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition, id.getName());
      changes.setSoleAttributeValue(topTeam, AtsAttributeTypes.WorkflowDefinitionReference, id);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamWorkflow teamWf, NamedIdBase id, IAtsChangeSet changes) {
      Conditions.assertNotNull(teamWf, "teamWf");
      Conditions.assertNotSentinel(teamWf, "teamWf");
      Conditions.assertNotNull(id, "id");
      Conditions.assertNotSentinel(id, "id");
      changes.setSoleAttributeValue(teamWf, ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition, id.getName());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.WorkflowDefinitionReference, id);
   }

   @Override
   public void internalSetWorkDefinition(IAtsWorkItem workItem, IAtsWorkDefinition workDef) {
      bootstrappingWorkItemToWorkDefCache.put(workItem, workDef);
   }

   @Override
   public void internalClearWorkDefinition(IAtsWorkItem workItem) {
      bootstrappingWorkItemToWorkDefCache.remove(workItem);
   }

   @Override
   public void addWorkDefinition(IAtsWorkDefinitionBuilder workDefBuilder) {
      atsApi.getWorkDefinitionProviderService().addWorkDefinition(workDefBuilder.build());
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(Id id) {
      return getWorkDefinition(id.getId());
   }

   @Override
   public Collection<IAtsRuleDefinition> getAllRuleDefinitions() {
      return ruleDefNameToRuleDef.values();
   }

   @Override
   public void addRuleDefinition(IAtsRuleDefinition ruleDef) {
      if (ruleDefNameToRuleDef.keySet().contains(ruleDef.getName())) {
         throw new OseeArgumentException("Can't have >1 Rule Defs with same name [%s]", ruleDef.getName());
      }
      ruleDefNameToRuleDef.put(ruleDef.getName(), ruleDef);
   }

   @Override
   public IAtsRuleDefinition getRuleDefinition(String name) {
      for (IAtsRuleDefinition ruleDef : getAllRuleDefinitions()) {
         if (ruleDef.getName().equals(name)) {
            return ruleDef;
         }
      }
      return null;
   }

}
