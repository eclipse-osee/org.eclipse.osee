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
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskStates;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workflow.SMAState;
import org.eclipse.osee.ats.core.workflow.XCurrentStateDam;
import org.eclipse.osee.ats.core.workflow.XStateDam;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactMultiChoiceSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class TaskMetrics extends AbstractBlam {
   private final CountingMap<IBasicUser> metrics;
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   private XHyperlabelTeamDefinitionSelection teamCombo;
   private XArtifactMultiChoiceSelect versionsWidget;

   public TaskMetrics() {
      metrics = new CountingMap<IBasicUser>();
   }

   @Override
   public String getName() {
      return "Task Metrics";
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates task metrics specific to LBA artifacts.";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      try {
         monitor.beginTask("TaskMetrics", 100);
         metrics.clear();
         charBak = new CharBackedInputStream();
         excelWriter = new ExcelXmlWriter(charBak.getWriter());

         //IArtifactType artifctType = variableMap.getArtifactType("Artifact Type");

         List<Artifact> versionArtifacts = versionsWidget.getSelected();

         if (!versionArtifacts.isEmpty()) {
            Set<Artifact> teamWorkflows =
               RelationManager.getRelatedArtifacts(versionArtifacts, 1,
                  AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow);

            int counter = 0;
            for (Artifact art : teamWorkflows) {
               monitor.worked(1 / teamWorkflows.size());
               counter++;
               if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {

                  TeamWorkFlowArtifact workflow = (TeamWorkFlowArtifact) art;

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
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void tallyState(TaskArtifact task) throws OseeCoreException {
      XStateDam stateDam = new XStateDam(task);

      SMAState state = stateDam.getState(TaskStates.InWork, false);
      if (state == null) {
         XCurrentStateDam currentStateDam = new XCurrentStateDam(task);
         state = currentStateDam.getState(TaskStates.InWork, false);
      }

      for (IBasicUser user : state.getAssignees()) {
         int percentComplete = state.getPercentComplete();

         if (percentComplete == 100) {
            task.getCompletedDate();
            String resolution = task.getSoleAttributeValue(AtsAttributeTypes.Resolution, "");

            if (resolution.equals("Complete")) {
               metrics.put(user, 100);
            } else {
               metrics.put(user, 5);
            }
         } else {
            metrics.put(user, percentComplete);
         }
      }
   }

   private void writeSummary() throws IOException {
      excelWriter.startSheet("task metrics", 6);
      excelWriter.writeRow("Engineer", "TaskMetric");

      for (Entry<IBasicUser, MutableInteger> entry : metrics.getCounts()) {
         IBasicUser user = entry.getKey();
         MutableInteger metric = entry.getValue();
         excelWriter.writeRow(user.getName(), metric.toString());
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetLabel = xWidget.getLabel();

      if (widgetLabel.equals("Version(s)")) {
         versionsWidget = (XArtifactMultiChoiceSelect) xWidget;
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (xWidget instanceof XHyperlabelTeamDefinitionSelection) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) xWidget;
         teamCombo.addXModifiedListener(new TeamSelectedListener());
      }
   }

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      return getXWidgetsXmlFromUiFile(getClass().getSimpleName(), AtsPlugin.PLUGIN_ID);
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.Report");
   }

   private class TeamSelectedListener implements XModifiedListener {
      @Override
      public void widgetModified(XWidget widget) {

         Collection<Artifact> versions = new HashSet<Artifact>();

         versionsWidget.setSelectableItems(Collections.<Artifact> emptyList());

         for (TeamDefinitionArtifact teamDef : teamCombo.getSelectedTeamDefintions()) {
            TeamDefinitionArtifact teamDefinitionHoldingVersions;
            try {

               teamDefinitionHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();

               if (teamDefinitionHoldingVersions != null) {
                  versions.addAll(teamDefinitionHoldingVersions.getVersionsArtifacts());
               }

            } catch (OseeCoreException ex) {
               OseeLog.log(TaskMetrics.class, Level.SEVERE, ex.toString());
            }
         }

         if (!versions.isEmpty()) {
            versionsWidget.setSelectableItems(versions);
         }
      }
   }
}
