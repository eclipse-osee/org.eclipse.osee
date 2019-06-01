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
package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CreateActionFromTaskBlam extends AbstractBlam {

   private final static String TASKS = "Tasks (drop here)";
   private final static String TITLE = "New Title (blank for same title)";
   private final static String ACTIONABLE_ITEMS = "Actionable Item(s)";
   private final static String CHANGE_TYPE = "Change Type";
   private final static String PRIORITY = "Priority";
   private Collection<TaskArtifact> taskArtifacts;

   public CreateActionFromTaskBlam() {
      // do nothing
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
               String changeTypeStr = variableMap.getString(CHANGE_TYPE);
               if (changeTypeStr == null || changeTypeStr.equals("--select--")) {
                  AWorkbench.popup("ERROR", "Must select a Change Type");
                  return;
               }
               ChangeType changeType = ChangeType.valueOf(changeTypeStr);
               String priority = variableMap.getString(PRIORITY);
               if (priority == null || priority.equals("--select--")) {
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
                  handleCreateActions(taskArts, title, aias, changeType, priority, monitor);
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

   private void handleCreateActions(Collection<TaskArtifact> tasks, String title, Collection<IAtsActionableItem> aias, ChangeType changeType, String priority, IProgressMonitor monitor) {
      Set<TeamWorkFlowArtifact> newTeamArts = new HashSet<>();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create Actions from Tasks");
      for (TaskArtifact task : tasks) {
         String useTitle = title;
         if (!Strings.isValid(useTitle)) {
            useTitle = task.getName();
         }
         ActionResult result = AtsClientService.get().getActionFactory().createAction(
            AtsClientService.get().getUserService().getCurrentUser(), useTitle, getDescription(task), changeType,
            priority, false, null, aias, new Date(), AtsClientService.get().getUserService().getCurrentUser(), null,
            changes);

         for (IAtsTeamWorkflow teamWf : result.getTeams()) {
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
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TASKS + "\" />" +
      //
         "<XWidget xwidgetType=\"XHyperlabelActionableItemSelection\" displayName=\"" + ACTIONABLE_ITEMS + "\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XText\" displayName=\"" + TITLE + "\" horizontalLabel=\"true\" defaultValue=\"" + getDefaultTitle() + "\"/>" +
         //
         "<XWidget displayName=\"" + CHANGE_TYPE + "\" xwidgetType=\"XCombo(" + Collections.toString(",",
            AttributeTypeManager.getEnumerationValues(
               AtsAttributeTypes.ChangeType)) + ")\" required=\"true\" horizontalLabel=\"true\" toolTip=\"" + AtsAttributeTypes.ChangeType.getDescription() + "\"/>" +
         //
         "<XWidget displayName=\"" + PRIORITY + "\" xwidgetType=\"XCombo(" + Collections.toString(",",
            AttributeTypeManager.getEnumerationValues(
               AtsAttributeTypes.PriorityType)) + ")\" required=\"true\" horizontalLabel=\"true\"/>" +
         //
         "</xWidgets>";
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
      return AXml.textToXml(title);
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
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.Everyone);
   }

}