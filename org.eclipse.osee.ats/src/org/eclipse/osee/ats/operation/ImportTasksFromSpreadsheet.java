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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.Import.ExcelAtsTaskArtifactExtractor;
import org.eclipse.osee.ats.util.Import.TaskImportJob;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksFromSpreadsheet extends AbstractBlam {

   public static String TASK_IMPORT_SPREADSHEET = "Task Import Spreadsheet";
   public static String TEAM_WORKFLOW = "Taskable Workflow (drop here)";
   public static String PERSIST = "Persist";
   public static String EMAIL_POCS = "Email POCs (if persist)";
   private TaskableStateMachineArtifact taskableStateMachineArtifact;

   public ImportTasksFromSpreadsheet() throws IOException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final BlamVariableMap variableMap, IProgressMonitor monitor)throws OseeCoreException, SQLException{
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TEAM_WORKFLOW);
               String filename = variableMap.getString(TASK_IMPORT_SPREADSHEET);
               boolean persist = variableMap.getBoolean(PERSIST);
               boolean emailPocs = variableMap.getBoolean(EMAIL_POCS);

               if (artifacts.size() == 0) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to add tasks.");
                  return;
               }
               if (artifacts.size() > 1) {
                  AWorkbench.popup("ERROR", "Only drag ONE Team Workflow.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!(artifact instanceof TeamWorkFlowArtifact)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               if (filename == null || filename.equals("")) {
                  AWorkbench.popup("ERROR", "Must enter valid filename.");
                  return;
               }
               File file = new File(filename);
               try {
                  Jobs.startJob(new TaskImportJob(file, new ExcelAtsTaskArtifactExtractor(
                        (TeamWorkFlowArtifact) artifact, emailPocs, persist), BranchPersistenceManager.getAtsBranch(),
                        false));
               } catch (Exception ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
                  return;
               }

               SMAEditor.editArtifact(artifact);
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         };
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)throws OseeCoreException, SQLException{
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && taskableStateMachineArtifact != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(Arrays.asList(taskableStateMachineArtifact));
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />");
      buffer.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"" + TASK_IMPORT_SPREADSHEET + "\" />");
      buffer.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + PERSIST + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      buffer.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + EMAIL_POCS + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Import tasks from spreadsheet into given Team Workflow";
   }

   /**
    * @return the TaskableStateMachineArtifact
    */
   public TaskableStateMachineArtifact getTaskableStateMachineArtifact() {
      return taskableStateMachineArtifact;
   }

   /**
    * @param taskableStateMachineArtifact the TaskableStateMachineArtifact to set
    */
   public void setTaskableStateMachineArtifact(TaskableStateMachineArtifact taskableStateMachineArtifact) {
      this.taskableStateMachineArtifact = taskableStateMachineArtifact;
   }

}