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

import static org.eclipse.osee.ats.ide.column.RelatedToStateColumnUI.RELATED_TO_STATE_SELECTION;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumnUI;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewerWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ImportTasksFromSimpleListBlam extends AbstractAtsBlam {

   public final static String ASSIGNEES = "Assignees";
   public final static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   protected IAtsTeamWorkflow teamWf;
   protected AtsWorkDefinitionToken taskWorkDef = AtsWorkDefinitionToken.SENTINEL;
   private XComboWidget stateCombo;

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
                  if (art.isOfType(CoreArtifactTypes.User)) {
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
                  NewTaskSet createTasks = atsApi.getTaskService().createTasks(
                     NewTaskSet.create(newTaskData, commitComment, atsApi.getUserService().getCurrentUserId()));
                  if (createTasks.isErrors()) {
                     XResultDataUI.report(createTasks.getResults(), getName());
                     return;
                  }
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
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && teamWf != null) {
         final XListDropViewerWidget viewer = (XListDropViewerWidget) xWidget;
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
      } else if (xWidget.getLabel().equals(RelatedToStateColumnUI.RELATED_TO_STATE_SELECTION)) {
         stateCombo = (XComboWidget) xWidget;
         refreshStateCombo();
      }

   }

   private void refreshStateCombo() {
      if (stateCombo != null && teamWf != null) {
         List<String> names = RelatedToStateColumnUI.getValidInWorkStates((TeamWorkFlowArtifact) teamWf);
         stateCombo.setDataStrings(names.toArray(new String[names.size()]));
      }
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      createTeamWfWidget(wba);
      wb.andWidget(TEAM_WORKFLOW, WidgetId.XListDropViewerWidget);
      wb.andWidget(getTitlesLabel(), WidgetId.XTextWidget).andToolTip("Enter task titles one per line");
      wb.andWidget("Create Tasks In State", WidgetId.XComboWidget).andLabelAfter().andToolTip(
         RELATED_TO_STATE_SELECTION);
      wb.andWidget("Assignee", WidgetId.XHyperlinkMemberSelWidget);
      return wb.getXWidgetDatas();
   }

   protected void createTeamWfWidget(XWidgetBuilderAts wba) {
      // for subclass
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