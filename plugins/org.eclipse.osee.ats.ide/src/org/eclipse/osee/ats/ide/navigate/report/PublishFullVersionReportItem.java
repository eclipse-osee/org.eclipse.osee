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

package org.eclipse.osee.ats.ide.navigate.report;

import java.io.File;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.config.version.VersionReportJob;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class PublishFullVersionReportItem extends XNavigateItemAction {

   private final IAtsTeamDefinition teamDef;
   private final String publishToFilename;

   public PublishFullVersionReportItem(String name, IAtsTeamDefinition teamDef, String publishToFilename) {
      super(name, AtsNavigateViewItems.ATS_VERSIONS);
      this.teamDef = teamDef;
      this.publishToFilename = publishToFilename;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      String usePublishToFilename = publishToFilename;
      if (usePublishToFilename == null) {
         final FileDialog dialog = new FileDialog(Displays.getActiveShell().getShell(), SWT.SAVE);
         dialog.setFilterExtensions(new String[] {"*.html"});
         usePublishToFilename = dialog.open();
         if (usePublishToFilename == null) {
            return;
         }
      }
      IAtsTeamDefinition useTeamDef = teamDef;
      if (useTeamDef == null) {
         List<IAtsTeamDefinition> teamDefinitions = null;
         try {
            teamDefinitions = AtsApiService.get().getTeamDefinitionService().getTeamDefinitions(Active.Both);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading team definitions", ex);
         }

         TeamDefinitionDialog dialog = new TeamDefinitionDialog();
         dialog.setInput(teamDefinitions);
         int result = dialog.open();
         if (result == 0) {
            useTeamDef = dialog.getSelectedFirst();
         } else {
            return;
         }
      } else if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }

      String title = useTeamDef == null ? "teamDef is null" : useTeamDef.getName() + " Version Report";
      PublishReportJob job = new PublishReportJob(title, teamDef, usePublishToFilename);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private static class PublishReportJob extends Job {

      private final IAtsTeamDefinition teamDef;
      private final String filename;

      public PublishReportJob(String title, IAtsTeamDefinition teamDef, String filename) {
         super(title);
         this.teamDef = teamDef;
         this.filename = filename;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            String html = VersionReportJob.getFullReleaseReport(teamDef, monitor);
            Lib.writeStringToFile(html, new File(filename));
            Program.launch(filename);
            AWorkbench.popup("Publish Complete", "Data Published To \"" + filename + "\"");
         } catch (Exception ex) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.toString(), ex);
         }

         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
