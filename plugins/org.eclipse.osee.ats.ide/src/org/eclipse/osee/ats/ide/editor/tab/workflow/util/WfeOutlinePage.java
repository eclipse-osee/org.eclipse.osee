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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.HeaderDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkflowHook;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
                     IAtsStateDefinition stateDef = WorkflowManager.getCurrentAtsWorkPage(awa).getStateDefinition();
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

   private class InternalLabelProvider extends LabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof WorkflowEditor) {
            return ((WorkflowEditor) element).getTitle();
         } else if (element instanceof WorkDefinitionViewer) {
            return ((WorkDefinitionViewer) element).getName();
         }
         return String.valueOf(element);
      }

      @Override
      public Image getImage(Object element) {
         if (element instanceof WorkDefinitionViewer) {
            return ImageManager.getImage(AtsImage.WORKFLOW_CONFIG);
         } else if (element instanceof WorkflowEditor) {
            return ((WorkflowEditor) element).getTitleImage();
         } else if (element instanceof AbstractWorkflowArtifact) {
            return ArtifactImageManager.getImage((AbstractWorkflowArtifact) element);
         } else if (element instanceof IAtsStateDefinition) {
            return ImageManager.getImage(AtsImage.STATE_DEFINITION);
         } else if (element instanceof IAtsWorkflowHook || element instanceof WrappedStateItems) {
            return ImageManager.getImage(AtsImage.STATE_ITEM);
         } else if (element instanceof IAtsWorkDefinition) {
            return ImageManager.getImage(AtsImage.WORKFLOW_CONFIG);
         } else if (element instanceof IAtsWidgetDefinition) {
            return ImageManager.getImage(FrameworkImage.GEAR);
         } else if (element instanceof IAtsCompositeLayoutItem || element instanceof WrappedLayout) {
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
            return ImageManager.getImage(AtsImage.WORK_DEFINITION);
         }
         return null;
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
            add(items, ((WorkDefinitionViewer) element).getWorkDef());
         } else if (element instanceof WorkflowEditor) {
            add(items, ((WorkflowEditor) element).getWorkItem());
            items.add(new WrappedStateItems(AtsClientService.get().getWorkItemService().getWorkflowHooks()));
         } else if (element instanceof AbstractWorkflowArtifact) {
            add(items, ((AbstractWorkflowArtifact) element).getWorkDefinition());
         } else if (element instanceof WrappedLayout) {
            items.addAll(((WrappedLayout) element).getStateItems());
         } else if (element instanceof WrappedPercentWeight) {
            getChildrenFromWrappedPercentDefinition((WrappedPercentWeight) element, items);
         } else if (element instanceof IAtsWorkDefinition) {
            getChildrenFromWorkDefinition((IAtsWorkDefinition) element, items);
         } else if (element instanceof IAtsStateDefinition) {
            getChildrenFromStateDefinition(element, items);
         } else if (element instanceof IAtsCompositeLayoutItem) {
            items.addAll(((IAtsCompositeLayoutItem) element).getaLayoutItems());
         } else if (element instanceof User) {
            items.add("Assignee: " + ((User) element).getName());
         } else if (element instanceof WrappedStateItems) {
            items.addAll(((WrappedStateItems) element).getStateItems());
         } else if (element instanceof IAtsWorkflowHook) {
            items.add("Description: " + ((IAtsWorkflowHook) element).getDescription());
            items.add("Full Name: " + ((IAtsWorkflowHook) element).getFullName());
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
         } else if (element instanceof IAtsWidgetDefinition) {
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

      private void add(Collection<Object> items, Object toAdd) {
         if (toAdd != null) {
            items.add(toAdd);
         }
      }

      @Override
      public Object getParent(Object element) {
         if (element instanceof AbstractWorkflowArtifact) {
            return workflowEditor;
         } else if (element instanceof IAtsWorkDefinition) {
            return workflowEditor != null ? workflowEditor : workDefViewer;
         } else if (element instanceof IAtsStateDefinition) {
            return ((IAtsStateDefinition) element).getWorkDefinition();
         } else if (element instanceof HeaderDefinition) {
            return ((HeaderDefinition) element).getWorkDefinition();
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
         } else if (element instanceof IAtsWorkDefinition) {
            return true;
         } else if (element instanceof IAtsStateDefinition) {
            return true;
         } else if (element instanceof IAtsCompositeLayoutItem) {
            return true;
         } else if (element instanceof IAtsWorkflowHook) {
            return true;
         } else if (element instanceof IAtsWidgetDefinition) {
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
               return AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(
                  ((WrappedPercentWeight) element).getWorkDef());
            } catch (OseeStateException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return false;
         } else if (element instanceof WrappedLayout) {
            return !((WrappedLayout) element).stateItems.isEmpty();
         } else if (element instanceof WrappedDecisionReviews) {
            return !((WrappedDecisionReviews) element).decReviews.isEmpty();
         } else if (element instanceof WrappedPeerReviews) {
            return !((WrappedPeerReviews) element).decReviews.isEmpty();
         } else if (element instanceof WrappedStateItems) {
            return !((WrappedStateItems) element).workflowHooks.isEmpty();
         } else if (element instanceof WrappedStates) {
            return !((WrappedStates) element).states.isEmpty();
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
            for (IAtsStateDefinition stateDef : AtsClientService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
               weightDef.getWorkDef())) {
               items.add(String.format("State [%s]: %d", stateDef.getName(), stateDef.getStateWeight()));
            }
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      private void getChildrenFromWidgetDefinition(Object element, List<Object> items) {
         items.add("XWidget: " + ((IAtsWidgetDefinition) element).getXWidgetName());
         if (((IAtsWidgetDefinition) element).getAttributeType() != null) {
            items.add("Attribute Name: " + ((IAtsWidgetDefinition) element).getAttributeType().getName());
         }
         if (Strings.isValid(((IAtsWidgetDefinition) element).getDescription())) {
            items.add("Description: " + ((IAtsWidgetDefinition) element).getDescription());
         }
         if (((IAtsWidgetDefinition) element).getHeight() > 0) {
            items.add("Height: " + ((IAtsWidgetDefinition) element).getHeight());
         }
         if (((IAtsWidgetDefinition) element).getAttributeType() != null) {
            items.add("Tooltip: " + ((IAtsWidgetDefinition) element).getAttributeType().getName());
         }
         if (!((IAtsWidgetDefinition) element).getOptions().getXOptions().isEmpty()) {
            items.addAll(((IAtsWidgetDefinition) element).getOptions().getXOptions());
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
               add(items, AtsClientService.get().getUserService().getUserById(userId));
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
               add(items, AtsClientService.get().getUserService().getUserById(userId));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               items.add(String.format("Exception loading user from id [%s] [%s]", userId, ex.getLocalizedMessage()));
            }
         }
         items.addAll(((IAtsDecisionReviewDefinition) element).getOptions());
      }

      private void getChildrenFromStateDefinition(Object element, List<Object> items) {
         IAtsStateDefinition stateDef = (IAtsStateDefinition) element;
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

      private void getChildrenFromWorkDefinition(IAtsWorkDefinition workDef, List<Object> items) {
         try {
            items.add(workDef.getHeaderDef());
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         try {
            items.addAll(AtsClientService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(workDef));
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         items.add("Show State Metrics: " + workDef.isShowStateMetrics());
         items.add(new WrappedPercentWeight(workDef));
      }

      private void getUsersFromDecisionReviewOpt(IAtsDecisionReviewOption revOpt, List<Object> items) {
         for (String userId : revOpt.getUserIds()) {
            try {
               IAtsUser user = AtsClientService.get().getUserService().getUserById(userId);
               add(items, user);
            } catch (OseeCoreException ex) {
               items.add(String.format("Erroring getting user by id [%s] : [%s]", userId, ex.getLocalizedMessage()));
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         for (String userName : revOpt.getUserNames()) {
            try {
               IAtsUser user = AtsClientService.get().getUserService().getUserByName(userName);
               add(items, user);
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
      private final IAtsStateDefinition stateDef;

      public WrappedRules(IAtsStateDefinition stateDef) {
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
               for (String workRuleDef : AtsClientService.get().getTeamDefinitionService().getRules(teamDef)) {
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

   private class WrappedStates {
      private final String name;
      private final Collection<IAtsStateDefinition> states;

      public WrappedStates(String name, Collection<IAtsStateDefinition> states) {
         this.name = name;
         this.states = states;
      }

      @Override
      public String toString() {
         return name + (states.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsStateDefinition> getStates() {
         return states;
      }

   }
   private class WrappedPercentWeight {

      private final IAtsWorkDefinition workDef;

      public WrappedPercentWeight(IAtsWorkDefinition workDef) {
         this.workDef = workDef;
      }

      @Override
      public String toString() {
         try {
            if (AtsClientService.get().getWorkDefinitionService().isStateWeightingEnabled(workDef)) {
               return "Total Percent Weighting";
            } else {
               return "Total Percent Weighting: Single Percent";
            }
         } catch (OseeStateException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return "Total Percent Weighting: exception (see error log)";
      }

      public IAtsWorkDefinition getWorkDef() {
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
      private final Collection<IAtsWorkflowHook> workflowHooks;

      public WrappedStateItems(Collection<IAtsWorkflowHook> stateItems) {
         this.workflowHooks = stateItems;
      }

      @Override
      public String toString() {
         return "Workflow Hooks" + (workflowHooks.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsWorkflowHook> getStateItems() {
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
      private final Collection<IAtsLayoutItem> stateItems;

      public WrappedLayout(Collection<IAtsLayoutItem> stateItems) {
         this.stateItems = stateItems;
      }

      @Override
      public String toString() {
         return "Layout" + (stateItems.isEmpty() ? " (Empty)" : "");
      }

      public Collection<IAtsLayoutItem> getStateItems() {
         return stateItems;
      }

   }

   private class WrappedTransitions {

      private final IAtsStateDefinition stateDef;

      public WrappedTransitions(IAtsStateDefinition stateDef) {
         this.stateDef = stateDef;
      }

      public Collection<Object> getTransitions() {
         List<IAtsStateDefinition> defaultToStates = new ArrayList<>();
         if (stateDef.getDefaultToState() != null) {
            defaultToStates.add(stateDef.getDefaultToState());
         }
         List<Object> items = new ArrayList<>();
         items.add(new WrappedStates("DefaultToState", defaultToStates));
         items.add(new WrappedStates("ToStates", stateDef.getToStates()));
         items.add(new WrappedStates("OverrideAttrValidationStates", stateDef.getOverrideAttributeValidationStates()));
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

}