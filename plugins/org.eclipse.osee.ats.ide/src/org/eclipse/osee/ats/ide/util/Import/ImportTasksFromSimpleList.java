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

package org.eclipse.osee.ats.ide.util.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksFromSimpleList extends AbstractBlam {

   public final static String ASSIGNEES = "Assignees";
   public final static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   protected IAtsTeamWorkflow teamWf;
   protected AtsWorkDefinitionToken taskWorkDef = AtsWorkDefinitionToken.SENTINEL;
   private XCombo stateCombo;

   @Override
   public String getName() {
      return "Import Tasks From Simple List";
   }

   protected String getTitlesLabel() {
      return "Task Import Titles";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      final AtsApi atsApi = AtsApiService.get();
      final String commitComment = getClass().getSimpleName();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               Set<Artifact> artifacts = new HashSet<>();
               artifacts.addAll(variableMap.getArtifacts(TEAM_WORKFLOW));
               if (teamWf != null) {
                  artifacts.add((Artifact) teamWf);
               }
               final List<AtsUser> assignees = new ArrayList<>();
               for (Artifact art : variableMap.getArtifacts(ASSIGNEES)) {
                  if (art instanceof User) {
                     AtsUser atsUser = atsApi.getUserService().getUserById(art);
                     assignees.add(atsUser);
                  }
               }
               final List<String> titles = new ArrayList<>();
               for (String title : variableMap.getString(getTitlesLabel()).split("\n")) {
                  title = title.replaceAll("\r", "");
                  if (!title.equals("")) {
                     titles.add(title);
                  }
               }

               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to add tasks.");
                  return;
               }
               if (artifacts.size() > 1) {
                  AWorkbench.popup("ERROR", "Only drag ONE Team Workflow.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               if (titles.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must enter title(s).");
                  return;
               }
               try {
                  if (assignees.isEmpty()) {
                     assignees.add(AtsCoreUsers.UNASSIGNED_USER);
                  }
                  IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(artifact);
                  NewTaskData newTaskData = NewTaskData.create(teamWf, titles, assignees, null,
                     atsApi.getUserService().getCurrentUser(), null, null, null);
                  if (taskWorkDef.isValid()) {
                     newTaskData.setTaskWorkDef(taskWorkDef);
                  }
                  atsApi.getTaskService().createTasks(
                     NewTaskSet.create(newTaskData, commitComment, atsApi.getUserService().getCurrentUserId()));
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  return;
               }

               artifact.reloadAttributesAndRelations();
               WorkflowEditor.editArtifact(artifact);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && teamWf != null) {
         final XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(Arrays.asList(teamWf));
         xWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               List<Artifact> artifacts = viewer.getArtifacts();
               if (artifacts.isEmpty() || !(artifacts.iterator().next() instanceof IAtsTeamWorkflow)) {
                  teamWf = null;
               } else {
                  teamWf = (IAtsTeamWorkflow) artifacts.iterator().next();
               }
               try {
                  refreshStateCombo();
               } catch (OseeStateException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         });
      } else if (xWidget.getLabel().equals(RelatedToStateColumn.RELATED_TO_STATE_SELECTION)) {
         stateCombo = (XCombo) xWidget;
         refreshStateCombo();
      }

   }

   private void refreshStateCombo() {
      if (stateCombo != null && teamWf != null) {
         List<String> names = RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf);
         stateCombo.setDataStrings(names.toArray(new String[names.size()]));
      }
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      createTeamWfWidget(buffer);
      buffer.append(
         "<XWidget xwidgetType=\"XText\" fill=\"Vertically\" height=\"80\" displayName=\"" + getTitlesLabel() + "\" toolTip=\"Enter task titles one per line\"/>");
      buffer.append(
         "<XWidget xwidgetType=\"XCombo()\" beginComposite=\"2\" labelAfter=\"true\" height=\"80\" displayName=\"" + RelatedToStateColumn.RELATED_TO_STATE_SELECTION + "\" />");
      buffer.append("<XWidget xwidgetType=\"XHyperlabelMemberSelection\" displayName=\"" + ASSIGNEES + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   protected void createTeamWfWidget(StringBuffer buffer) {
      buffer.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />");
   }

   @Override
   public String getDescriptionUsage() {
      return "Import tasks from spreadsheet into given Team Workflow.  Assignee for tasks will be current user unless otherwise specified.";
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_IMPORT);
   }

   public void setTaskWorkDef(AtsWorkDefinitionToken taskWorkDef) {
      this.taskWorkDef = taskWorkDef;
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.IMPORT);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.IMPORT);
   }

}