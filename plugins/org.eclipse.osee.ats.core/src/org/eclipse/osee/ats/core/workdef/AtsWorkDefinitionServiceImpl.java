/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.HeaderDefinition;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.agile.AgileItem;
import org.eclipse.osee.ats.core.workdef.operations.ValidateWorkDefinitionsOperation;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImpl implements IAtsWorkDefinitionService {

   private final AtsApi atsApi;
   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final Map<IAtsWorkItem, WorkDefinition> bootstrappingWorkItemToWorkDefCache = new HashMap<>();
   private final Set<IAtsWorkItem> logOnce = new HashSet<>();
   public static final String VALID_STATE_NAMES_KEY = "validStateNames";

   public AtsWorkDefinitionServiceImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.atsApi = atsApi;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   @Override
   public WorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject, AttributeTypeToken workDefAttrTypeId) {
      WorkDefinition workDefinition = null;
      String workDefIdStr =
         atsApi.getAttributeResolver().getSoleAttributeValueAsString(atsObject, workDefAttrTypeId, "");
      if (Strings.isNumeric(workDefIdStr)) {
         workDefinition = getWorkDefinition(Long.valueOf(workDefIdStr));
      }
      return workDefinition;
   }

   private WorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject) {
      return getWorkDefinitionFromAsObject(atsObject, AtsAttributeTypes.WorkflowDefinitionReference);
   }

   @Override
   public WorkDefinition getWorkDefinition(IAtsWorkItem workItem) {
      // check cache used for initial creation of work item
      WorkDefinition workDefinition = bootstrappingWorkItemToWorkDefCache.get(workItem);
      if (workDefinition != null) {
         return workDefinition;
      }
      try {
         workDefinition = getWorkDefinitionFromAsObject(workItem);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error getting work definition for work item %s",
            workItem.toStringWithId());
      }
      return workDefinition;
   }

   @Override
   public WorkDefinition getWorkDefinitionByName(String name) {
      for (WorkDefinition workDef : atsApi.getWorkDefinitionProviderService().getAll()) {
         if (workDef.getName().equals(name)) {
            return workDef;
         }
      }
      if (Strings.isNumeric(name)) {
         throw new OseeArgumentException("Can't get work def, but is numeric [%s], probably wrong method", name);
      }
      return null;
   }

   private WorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsWorkItem workItem) {
      WorkDefinition workDefinition = getWorkDefinitionFromAsObject(workItem);
      return workDefinition;
   }

   private WorkDefinition getWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      WorkDefinition workDefinition = getWorkDefinitionFromAsObject(teamDef);
      return workDefinition;
   }

   private WorkDefinition getTaskWorkDefinitionFromArtifactsAttributeValue(IAtsTeamDefinition teamDef) {
      WorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDef, AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference);
      return workDefinition;
   }

   /**
    * Look at team def's attribute for Work Definition setting, otherwise, walk up team tree for setting
    */
   private WorkDefinition getWorkDefinitionFromTeamDefinitionAttributeInherited(IAtsTeamDefinition teamDef) {
      WorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(teamDef);
      if (workDef != null) {
         return workDef;
      }
      IAtsTeamDefinition parentArt = atsApi.getTeamDefinitionService().getParentTeamDef(teamDef);
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
   public WorkDefinition computedWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf) {
      Conditions.assertNotNull(teamWf, "Team Workflow can not be null");
      WorkDefinition workDefinition = null;
      for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getTeamWorkflowProviders()) {
         AtsWorkDefinitionToken workDefTok = provider.getRelatedTaskWorkflowDefinitionId(teamWf);
         if (workDefTok != null && workDefTok.isValid()) {
            workDefinition = getWorkDefinition(workDefTok);
            break;
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
   public WorkDefinition computeWorkDefinition(IAtsWorkItem workItem) {
      return computeWorkDefinition(workItem, true);
   }

   @Override
   public WorkDefinition computeWorkDefinition(IAtsWorkItem workItem, boolean useAttr) {
      // If this artifact specifies it's own workflow definition, use it
      WorkDefinition workDef = null;
      if (useAttr) {
         workDef = getWorkDefinitionFromArtifactsAttributeValue(workItem);
      }
      if (workDef == null) {
         if (!logOnce.contains(workItem)) {
            OseeLog.log(AtsWorkDefinitionServiceImpl.class, Level.INFO,
               "No WorkDef attr for " + workItem.toStringWithId());
            logOnce.add(workItem);
         }
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
               if (workItem instanceof AgileItem) {
                  workItem = atsApi.getWorkItemService().getWorkItem(((AgileItem) workItem).getId());
               }

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
   public WorkDefinition getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) {
      Conditions.notNull(review, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      WorkDefinition workDef = getWorkDefinitionFromArtifactsAttributeValue(review);
      if (workDef == null) {
         workDef = getDefaultPeerToPeerWorkflowDefinition();
      }
      return workDef;
   }

   @Override
   public WorkDefinition getDefaultPeerToPeerWorkflowDefinition() {
      return getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer);
   }

   /**
    * @return WorkDefinitionMatch for peer review off created teamWf. Will use configured value off team definitions
    * with recurse or return default review work definition
    */
   @Override
   public WorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) {
      Conditions.notNull(teamWf, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      IAtsTeamDefinition teamDefinition = teamWf.getTeamDefinition();
      WorkDefinition workDef = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(teamDefinition);
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
   public WorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) {
      Conditions.notNull(actionableItem, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      WorkDefinition workDef = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(
         actionableItem.getAtsApi().getActionableItemService().getTeamDefinitionInherited(actionableItem));
      if (workDef == null) {
         workDef = getDefaultPeerToPeerWorkflowDefinition();
      }
      return workDef;
   }

   /**
    * @return WorkDefinitionMatch of teamDefinition configured with RelatedPeerWorkflowDefinition attribute with recurse
    * up to top teamDefinition or will return no match
    */
   public WorkDefinition getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(IAtsTeamDefinition teamDef) {
      Conditions.notNull(teamDef, AtsWorkDefinitionServiceImpl.class.getSimpleName());
      WorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDef, AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference);
      if (workDefinition == null || workDefinition.isInvalid()) {
         IAtsTeamDefinition parentTeamDef = atsApi.getTeamDefinitionService().getParentTeamDef(teamDef);
         if (parentTeamDef != null) {
            workDefinition = getPeerToPeerWorkDefinitionFromTeamDefinitionAttributeValueRecurse(parentTeamDef);
         }
      }
      return workDefinition;
   }

   @Override
   public List<StateDefinition> getStatesOrderedByOrdinal(WorkDefinition workDef) {
      List<StateDefinition> orderedPages = new ArrayList<>();
      List<StateDefinition> unOrderedPages = new ArrayList<>();
      for (int x = 1; x < workDef.getStates().size() + 1; x++) {
         for (StateDefinition state : workDef.getStates()) {
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

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   @Override
   public List<WidgetDefinition> getWidgetsFromLayoutItems(StateDefinition stateDef) {
      List<WidgetDefinition> widgets = new ArrayList<>();
      getWidgets(stateDef, widgets, stateDef.getLayoutItems());
      return widgets;
   }

   @Override
   public List<WidgetDefinition> getWidgetsFromLayoutItems(StateDefinition stateDef, List<LayoutItem> layoutItems) {
      List<WidgetDefinition> widgets = new ArrayList<>();
      getWidgets(stateDef, widgets, layoutItems);
      return widgets;
   }

   private static void getWidgets(StateDefinition stateDef, List<WidgetDefinition> widgets, List<LayoutItem> layoutItems) {
      for (LayoutItem lItem : layoutItems) {
         if (lItem instanceof CompositeLayoutItem) {
            getWidgets(stateDef, widgets, ((CompositeLayoutItem) lItem).getLayoutItems());
         } else if (lItem instanceof WidgetDefinition) {
            widgets.add((WidgetDefinition) lItem);
         }
      }
   }

   private static void getWidgets(HeaderDefinition headerDef, List<WidgetDefinition> widgets, List<LayoutItem> layoutItems) {
      for (LayoutItem lItem : layoutItems) {
         if (lItem instanceof CompositeLayoutItem) {
            getWidgets(headerDef, widgets, ((CompositeLayoutItem) lItem).getLayoutItems());
         } else if (lItem instanceof WidgetDefinition) {
            widgets.add((WidgetDefinition) lItem);
         }
      }
   }

   @Override
   public Collection<WidgetDefinition> getWidgets(WorkDefinition workDef) {
      List<WidgetDefinition> widgets = new ArrayList<>();
      getWidgets(workDef.getHeaderDef(), widgets, workDef.getHeaderDef().getLayoutItems());
      for (StateDefinition stateDef : workDef.getStates()) {
         getWidgets(stateDef, widgets, stateDef.getLayoutItems());
      }
      return widgets;
   }

   @Override
   public boolean hasWidgetNamed(StateDefinition stateDef, String name) {
      for (WidgetDefinition widgetDef : getWidgetsFromLayoutItems(stateDef)) {
         if (widgetDef.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Collection<String> getStateNames(WorkDefinition workDef) {
      return Named.getNames(workDef.getStates());
   }

   @Override
   public WorkDefinition getWorkDefinition(Long id) {
      Conditions.assertTrue(id > 0, "Id must be > 0, not %s", id);
      return atsApi.getWorkDefinitionProviderService().getWorkDefinition(id);
   }

   @Override
   public StateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName) {
      return getWorkDefinition(workItem).getStateByName(stateName);
   }

   @Override
   public Collection<String> computeAllValidStateNames() {
      Set<String> allValidStateNames = new HashSet<>();
      for (WorkDefinition workDef : getAllWorkDefinitions()) {
         for (String stateName : getStateNames(workDef)) {
            if (!allValidStateNames.contains(stateName)) {
               allValidStateNames.add(stateName);
            }
         }
      }
      return allValidStateNames;
   }

   @Override
   public Collection<String> getAllValidStateNamesFromConfig() {
      String stateNamesStr = atsApi.getConfigValue(VALID_STATE_NAMES_KEY);
      List<String> stateNames = new LinkedList<>();
      if (Strings.isValid(stateNamesStr)) {
         for (String stateName : stateNamesStr.split(",")) {
            stateNames.add(stateName);
         }
      }
      return stateNames;
   }

   @Override
   public Collection<String> updateAllValidStateNames() {
      Collection<String> validStateNames = atsApi.getWorkDefinitionService().computeAllValidStateNames();
      atsApi.setConfigValue(VALID_STATE_NAMES_KEY, Collections.toString(",", validStateNames));
      return validStateNames;
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
            hasRule = atsApi.getTeamDefinitionService().hasRule(teamWf.getTeamDefinition(), option.name());
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error reading rule [%s] for workItem %s", option, workItem.toStringWithId());
      }
      return hasRule;
   }

   @Override
   public boolean isInState(IAtsWorkItem workItem, StateDefinition stateDef) {
      return workItem.getCurrentStateName().equals(stateDef.getName());
   }

   @Override
   public Collection<WorkDefinition> getAllWorkDefinitions() {
      return atsApi.getWorkDefinitionProviderService().getAll();
   }

   @Override
   public ArtifactToken getWorkDefArt(String workDefName) {
      return atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.WorkDefinition, workDefName);
   }

   @Override
   public WorkDefinition computeWorkDefinitionForTeamWfNotYetCreated(IAtsTeamDefinition teamDef, Collection<INewActionListener> newActionListeners) {
      Conditions.assertNotNull(teamDef, "Team Definition can not be null");

      // If work def id is specified by listener, set as attribute
      WorkDefinition workDefinition = null;
      if (newActionListeners != null) {
         for (INewActionListener listener : newActionListeners) {
            AtsWorkDefinitionToken workDefTok = listener.getOverrideWorkDefinitionId(teamDef);
            if (workDefTok != null) {
               workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefTok);
               break;
            }
         }
      }
      // else if work def is specified by provider, set as attribute
      if (workDefinition == null) {
         for (ITeamWorkflowProvider provider : atsApi.getWorkItemService().getTeamWorkflowProviders().getProviders()) {
            AtsWorkDefinitionToken workDefTok = provider.getOverrideWorkflowDefinitionId(teamDef);
            if (workDefTok != null) {
               workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefTok);
            }
         }
      }
      // else if work def is specified by teamDef
      if (workDefinition == null) {
         workDefinition = getWorkDefinitionForTeamWfFromTeamDef(teamDef);
      }
      if (workDefinition == null) {
         throw new OseeStateException("Work Definition not computed for %s", teamDef.toStringWithId());
      }
      return workDefinition;
   }

   private WorkDefinition getWorkDefinitionForTeamWfFromTeamDef(IAtsTeamDefinition teamDef) {
      WorkDefinition workDefinition =
         getWorkDefinitionFromAsObject(teamDef, AtsAttributeTypes.WorkflowDefinitionReference);
      if (workDefinition != null && workDefinition.isValid()) {
         return workDefinition;
      }

      IAtsTeamDefinition parentTeamDef = atsApi.getTeamDefinitionService().getParentTeamDef(teamDef);
      if (parentTeamDef == null) {
         return atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Team_Default);
      }
      return getWorkDefinitionForTeamWfFromTeamDef(parentTeamDef);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamDefinition teamDef, WorkDefinition workDefinition, IAtsChangeSet changes) {
      setWorkDefinitionAttrs((IAtsObject) teamDef, workDefinition, changes);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsWorkItem workItem, WorkDefinition workDefinition, IAtsChangeSet changes) {
      setWorkDefinitionAttrs((IAtsObject) workItem, workDefinition, changes);
   }

   private void setWorkDefinitionAttrs(IAtsObject atsObject, WorkDefinition workDef, IAtsChangeSet changes) {
      Conditions.assertNotNull(workDef, "workDefArt");
      Conditions.assertNotSentinel(workDef, "workDefArt");
      changes.setSoleAttributeValue(atsObject, AtsAttributeTypes.WorkflowDefinitionReference,
         Id.valueOf(workDef.getId()));
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamDefinition topTeam, NamedIdBase id, IAtsChangeSet changes) {
      Conditions.assertNotNull(topTeam, "topTeam");
      Conditions.assertNotSentinel(topTeam, "topTeam");
      Conditions.assertNotNull(id, "id");
      Conditions.assertNotSentinel(id, "id");
      changes.setSoleAttributeValue(topTeam, AtsAttributeTypes.WorkflowDefinitionReference, id);
   }

   @Override
   public void setWorkDefinitionAttrs(IAtsTeamWorkflow teamWf, NamedIdBase id, IAtsChangeSet changes) {
      Conditions.assertNotNull(teamWf, "teamWf");
      Conditions.assertNotSentinel(teamWf, "teamWf");
      Conditions.assertNotNull(id, "id");
      Conditions.assertNotSentinel(id, "id");
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.WorkflowDefinitionReference, id);
   }

   @Override
   public void internalSetWorkDefinition(IAtsWorkItem workItem, WorkDefinition workDef) {
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
   public WorkDefinition getWorkDefinition(Id id) {
      return getWorkDefinition(id.getId());
   }

   @Override
   public XResultData validateWorkDefinitions() {
      ValidateWorkDefinitionsOperation op = new ValidateWorkDefinitionsOperation(atsApi);
      return op.run();
   }

}
