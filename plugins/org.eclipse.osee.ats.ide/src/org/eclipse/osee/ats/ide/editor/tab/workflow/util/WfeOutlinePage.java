/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.HeaderDefinition;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.editor.WorkDefinitionViewer;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @author Donald G. Dunne
 */
public class WfeOutlinePage extends ContentOutlinePage {

   private WorkflowEditor workflowEditor;
   private WorkDefinitionViewer workDefViewer;
   private boolean isTeamWf;

   @Override
   public void createControl(Composite parent) {
      super.createControl(parent);

      Tree tree = getTreeViewer().getTree();
      tree.setLayout(new FillLayout(SWT.VERTICAL));
      getTreeViewer().setContentProvider(new InternalContentProvider());
      getTreeViewer().setLabelProvider(new InternalLabelProvider());
      if (workflowEditor != null) {
         setInput(workflowEditor);
      } else if (workDefViewer != null) {
         setInput(workDefViewer);
      } else {
         setInput("No Imput Provided");
      }

      getSite().getActionBars().getToolBarManager().add(
         new Action("Refresh", ImageManager.getImageDescriptor(PluginUiImage.REFRESH)) {
            @Override
            public void run() {
               refresh();
            }
         });
      getSite().getActionBars().getToolBarManager().update(true);
   }

