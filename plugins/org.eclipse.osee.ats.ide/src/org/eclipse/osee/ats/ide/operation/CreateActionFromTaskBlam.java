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

package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkChangeTypeSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkPrioritySelection;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CreateActionFromTaskBlam extends AbstractBlam {

   private final static String TASKS = "Tasks (drop here)";
   private final static String TITLE = "Title";
   private final static String ACTIONABLE_ITEMS = "Actionable Item(s)";
   private final static String CHANGE_TYPE = "Change Type";
   private final static String PRIORITY = "Priority";
   private Collection<TaskArtifact> taskArtifacts;
   private final AtsApi atsApi;
   private XHyperlinkChangeTypeSelection changeTypeWidget;
   private XHyperlinkPrioritySelection priorityWidget;
   private XHyperlabelActionableItemSelection aiWidget;

   public CreateActionFromTaskBlam() {
      // do nothing
      atsApi = AtsApiService.get();
   }

   @Override
   public void runOperation(final VariableMap variableMap, final IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TASKS);
               String title = variableMap.getString(TITLE);
               Collection<IAtsActionableItem> aiasArts =
                  variableMap.getCollection(IAtsActionableItem.class, ACTIONABLE_ITEMS);
               ChangeTypes changeType = (ChangeTypes) variableMap.getValue(CHANGE_TYPE);
               if (changeType == null) {
                  AWorkbench.popup("ERROR", "Must select a Change Type");
                  return;
               }

               Priorities priority = (Priorities) variableMap.getValue(PRIORITY);
               if (priority == null) {
                  AWorkbench.popup("ERROR", "Must select a Priority");
                  return;
               }

               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Tasks to create Actions.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!artifact.isOfType(AtsArtifactTypes.Task)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Task");
                  return;
               }
               if (aiasArts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must select Actionable Item(s)");
                  return;
               }
               try {
                  AtsUtilClient.setEmailEnabled(false);
                  Collection<TaskArtifact> taskArts = Collections.castAll(artifacts);
                  Collection<IAtsActionableItem> aias = Collections.castAll(aiasArts);
                  handleCreateActions(taskArts, title, aias, changeType, priority.getName(), monitor);
               } catch (Exception ex) {
                  log(ex);
               } finally {
                  AtsUtilClient.setEmailEnabled(true);
               }

            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   private void handleCreateActions(Collection<TaskArtifact> tasks, String title, Collection<IAtsActionableItem> aias, ChangeTypes changeType, String priority, IProgressMonitor monitor) {
      Set<TeamWorkFlowArtifact> newTeamArts = new HashSet<>();
      IAtsChangeSet changes = atsApi.createChangeSet("Create Actions from Tasks");
      for (TaskArtifact task : tasks) {
         String useTitle = title;
         if (!Strings.isValid(useTitle)) {
            useTitle = task.getName();
         }
         ActionResult result = atsApi.getActionService().createAction(atsApi.getUserService().getCurrentUser(),
            useTitle, getDescription(task), changeType, priority, false, null, aias, new Date(),
            atsApi.getUserService().getCurrentUser(), null, changes);

         for (IAtsTeamWorkflow teamWf : result.getTeamWfs()) {
            newTeamArts.add((TeamWorkFlowArtifact) teamWf.getStoreObject());
            changes.relate(teamWf, CoreRelationTypes.SupportingInfo_SupportingInfo, task);
            changes.add(teamWf);
         }
      }
      changes.execute();
      if (newTeamArts.size() == 1) {
         WorkflowEditor.editArtifact(newTeamArts.iterator().next());
      } else {
         AtsEditors.openInAtsWorldEditor("Created Tasks from Actions", newTeamArts);
      }

   }

   private String getDescription(TaskArtifact taskArt) {
      if (Strings.isValid(taskArt.getDescription())) {
         return String.format("Create from task [%s]\n\n[%s]", taskArt.toStringWithId(), taskArt.getDescription());
      }
      return String.format("Created from task [%s]", taskArt.toStringWithId());
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TASKS) && taskArtifacts != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(taskArtifacts);
      } else if (xWidget.getLabel().equals(CHANGE_TYPE)) {
         changeTypeWidget = (XHyperlinkChangeTypeSelection) xWidget;
      } else if (xWidget.getLabel().equals(PRIORITY)) {
         priorityWidget = (XHyperlinkPrioritySelection) xWidget;
      } else if (xWidget.getLabel().equals(ACTIONABLE_ITEMS)) {
         aiWidget = (XHyperlabelActionableItemSelection) xWidget;
         aiWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               Collection<IAtsActionableItem> ais = aiWidget.getSelectedActionableItems();
               if (!ais.isEmpty()) {
                  IAtsActionableItem ai = ais.iterator().next();

                  List<ChangeTypes> changeTypeOptions = atsApi.getWorkItemService().getChangeTypeOptions(ai);
                  changeTypeWidget.setSelectable(changeTypeOptions);

                  List<Priorities> priorityOptions = atsApi.getWorkItemService().getPrioritiesOptions(ai);
                  priorityWidget.setSelectable(priorityOptions);
               }
            }
         });
      }
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget(TASKS, "XListDropViewer");
      wb.andXHyperlinkActionableItemActive().andRequired().endWidget();
      wb.andXText(TITLE).andDefault(getDefaultTitle()).andRequired().endWidget();
      wb.andChangeType(ChangeTypes.DEFAULT_CHANGE_TYPES).andRequired().endWidget();
      wb.andPriority().andRequired().endWidget();
      return wb.getItems();
   }

   /**
    * Return "Copy of"-title if all titles of workflows are the same, else ""
    */
   private String getDefaultTitle() {
      String title = "";
      if (taskArtifacts != null) {
         for (TaskArtifact taskArt : taskArtifacts) {
            if (title.equals("")) {
               title = taskArt.getName();
            } else if (!title.equals(taskArt.getName())) {
               return "";
            }
         }
      }
      return title;
   }

   @Override
   public String getDescriptionUsage() {
      return "Create Action from task and relate using supporting information relation.";
   }

   public void setDefaultTeamWorkflows(Collection<? extends TaskArtifact> taskArtifacts) {
      this.taskArtifacts = new LinkedList<>();
      this.taskArtifacts.addAll(taskArtifacts);
   }

   @Override
   public String getName() {
      return "Create Actions from Tasks";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL);
   }

}