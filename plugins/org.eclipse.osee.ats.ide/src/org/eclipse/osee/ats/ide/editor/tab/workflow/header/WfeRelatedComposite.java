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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeRelatedComposite extends Composite implements XModifiedListener {

   private final WorkflowEditor editor;
   private final IAtsWorkItem workItem;
   private XRelatedWidget siblingWidget, tasksWidget, reviewsWidget, parentTeamWfWidget, parentActionWfWidget,
      supportingWidget, derivedFromWidget, derivedToWidget;
   private final AtsApi atsApi;
   private final String SPACE = " ";

   public WfeRelatedComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
      this.workItem = editor.getWorkItem();
      this.atsApi = AtsApiService.get();
   }

   public void create() {
      setLayout(ALayout.getZeroMarginLayout(15, false));
      GridData gd = new GridData(SWT.NONE, SWT.NONE, false, false);
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      createRelated();

      refresh();

      layout(true, true);
      getParent().layout(true, true);
      editor.getWorkFlowTab().getManagedForm().reflow(true);
   }

   private void createRelated() {
      Label label = new Label(this, SWT.NONE);
      label.setText("Related: ");
      label.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      label.setFont(FontManager.getDefaultLabelFont());

      IAtsAction action = workItem.getParentAction();
      if (action != null) {
         parentActionWfWidget = new XRelatedWidget("Parent Action", this);
         parentActionWfWidget.setToolkit(editor.getToolkit());
         parentActionWfWidget.setToolTip("Select to Open");
         parentActionWfWidget.setIncludeColon(false);
         parentActionWfWidget.createWidgets(this, 2);

         addSpaceWidget();
      }
      if (workItem.isTask() || workItem.isReview()) {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            parentTeamWfWidget = new XRelatedWidget("Parent Team WF", this);
            parentTeamWfWidget.setIncludeColon(false);
            parentTeamWfWidget.setToolTip("Select to Open");
            parentTeamWfWidget.setToolkit(editor.getToolkit());
            parentTeamWfWidget.createWidgets(this, 2);

            addSpaceWidget();
         }
      }
      if (workItem.isTeamWorkflow()) {
         siblingWidget = new XRelatedWidget("Siblings: ", this);
         siblingWidget.setToolkit(editor.getToolkit());
         siblingWidget.setToolTip("Select to Open");
         siblingWidget.createWidgets(this, 2);

         addSpaceWidget();

         tasksWidget = new XRelatedWidget("Tasks: ", this);
         tasksWidget.setToolkit(editor.getToolkit());
         tasksWidget.setToolTip("Select to Open");
         tasksWidget.createWidgets(this, 2);

         addSpaceWidget();

         reviewsWidget = new XRelatedWidget("Reviews: ", this);
         reviewsWidget.setToolkit(editor.getToolkit());
         reviewsWidget.setToolTip("Select to Open");
         reviewsWidget.createWidgets(this, 2);

         addSpaceWidget();

      }

      supportingWidget = new XRelatedWidget("Supporting: ", this);
      supportingWidget.setToolkit(editor.getToolkit());
      supportingWidget.setToolTip("Select to Open");
      supportingWidget.createWidgets(this, 2);

      addSpaceWidget();

      Collection<ArtifactToken> derivedFrom =
         atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_From);
      if (!derivedFrom.isEmpty()) {
         derivedFromWidget = new XRelatedWidget("Derived From: ", this);
         derivedFromWidget.setToolkit(editor.getToolkit());
         derivedFromWidget.setToolTip("Select to Open");
         derivedFromWidget.createWidgets(this, 2);

         addSpaceWidget();
      }

      Collection<ArtifactToken> derived = atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_To);
      if (!derived.isEmpty()) {
         derivedToWidget = new XRelatedWidget("Derived: ", this);
         derivedToWidget.setToolkit(editor.getToolkit());
         derivedToWidget.setToolTip("Select to Open");
         derivedToWidget.createWidgets(this, 2);

         addSpaceWidget();
      }

   }

   private void addSpaceWidget() {
      XLabel label = new XLabel(SPACE);
      label.createWidgets(this, 1);
      label.adaptControls(editor.getWorkFlowTab().getManagedForm().getToolkit());
   }

   @Override
   public void widgetModified(XWidget widget) {
      if (parentActionWfWidget != null && widget.equals(parentActionWfWidget)) {
         IAtsAction action = workItem.getParentAction();
         if (action != null) {
            (new OpenInAtsWorldAction((AbstractWorkflowArtifact) workItem)).run();
         }
      }
      if (parentTeamWfWidget != null && widget.equals(parentTeamWfWidget)) {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         WorkflowEditor.edit(teamWf);
      }
      if (siblingWidget != null && widget.equals(siblingWidget)) {
         (new OpenInAtsWorldAction((AbstractWorkflowArtifact) workItem)).run();
      }
      if (tasksWidget != null && widget.equals(tasksWidget)) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem);
         if (tasks.isEmpty()) {
            AWorkbench.popup("No Tasks Found");
            return;
         }
         TaskEditor.open(
            new TaskEditorSimpleProvider("Tasks for " + workItem.toStringWithAtsId(), Collections.castAll(tasks)));
      }
      if (reviewsWidget != null && widget.equals(reviewsWidget)) {
         Collection<IAtsAbstractReview> reviews = atsApi.getReviewService().getReviews((IAtsTeamWorkflow) workItem);
         if (reviews.isEmpty()) {
            AWorkbench.popup("No Reviews Found");
            return;
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider("Reviews for " + workItem.toStringWithAtsId(),
               Collections.castAll(reviews)));
         }
      }
      if (supportingWidget != null && widget.equals(supportingWidget)) {
         Map<IRelationLink, Artifact> supporting = getSupporting();
         if (supporting.isEmpty()) {
            AWorkbench.popup("No Supporting Items Found");
            return;
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider("Supporting for " + workItem.toStringWithAtsId(),
               Collections.castAll(supporting.values())));
         }
      }
      if (derivedFromWidget != null && widget.equals(derivedFromWidget)) {
         Collection<ArtifactToken> derivedFrom =
            atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_From);
         if (derivedFrom.isEmpty()) {
            AWorkbench.popup("No Derived From Found");
            return;
         } else {
            if (derivedFrom.size() == 1) {
               WorkflowEditor.edit((IAtsWorkItem) derivedFrom.iterator().next());
            } else {
               WorldEditor.open(new WorldEditorSimpleProvider("Derived From " + workItem.toStringWithAtsId(),
                  Collections.castAll(derivedFrom)));
            }
         }
      }
      if (derivedToWidget != null && widget.equals(derivedToWidget)) {
         Collection<ArtifactToken> derivedTo =
            atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_To);
         if (derivedTo.isEmpty()) {
            AWorkbench.popup("No Derived To Found");
            return;
         } else {
            if (derivedTo.size() == 1) {
               WorkflowEditor.edit((IAtsWorkItem) derivedTo.iterator().next());
            } else {
               WorldEditor.open(new WorldEditorSimpleProvider("Derived To From " + workItem.toStringWithAtsId(),
                  Collections.castAll(derivedTo)));
            }
         }
      }
   }

   private Map<IRelationLink, Artifact> getSupporting() {
      Set<IRelationLink> supportingLink = new HashSet<>();
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(workItem.getStoreObject(),
         CoreRelationTypes.SupportingInfo_SupportingInfo));
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(workItem.getStoreObject(),
         CoreRelationTypes.SupportingInfo_IsSupportedBy));
      Map<IRelationLink, Artifact> supporting = new HashMap<>();
      for (IRelationLink iLink : supportingLink) {
         RelationLink link = (RelationLink) iLink;
         if (workItem.getArtifactId().equals(link.getArtifactIdA())) {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactB());
            supporting.put(link, otherArt);
         } else {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactA());
            supporting.put(link, otherArt);
         }
      }
      return supporting;
   }

   public Collection<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      if (siblingWidget != null) {
         widgets.add(siblingWidget);
      }
      if (tasksWidget != null) {
         widgets.add(tasksWidget);
      }
      if (reviewsWidget != null) {
         widgets.add(reviewsWidget);
      }
      if (parentActionWfWidget != null) {
         widgets.add(parentActionWfWidget);
      }
      if (parentTeamWfWidget != null) {
         widgets.add(parentTeamWfWidget);
      }
      if (siblingWidget != null) {
         widgets.add(siblingWidget);
      }
      return widgets;
   }

   public void refresh() {
      if (Widgets.isAccessible(this)) {

         if (workItem.isTeamWorkflow()) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
            Collection<IAtsTeamWorkflow> workflows = atsApi.getActionService().getSiblingTeamWorkflows(teamWf);
            siblingWidget.setCurrentValue(String.valueOf(workflows.size()));
            siblingWidget.refresh();

            tasksWidget.setCurrentValue(String.valueOf(atsApi.getTaskService().getTasks(teamWf).size()));
            tasksWidget.refresh();

            reviewsWidget.setCurrentValue(String.valueOf(atsApi.getReviewService().getReviews(teamWf).size()));
            reviewsWidget.refresh();
         }
         if (supportingWidget != null) {
            Map<IRelationLink, Artifact> supporting = getSupporting();
            supportingWidget.setCurrentValue(String.valueOf(supporting.size()));
            supportingWidget.refresh();
         }
         if (derivedFromWidget != null) {
            Collection<ArtifactToken> derivedFrom =
               atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_From);
            derivedFromWidget.setCurrentValue(String.valueOf(derivedFrom.size()));
            derivedFromWidget.refresh();
         }
         if (derivedToWidget != null) {
            Collection<ArtifactToken> derivedTo =
               atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Derive_To);
            derivedToWidget.setCurrentValue(String.valueOf(derivedTo.size()));
            derivedToWidget.refresh();
         }
      }
   }

}
