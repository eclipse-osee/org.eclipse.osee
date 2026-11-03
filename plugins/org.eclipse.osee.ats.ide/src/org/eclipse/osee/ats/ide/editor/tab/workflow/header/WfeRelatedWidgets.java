/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXRelatedWfeWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class WfeRelatedWidgets implements XModifiedListener {

   private XXRelatedWfeWidget siblingWidget, tasksWidget, reviewsWidget, parentTeamWfWidget, parentActionWfWidget,
      supportingWidget, derivedFromWidget, derivedToWidget;
   private final Long SIB_ID = 2341L;
   private final Long TASK_ID = 2342L;
   private final Long REV_ID = 2343L;
   private final Long PTEAMWf_ID = 2344L;
   private final Long PACTION_ID = 2345L;
   private final Long SUPP_ID = 2346L;
   private final Long DER_FROM_ID = 2347L;
   private final Long DER_TO_ID = 2348L;
   private final WorkflowEditor editor;
   private final AtsApi atsApi;
   private final boolean loaded = false;
   private final XXWorkItemData xxWid;
   private final List<XWidget> widgets = new ArrayList<>();

   public WfeRelatedWidgets(WorkflowEditor editor) {
      this.editor = editor;
      this.xxWid = XXWorkItemData.get((IAtsWorkItem) editor.getWorkItem());
      this.atsApi = AtsApiService.get();
   }

   public void create(XWidgetBuilderAts wba) {
      wba.andLabelWidget("Related").and(XOption.LABEL_BOLD, XOption.COLON).startComp(15);

      IAtsAction action = xxWid.getAction();
      if (action != null) {
         // Parent Action / Parent TeamWf are navigation-only (click opens the parent); no value. noValue() skips the
         // value control entirely so it doesn't reserve width - keeps the Related composite narrower.
         wba.andWidget(PACTION_ID, "Parent Action", XXRelatedWfeWidget).noClear().andListener(this).noValue().and(
            XOption.NO_COLON).andOseeImage(AtsImage.ACTION);
      }
      if (xxWid.getWorkItem().isTask() || xxWid.getWorkItem().isReview()) {
         IAtsTeamWorkflow teamWf = xxWid.getParentTeamWf();
         if (teamWf != null) {
            wba.andWidget(PTEAMWf_ID, "Parent TeamWf", XXRelatedWfeWidget).noClear().andListener(this).noValue().and(
               XOption.NO_COLON).andOseeImage(AtsImage.WORKFLOW);
         }
      }
      if (xxWid.isTeamWf()) {
         wba.andWidget(SIB_ID, "Siblings", XXRelatedWfeWidget).andListener(this).noClear().andOseeImage(
            AtsImage.SIBLINGS);
         wba.andSpaceWidget();

         wba.andWidget(TASK_ID, "Tasks", XXRelatedWfeWidget).andListener(this).noClear().andOseeImage(AtsImage.TASK);
         wba.andSpaceWidget();

         wba.andWidget(REV_ID, "Reviews", XXRelatedWfeWidget).andListener(this).noClear().andOseeImage(AtsImage.REVIEW);
         wba.andSpaceWidget();
      }

      wba.andWidget(SUPP_ID, "Supporting", XXRelatedWfeWidget).andListener(this).noClear().andOseeImage(
         AtsImage.SUPPORTING);
      wba.andSpaceWidget();

      Collection<ArtifactToken> derivedFrom = xxWid.getDerivedFrom();
      if (!derivedFrom.isEmpty()) {
         wba.andWidget(DER_FROM_ID, "Derived From", XXRelatedWfeWidget).noClear().andListener(this);
         wba.andSpaceWidget();
      }

      Collection<ArtifactToken> derived = xxWid.getDerivedTo();
      if (!derived.isEmpty()) {
         wba.andWidget(DER_TO_ID, "Derived", XXRelatedWfeWidget).andListener(this);
         wba.andSpaceWidget();
      }

      wba.endComp();
   }

   @Override
   public void widgetModified(XWidget widget) {
      ensureLoaded();
      if (parentActionWfWidget != null && widget.equals(parentActionWfWidget)) {
         IAtsAction action = xxWid.getAction();
         if (action != null) {
            (new OpenInAtsWorldAction((AbstractWorkflowArtifact) xxWid.getWorkItem())).run();
         }
      }
      if (parentTeamWfWidget != null && widget.equals(parentTeamWfWidget)) {
         IAtsTeamWorkflow teamWf = xxWid.getParentTeamWf();
         WorkflowEditor.edit(teamWf);
      }
      if (siblingWidget != null && widget.equals(siblingWidget)) {
         Collection<IAtsTeamWorkflow> siblings = xxWid.getSiblings();
         if (siblings.isEmpty()) {
            AWorkbench.popup("No Siblings Found");
            return;
         }
         AtsEditors.openInAtsWorldEditor(siblings, "Siblings");
      }
      if (tasksWidget != null && widget.equals(tasksWidget)) {
         Collection<IAtsTask> tasks = xxWid.getTasks();
         if (tasks.isEmpty()) {
            AWorkbench.popup("No Tasks Found");
            return;
         }
         TaskEditor.open(new TaskEditorSimpleProvider("Tasks for " + xxWid.getWorkItem().toStringWithAtsId(),
            Collections.castAll(tasks)));
      }
      if (reviewsWidget != null && widget.equals(reviewsWidget)) {
         Collection<IAtsAbstractReview> reviews = xxWid.getReviews();
         if (reviews.isEmpty()) {
            AWorkbench.popup("No Reviews Found");
            return;
         }
         WorldEditor.open(new WorldEditorSimpleProvider("Reviews for " + xxWid.getWorkItem().toStringWithAtsId(),
            Collections.castAll(reviews)));
      }
      if (supportingWidget != null && widget.equals(supportingWidget)) {
         Map<IRelationLink, Artifact> supporting = getSupporting();
         if (supporting.isEmpty()) {
            AWorkbench.popup("No Supporting Items Found");
            return;
         }
         WorldEditor.open(new WorldEditorSimpleProvider("Supporting for " + xxWid.getWorkItem().toStringWithAtsId(),
            Collections.castAll(supporting.values())));
      }
      if (derivedFromWidget != null && widget.equals(derivedFromWidget)) {
         Collection<ArtifactToken> derivedFrom = xxWid.getDerivedFrom();
         if (derivedFrom.isEmpty()) {
            AWorkbench.popup("No Derived From Found");
            return;
         }
         if (derivedFrom.size() == 1) {
            WorkflowEditor.edit((IAtsWorkItem) derivedFrom.iterator().next());
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider("Derived From " + xxWid.getWorkItem().toStringWithAtsId(),
               Collections.castAll(derivedFrom)));
         }
      }
      if (derivedToWidget != null && widget.equals(derivedToWidget)) {
         Collection<ArtifactToken> derivedTo = xxWid.getDerivedTo();
         if (derivedTo.isEmpty()) {
            AWorkbench.popup("No Derived To Found");
            return;
         }
         if (derivedTo.size() == 1) {
            WorkflowEditor.edit((IAtsWorkItem) derivedTo.iterator().next());
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider("Derived To From " + xxWid.getWorkItem().toStringWithAtsId(),
               Collections.castAll(derivedTo)));
         }
      }
   }

   private Map<IRelationLink, Artifact> getSupporting() {
      Set<IRelationLink> supportingLink = new HashSet<>();
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(xxWid.getWorkItem().getStoreObject(),
         CoreRelationTypes.SupportingInfo_SupportingInfo));
      supportingLink.addAll(atsApi.getRelationResolver().getRelations(xxWid.getWorkItem().getStoreObject(),
         CoreRelationTypes.SupportingInfo_IsSupportedBy));
      Map<IRelationLink, Artifact> supporting = new HashMap<>();
      for (IRelationLink iLink : supportingLink) {
         RelationLink link = (RelationLink) iLink;
         if (xxWid.getWorkItem().getStoreObject().equals(link.getArtifactIdA())) {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactB());
            supporting.put(link, otherArt);
         } else {
            Artifact otherArt = (Artifact) atsApi.getQueryService().getArtifact(link.getArtifactA());
            supporting.put(link, otherArt);
         }
      }
      return supporting;
   }

   public void refresh() {
      if (Widgets.isAccessible(editor.getWorkFlowTab().getHeader())) {
         ensureLoaded();
         if (xxWid.isTeamWf()) {
            IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
            Collection<IAtsTeamWorkflow> workflows = xxWid.getSiblings();
            siblingWidget.setSelected(String.valueOf(workflows.size()));
            siblingWidget.refresh();

            tasksWidget.setSelected(String.valueOf(atsApi.getTaskService().getTasks(teamWf).size()));
            tasksWidget.refresh();

            reviewsWidget.setSelected(String.valueOf(atsApi.getReviewService().getReviews(teamWf).size()));
            reviewsWidget.refresh();
         }
         if (supportingWidget != null) {
            Map<IRelationLink, Artifact> supporting = getSupporting();
            supportingWidget.setSelected(String.valueOf(supporting.size()));
            supportingWidget.refresh();
         }
         if (derivedFromWidget != null) {
            Collection<ArtifactToken> derivedFrom = xxWid.getDerivedFrom();
            derivedFromWidget.setSelected(String.valueOf(derivedFrom.size()));
            derivedFromWidget.refresh();
         }
         if (derivedToWidget != null) {
            Collection<ArtifactToken> derivedTo = xxWid.getDerivedTo();
            derivedToWidget.setSelected(String.valueOf(derivedTo.size()));
            derivedToWidget.refresh();
         }
      }
   }

   private void ensureLoaded() {
      if (!loaded) {
         siblingWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(SIB_ID);
         addAndConfig(siblingWidget);
         tasksWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(TASK_ID);
         addAndConfig(tasksWidget);
         reviewsWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(REV_ID);
         addAndConfig(reviewsWidget);
         parentTeamWfWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(PTEAMWf_ID);
         addAndConfig(parentTeamWfWidget);
         parentActionWfWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(PACTION_ID);
         addAndConfig(parentActionWfWidget);
         supportingWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(SUPP_ID);
         addAndConfig(supportingWidget);
         derivedFromWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(DER_FROM_ID);
         addAndConfig(derivedFromWidget);
         derivedToWidget = (XXRelatedWfeWidget) editor.getWorkFlowTab().getHeader().getWidget(DER_TO_ID);
         addAndConfig(derivedToWidget);
      }
   }

   private void addAndConfig(XXRelatedWfeWidget widget) {
      if (widget != null) {
         widgets.add(widget);
         widget.setToolTip("Select to Open");
      }
   }

   public List<XWidget> getWidgets() {
      return widgets;
   }

}
