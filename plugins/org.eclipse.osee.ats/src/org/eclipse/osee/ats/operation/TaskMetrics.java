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
package org.eclipse.osee.ats.operation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.util.widgets.dialog.AtsObjectMultiChoiceSelect;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class TaskMetrics extends AbstractBlam {
   private static final int IN_WORK_PERCENT = 5;
   private static final int COMPLETED_CANCELLED_PERCENT = 100;
   private final CountingMap<IAtsUser> metrics = new CountingMap<>();
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   private XHyperlabelTeamDefinitionSelection teamCombo;
   private AtsObjectMultiChoiceSelect versionsWidget;

   @Override
   public String getName() {
      return "Task Metrics";
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates task metrics specific to LBA artifacts.";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) {
      try {
         monitor.beginTask("TaskMetrics", 100);
         metrics.clear();
         charBak = new CharBackedInputStream();
         excelWriter = new ExcelXmlWriter(charBak.getWriter());

         List<IAtsObject> versionArtifacts = versionsWidget.getSelected();

         if (!versionArtifacts.isEmpty()) {
            Set<IAtsTeamWorkflow> teamWorkflows = new HashSet<>();
            for (IAtsObject version : versionArtifacts) {
               teamWorkflows.addAll(
                  AtsClientService.get().getVersionService().getTargetedForTeamWorkflows((IAtsVersion) version));
            }

            for (IAtsTeamWorkflow team : teamWorkflows) {
               monitor.worked(1 / teamWorkflows.size());
               if (((Artifact) team.getStoreObject()).isOfType(AtsArtifactTypes.TeamWorkflow)) {

                  TeamWorkFlowArtifact workflow = (TeamWorkFlowArtifact) team;

                  if (teamCombo.getSelectedTeamDefintions().contains(workflow.getTeamDefinition())) {
                     for (Artifact task : workflow.getTaskArtifacts()) {
                        tallyState((TaskArtifact) task);
                     }
                  }

               }
            }
         }

         writeSummary();

         excelWriter.endWorkbook();

         IFile iFile = OseeData.getIFile("Task_Metrics_" + Lib.getDateTimeString() + ".xml");

         AIFile.writeToFile(iFile, charBak);
         Program.launch(iFile.getLocation().toOSString());
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void tallyState(TaskArtifact task) {

      List<IAtsUser> assignees = task.getStateMgr().getAssignees();
      if (assignees.isEmpty()) {
         assignees = AtsClientService.get().getImplementerService().getImplementers(task);
      }
      for (IAtsUser user : assignees) {
         int percentComplete =
            PercentCompleteTotalUtil.getPercentCompleteTotal(task, AtsClientService.get().getServices());
         if (percentComplete == COMPLETED_CANCELLED_PERCENT) {
            if (task.getStateMgr().getStateType().isCompletedOrCancelledState()) {
               metrics.put(user, COMPLETED_CANCELLED_PERCENT);
            } else {
               metrics.put(user, IN_WORK_PERCENT);
            }
         } else {
            metrics.put(user, percentComplete);
         }
      }
   }

   private void writeSummary() throws IOException {
      excelWriter.startSheet("task metrics", 6);
      excelWriter.writeRow("Engineer", "TaskMetric");

      for (Entry<IAtsUser, MutableInteger> entry : metrics.getCounts()) {
         IAtsUser user = entry.getKey();
         MutableInteger metric = entry.getValue();
         excelWriter.writeRow(user.getName(), metric.toString());
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetLabel = xWidget.getLabel();

      if (widgetLabel.equals("Version(s)")) {
         versionsWidget = (AtsObjectMultiChoiceSelect) xWidget;
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (xWidget instanceof XHyperlabelTeamDefinitionSelection) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) xWidget;
         teamCombo.addXModifiedListener(new TeamSelectedListener());
      }
   }

   @Override
   public String getXWidgetsXml() {
      return OseeInf.getResourceContents(getClass().getSimpleName(), getClass());
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.Report");
   }

   private class TeamSelectedListener implements XModifiedListener {
      @Override
      public void widgetModified(XWidget widget) {

         Collection<IAtsObject> versions = new HashSet<>();

         versionsWidget.setSelectableItems(Collections.<IAtsObject> emptyList());

         for (IAtsTeamDefinition teamDef : teamCombo.getSelectedTeamDefintions()) {
            IAtsTeamDefinition teamDefinitionHoldingVersions;
            try {

               teamDefinitionHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();

               if (teamDefinitionHoldingVersions != null) {
                  versions.addAll(teamDefinitionHoldingVersions.getVersions());
               }

            } catch (OseeCoreException ex) {
               log(ex);
            }
         }

         if (!versions.isEmpty()) {
            versionsWidget.setSelectableItems(versions);
         }
      }
   }
}