   public void setInput(Object input) {
      if (input instanceof WorkflowEditor) {
         this.workflowEditor = (WorkflowEditor) input;
         if (getTreeViewer() != null) {
            if (workflowEditor != null && getTreeViewer() != null && Widgets.isAccessible(getTreeViewer().getTree())) {
               getTreeViewer().setInput(workflowEditor);
               try {
                  AbstractWorkflowArtifact awa = workflowEditor.getWorkItem();
                  if (awa != null) {
                     StateDefinition stateDef = WorkflowManager.getCurrentAtsWorkPage(awa).getStateDefinition();
                     StructuredSelection newSelection = new StructuredSelection(Arrays.asList(stateDef));
                     getTreeViewer().expandToLevel(awa, 2);
                     getTreeViewer().expandToLevel(stateDef, 1);
                     getTreeViewer().setSelection(newSelection);
                  }
               } catch (OseeStateException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      } else if (input instanceof WorkDefinitionViewer) {
         if (getTreeViewer() != null) {
            workDefViewer = (WorkDefinitionViewer) input;
            getTreeViewer().setInput(workDefViewer);
            getTreeViewer().expandToLevel(workDefViewer, 2);
         }
      }

      isTeamWf =
         (workflowEditor != null && workflowEditor.getWorkItem() != null && workflowEditor.getWorkItem().isOfType(
            AtsArtifactTypes.TeamWorkflow)) || //
            (workDefViewer != null && workDefViewer.getWorkDef() != null && workDefViewer.getWorkDef().getName().contains(
               "_Team_"));

   }

   public void refresh() {
      TreeViewer viewer = getTreeViewer();
      if (viewer != null && Widgets.isAccessible(viewer.getTree())) {
         viewer.refresh();
      }
   }

   private class InternalContentProvider implements ITreeContentProvider {

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
         // do nothing
      }

      @Override
      public Object[] getChildren(Object element) {
         List<Object> items = new ArrayList<>();
         if (element instanceof WorkDefinitionViewer) {
            items.add(((WorkDefinitionViewer) element).getWorkDef());
         } else if (element instanceof WrappedChangeTypes) {
            items.addAll(((WrappedChangeTypes) element).getTypes());
         } else if (element instanceof WorkflowEditor) {
            items.add(((WorkflowEditor) element).getWorkItem());
            items.add(new WrappedStateItems(AtsApiService.get().getWorkItemService().getWorkItemHooks()));
         } else if (element instanceof AbstractWorkflowArtifact) {
            items.add(((AbstractWorkflowArtifact) element).getWorkDefinition());
         } else if (element instanceof WrappedLayout) {
            items.addAll(((WrappedLayout) element).getStateItems());
         } else if (element instanceof WrappedPercentWeight) {
            getChildrenFromWrappedPercentDefinition((WrappedPercentWeight) element, items);
         } else if (element instanceof WorkDefinition) {
            getChildrenFromWorkDefinition((WorkDefinition) element, items);
         } else if (element instanceof StateDefinition) {
            getChildrenFromStateDefinition(element, items);
         } else if (element instanceof CompositeLayoutItem) {
            items.addAll(((CompositeLayoutItem) element).getaLayoutItems());
         } else if (element instanceof User) {
            items.add("Assignee: " + ((User) element).getName());
         } else if (element instanceof WrappedStateItems) {
            items.addAll(((WrappedStateItems) element).getStateItems());
         } else if (element instanceof IAtsWorkItemHook) {
            items.add("Description: " + ((IAtsWorkItemHook) element).getDescription());
            items.add("Full Name: " + ((IAtsWorkItemHook) element).getFullName());
         } else if (element instanceof WrappedTransitions) {
            items.addAll(((WrappedTransitions) element).getTransitions());
         } else if (element instanceof IAtsDecisionReviewDefinition) {
            getChildrenFromDecisionReviewDefinition(element, items);
         } else if (element instanceof IAtsPeerReviewDefinition) {
            getChildrenFromPeerReviewDefinition(element, items);
         } else if (element instanceof IAtsDecisionReviewOption) {
            getUsersFromDecisionReviewOpt((IAtsDecisionReviewOption) element, items);
         } else if (element instanceof WrappedDecisionReviews) {
            items.addAll(((WrappedDecisionReviews) element).getDecisionReviews());
         } else if (element instanceof WrappedPeerReviews) {
            items.addAll(((WrappedPeerReviews) element).getPeerReviews());
         } else if (element instanceof WrappedRules) {
            items.addAll(((WrappedRules) element).getRuleAndLocations());
         } else if (element instanceof WidgetDefinition) {
            getChildrenFromWidgetDefinition(element, items);
         } else if (element instanceof String) {
            items.add(element);
         } else if (element instanceof WrappedStates) {
            items.addAll(((WrappedStates) element).getStates());
         } else if (element instanceof HeaderDefinition) {
            getChildrentFromHeaderDefinition((HeaderDefinition) element, items);
         }
         return items.toArray(new Object[items.size()]);
      }

      @Override
      public Object getParent(Object element) {
         if (element instanceof AbstractWorkflowArtifact) {
            return workflowEditor;
         } else if (element instanceof WorkDefinition) {
            return workflowEditor != null ? workflowEditor : workDefViewer;
         } else if (element instanceof StateDefinition) {
            return ((StateDefinition) element).getWorkDefinition();
         } else if (element instanceof HeaderDefinition) {
            return ((HeaderDefinition) element).getWorkDefinition();
         } else if (element instanceof WrappedChangeTypes) {
            return workflowEditor.getWorkItem().getWorkDefinition();
         } else if (element instanceof String) {
            return workflowEditor != null ? workflowEditor : workDefViewer;
         }
         return null;
      }

      @Override
      public boolean hasChildren(Object element) {
         if (element instanceof String) {
            return false;
         } else if (element instanceof AbstractWorkflowArtifact) {
            return true;
         } else if (element instanceof WorkDefinition) {
            return true;
         } else if (element instanceof StateDefinition) {
            return true;
         } else if (element instanceof CompositeLayoutItem) {
            return true;
         } else if (element instanceof IAtsWorkItemHook) {
            return true;
         } else if (element instanceof WidgetDefinition) {
            return true;
         } else if (element instanceof IAtsPeerReviewDefinition) {
            return true;
         } else if (element instanceof IAtsDecisionReviewDefinition) {
            return true;
         } else if (element instanceof IAtsDecisionReviewOption) {
            return !((IAtsDecisionReviewOption) element).getUserIds().isEmpty();
         } else if (element instanceof WrappedTransitions) {
            return true;
         } else if (element instanceof WrappedPercentWeight) {
            try {
               return AtsApiService.get().getWorkDefinitionService().isStateWeightingEnabled(
                  ((WrappedPercentWeight) element).getWorkDef());
            } catch (OseeStateException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return false;
         } else if (element instanceof WrappedChangeTypes) {
            return !((WrappedChangeTypes) element).getTypes().isEmpty();
         } else if (element instanceof WrappedLayout) {
            return !((WrappedLayout) element).stateItems.isEmpty();
         } else if (element instanceof WrappedDecisionReviews) {
            return !((WrappedDecisionReviews) element).decReviews.isEmpty();
         } else if (element instanceof WrappedPeerReviews) {
            return !((WrappedPeerReviews) element).decReviews.isEmpty();
         } else if (element instanceof WrappedStateItems) {
            return !((WrappedStateItems) element).workflowHooks.isEmpty();
         } else if (element instanceof WrappedStates) {
            if (((WrappedStates) element).states != null) {
               return !((WrappedStates) element).states.isEmpty();
            }
         } else if (element instanceof RuleAndLocation) {
            return false;
         } else if (element instanceof WrappedRules) {
            return !((WrappedRules) element).getRuleAndLocations().isEmpty();
         } else if (element instanceof HeaderDefinition) {
            return true;
         }
         return false;
      }

      private void getChildrenFromWrappedPercentDefinition(WrappedPercentWeight weightDef, List<Object> items) {
         try {
            for (StateDefinition stateDef : AtsApiService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
               weightDef.getWorkDef())) {
               items.add(String.format("State [%s]: %d", stateDef.getName(), stateDef.getStateWeight()));
            }
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      private void getChildrenFromWidgetDefinition(Object element, List<Object> items) {
         items.add("XWidget: " + ((WidgetDefinition) element).getXWidgetName());
         if (((WidgetDefinition) element).getAttributeType() != null) {
            items.add("Attribute Name: " + ((WidgetDefinition) element).getAttributeType().getName());
         }
         if (Strings.isValid(((WidgetDefinition) element).getDescription())) {
            items.add("Description: " + ((WidgetDefinition) element).getDescription());
         }
         if (((WidgetDefinition) element).getHeight() > 0) {
            items.add("Height: " + ((WidgetDefinition) element).getHeight());
         }
         if (((WidgetDefinition) element).getAttributeType() != null) {
            items.add("Tooltip: " + ((WidgetDefinition) element).getAttributeType().getName());
         }
         if (!((WidgetDefinition) element).getOptions().getXOptions().isEmpty()) {
            items.addAll(((WidgetDefinition) element).getOptions().getXOptions());
         }
      }

      private void getChildrenFromPeerReviewDefinition(Object element, List<Object> items) {
         if (Strings.isValid(((IAtsPeerReviewDefinition) element).getReviewTitle())) {
            items.add("Title: " + ((IAtsPeerReviewDefinition) element).getReviewTitle());
         }
         if (Strings.isValid(((IAtsPeerReviewDefinition) element).getDescription())) {
            items.add("Description: " + ((IAtsPeerReviewDefinition) element).getDescription());
         }
         if (Strings.isValid(((IAtsPeerReviewDefinition) element).getLocation())) {
            items.add("Description: " + ((IAtsPeerReviewDefinition) element).getLocation());
         }
         items.add("On Event: " + ((IAtsPeerReviewDefinition) element).getStateEventType().name());
         items.add("Related To State: " + ((IAtsPeerReviewDefinition) element).getRelatedToState());
         items.add("Review Blocks: " + ((IAtsPeerReviewDefinition) element).getBlockingType().name());
         for (String userId : ((IAtsPeerReviewDefinition) element).getAssignees()) {
            try {
               items.add(AtsApiService.get().getUserService().getUserByUserId(userId));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               items.add(String.format("Exception loading user from id [%s] [%s]", userId, ex.getLocalizedMessage()));
            }
         }
      }

      private void getChildrenFromDecisionReviewDefinition(Object element, List<Object> items) {
         if (Strings.isValid(((IAtsDecisionReviewDefinition) element).getReviewTitle())) {
            items.add("Title: " + ((IAtsDecisionReviewDefinition) element).getReviewTitle());
         }
         if (Strings.isValid(((IAtsDecisionReviewDefinition) element).getDescription())) {
            items.add("Description: " + ((IAtsDecisionReviewDefinition) element).getDescription());
         }
         items.add("On Event: " + ((IAtsDecisionReviewDefinition) element).getStateEventType().name());
         items.add("Related To State: " + ((IAtsDecisionReviewDefinition) element).getRelatedToState());
         items.add("Review Blocks: " + ((IAtsDecisionReviewDefinition) element).getBlockingType().name());
         items.add(
            "Auto Transition to Decision: " + ((IAtsDecisionReviewDefinition) element).isAutoTransitionToDecision());
         for (String userId : ((IAtsDecisionReviewDefinition) element).getAssignees()) {
            try {
               items.add(AtsApiService.get().getUserService().getUserByUserId(userId));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               items.add(String.format("Exception loading user from id [%s] [%s]", userId, ex.getLocalizedMessage()));
            }
         }
         items.addAll(((IAtsDecisionReviewDefinition) element).getOptions());
      }

      private void getChildrenFromStateDefinition(Object element, List<Object> items) {
         StateDefinition stateDef = (StateDefinition) element;
         items.add("Ordinal: " + stateDef.getOrdinal());
         if (Strings.isValid(stateDef.getDescription())) {
            items.add("Description: " + stateDef.getDescription());
         }
         items.add(new WrappedLayout(stateDef.getLayoutItems()));
         items.add(new WrappedRules(stateDef));
         if (stateDef.getRecommendedPercentComplete() == null) {
            items.add("Recommended Percent Complete: not set");
         } else {
            items.add("Recommended Percent Complete: " + stateDef.getRecommendedPercentComplete());
         }
         items.add("Color: " + (stateDef.getColor() == null ? "not set" : stateDef.getColor().toString()));
         if (isTeamWf) {
            items.add(new WrappedDecisionReviews(stateDef.getDecisionReviews()));
            items.add(new WrappedPeerReviews(stateDef.getPeerReviews()));
         }
         items.add(new WrappedTransitions(stateDef));
      }

      private void getChildrentFromHeaderDefinition(HeaderDefinition headerDef, List<Object> items) {
         try {
            items.add("Show Metrics Header: " + headerDef.isShowMetricsHeader());
            items.add("Show Work Package Header: " + headerDef.isShowWorkPackageHeader());
            items.add("Show Sibling Links: " + headerDef.isShowSiblingLinks());
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         try {
            items.add(new WrappedLayout(headerDef.getLayoutItems()));
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      private void getChildrenFromWorkDefinition(WorkDefinition workDef, List<Object> items) {
         items.add(new WrappedChangeTypes(workDef));
         try {
            items.add(workDef.getHeaderDef());
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         try {
            items.addAll(AtsApiService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(workDef));
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         items.add("Show State Metrics: " + workDef.isShowStateMetrics());
         items.add(new WrappedPercentWeight(workDef));
      }

      private void getUsersFromDecisionReviewOpt(IAtsDecisionReviewOption revOpt, List<Object> items) {
         for (String userId : revOpt.getUserIds()) {
            try {
               items.add(AtsApiService.get().getUserService().getUserByUserId(userId));
            } catch (OseeCoreException ex) {
               items.add(String.format("Erroring getting user by id [%s] : [%s]", userId, ex.getLocalizedMessage()));
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         for (String userName : revOpt.getUserNames()) {
            try {
               items.add(AtsApiService.get().getUserService().getUserByName(userName));
            } catch (OseeCoreException ex) {
               items.add(
                  String.format("Erroring getting user by name [%s] : [%s]", userName, ex.getLocalizedMessage()));
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

      @Override
      public Object[] getElements(Object inputElement) {
         return getChildren(inputElement);
      }
   }

   private class WrappedRules {
      private final StateDefinition stateDef;

      public WrappedRules(StateDefinition stateDef) {
         this.stateDef = stateDef;
      }

      @Override
      public String toString() {
         return "Rules" + (getRuleAndLocations().isEmpty() ? " (Empty)" : "");
      }

      public Collection<RuleAndLocation> getRuleAndLocations() {
         List<RuleAndLocation> result = new ArrayList<>();
         // get rules from stateDef
         for (String ruleDef : stateDef.getRules()) {
            result.add(new RuleAndLocation(ruleDef, "State Definition"));
         }
         // add rules from Team Definition
         if (workflowEditor != null && workflowEditor.getWfeInput().getArtifact().isOfType(
            AtsArtifactTypes.TeamWorkflow)) {
            try {
               IAtsTeamDefinition teamDef =
                  ((TeamWorkFlowArtifact) workflowEditor.getWfeInput().getArtifact()).getTeamDefinition();
               for (String workRuleDef : AtsApiService.get().getTeamDefinitionService().getRules(teamDef)) {
                  String location = String.format("Team Definition [%s]", teamDef);
                  result.add(new RuleAndLocation(workRuleDef, location));
                  if (workRuleDef.startsWith("ats")) {
                     result.add(new RuleAndLocation(workRuleDef.replaceFirst("^ats", ""),
                        location + " translated from WorkRuleDefinition starting with ats"));
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         return result;
      }
   }

   private class RuleAndLocation {
      private final String rule;
      private final String location;

      public RuleAndLocation(String rule, String location) {
         this.rule = rule;
         this.location = location;
      }

      @Override
      public String toString() {
         return String.format("%s [%s]", rule, location);
      }
   }

   private class WrappedChangeTypes {
      private final String name = "Change Types";
      private final WorkDefinition workDef;

      public WrappedChangeTypes(WorkDefinition workDef) {
         this.workDef = workDef;
      }

      @Override
      public String toString() {
         if (workDef.getChangeTypes() != null) {
            return name + (workDef.getChangeTypes().isEmpty() ? " (Empty)" : "");
         } else {
            return name;
         }
      }

      public Collection<ChangeTypes> getTypes() {
         return workDef.getChangeTypes();
      }
   }

   private class WrappedStates {
      private final String name;
      private final Collection<StateDefinition> states;

      public WrappedStates(String name, Collection<StateDefinition> states) {
         this.name = name;
         this.states = states;
      }

      @Override
      public String toString() {
         if (states != null) {
            return name + (states.isEmpty() ? " (Empty)" : "");
         } else {
            return name;
         }
      }

      public Collection<StateDefinition> getStates() {
         return states;
      }
   }

   private class WrappedPercentWeight {

      private final WorkDefinition workDef;

      public WrappedPercentWeight(WorkDefinition workDef) {
         this.workDef = workDef;
      }

      @Override
      public String toString() {
         try {
            if (AtsApiService.get().getWorkDefinitionService().isStateWeightingEnabled(workDef)) {
               return "Total Percent Weighting";
            } else {
               return "Total Percent Weighting: Single Percent";
            }
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return "Total Percent Weighting: exception (see error log)";
      }

      public WorkDefinition getWorkDef() {
         return workDef;
      }

   }

   private class WrappedDecisionReviews {
      private final Collection<IAtsDecisionReviewDefinition> decReviews;

      public WrappedDecisionReviews(Collection<IAtsDecisionReviewDefinition> decReviews) {
         this.decReviews = decReviews;
      }

      @Override
      public String toString() {
         return "Decision Reviews" + (decReviews.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsDecisionReviewDefinition> getDecisionReviews() {
         return decReviews;
      }

   }

   private class WrappedStateItems {
      private final Collection<IAtsWorkItemHook> workflowHooks;

      public WrappedStateItems(Collection<IAtsWorkItemHook> stateItems) {
         this.workflowHooks = stateItems;
      }

      @Override
      public String toString() {
         return "Workflow Hooks" + (workflowHooks.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsWorkItemHook> getStateItems() {
         return workflowHooks;
      }

   }

   private class WrappedPeerReviews {
      private final Collection<IAtsPeerReviewDefinition> decReviews;

      public WrappedPeerReviews(Collection<IAtsPeerReviewDefinition> decReviews) {
         this.decReviews = decReviews;
      }

      @Override
      public String toString() {
         return "Peer Reviews" + (decReviews.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsPeerReviewDefinition> getPeerReviews() {
         return decReviews;
      }

   }

   private class WrappedLayout {
      private final Collection<LayoutItem> stateItems;

      public WrappedLayout(Collection<LayoutItem> stateItems) {
         this.stateItems = stateItems;
      }

      @Override
      public String toString() {
         return "Layout" + (stateItems.isEmpty() ? " (Empty)" : "");
      }

      public Collection<LayoutItem> getStateItems() {
         return stateItems;
      }

   }

   private class WrappedTransitions {

      private final StateDefinition stateDef;

      public WrappedTransitions(StateDefinition stateDef) {
         this.stateDef = stateDef;
      }

      public Collection<Object> getTransitions() {
         List<Object> items = new ArrayList<>();
         if (stateDef.getToStates().isEmpty()) {
            items.add(new WrappedStates("DefaultToState: None", null));
         } else {
            items.add(new WrappedStates("DefaultToState: " + stateDef.getToStates().iterator().next(), null));
         }
         items.add(new WrappedStates("ToStates", stateDef.getToStates()));
         return items;
      }

      @Override
      public String toString() {
         return "Transitions" + (stateDef.getToStates().isEmpty() ? " (Empty)" : "");
      }

   }

   public void setWorkDefViewer(WorkDefinitionViewer workDefViewer) {
      this.workDefViewer = workDefViewer;
   }

   private class InternalLabelProvider extends LabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof WorkflowEditor) {
            return ((WorkflowEditor) element).getTitle();
         } else if (element instanceof WorkDefinitionViewer) {
            return ((WorkDefinitionViewer) element).getName();
         } else if (element instanceof ChangeTypes) {
            ChangeTypes changeTypes = (ChangeTypes) element;
            String desc = changeTypes.getDescription();
            if (Strings.isValid(desc)) {
               return String.format("%s - %s", changeTypes.name(), desc);
            }
            return changeTypes.name();
         }
         return String.valueOf(element);
      }

      @Override
      public Image getImage(Object element) {
         if (element instanceof WorkDefinitionViewer) {
            return ImageManager.getImage(AtsImage.WORKFLOW);
         } else if (element instanceof WorkflowEditor) {
            return ((WorkflowEditor) element).getTitleImage();
         } else if (element instanceof AbstractWorkflowArtifact) {
            return ArtifactImageManager.getImage((AbstractWorkflowArtifact) element);
         } else if (element instanceof StateDefinition) {
            return ImageManager.getImage(AtsImage.STATE_DEFINITION);
         } else if (element instanceof IAtsWorkItemHook || element instanceof WrappedStateItems) {
            return ImageManager.getImage(AtsImage.STATE_ITEM);
         } else if (element instanceof WorkDefinition) {
            return ImageManager.getImage(AtsImage.WORKFLOW);
         } else if (element instanceof WidgetDefinition) {
            return ImageManager.getImage(FrameworkImage.GEAR);
         } else if (element instanceof CompositeLayoutItem || element instanceof WrappedLayout) {
            return ImageManager.getImage(AtsImage.COMPOSITE_STATE_ITEM);
         } else if (element instanceof String || element instanceof WidgetOption || element instanceof WrappedPercentWeight) {
            return ImageManager.getImage(AtsImage.RIGHT_ARROW_SM);
         } else if (element instanceof WrappedStates || element instanceof WrappedTransitions) {
            return ImageManager.getImage(AtsImage.TRANSITION);
         } else if (element instanceof WrappedRules || element instanceof RuleAndLocation) {
            return ImageManager.getImage(FrameworkImage.RULE);
         } else if (element instanceof User) {
            return ImageManager.getImage(FrameworkImage.USER);
         } else if (element instanceof WrappedPeerReviews || element instanceof IAtsPeerReviewDefinition) {
            return ImageManager.getImage(AtsImage.PEER_REVIEW);
         } else if (element instanceof WrappedDecisionReviews || element instanceof IAtsDecisionReviewDefinition) {
            return ImageManager.getImage(AtsImage.DECISION_REVIEW);
         } else if (element instanceof IAtsDecisionReviewOption) {
            return ImageManager.getImage(FrameworkImage.QUESTION);
         } else if (element instanceof HeaderDefinition) {
            return ImageManager.getImage(AtsImage.WORKFLOW_DEFINITION);
         } else if (element instanceof WrappedChangeTypes) {
            return ImageManager.getImage(AtsImage.CHANGE_REQUEST);
         } else if (element instanceof ChangeTypes) {
            return ImageManager.getImage(AtsImage.CHANGE_REQUEST);
         }
         return null;
      }
   }

}