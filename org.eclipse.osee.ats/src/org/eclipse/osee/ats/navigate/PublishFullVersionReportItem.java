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
package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.VersionReportJob;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class PublishFullVersionReportItem extends XNavigateItemAction {

   private final TeamDefinitionArtifact teamDef;
   private String publishToFilename;
   private final String teamDefName;

   public PublishFullVersionReportItem(XNavigateItem parent, String name, TeamDefinitionArtifact teamDef, String publishToFilename) {
      super(parent, name);
      this.teamDef = teamDef;
      this.teamDefName = null;
      this.publishToFilename = publishToFilename;
   }

   public PublishFullVersionReportItem(XNavigateItem parent, String name, String teamDefName, String publishToFilename) {
      super(parent, name);
      this.teamDefName = teamDefName;
      this.teamDef = null;
      this.publishToFilename = publishToFilename;
   }

   public PublishFullVersionReportItem(XNavigateItem parent) {
      this(parent, "Publish Full Version Report", (String) null, null);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
      String usePublishToFilename = publishToFilename;
      if (usePublishToFilename == null) {
         final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.SAVE);
         dialog.setFilterExtensions(new String[] {"*.html"});
         usePublishToFilename = dialog.open();
         if (usePublishToFilename == null) return;
      }
      TeamDefinitionArtifact useTeamDef = teamDef;
      if (useTeamDef == null && teamDefName != null) {
         try {
            useTeamDef = AtsCache.getSoleArtifactByName(teamDefName, TeamDefinitionArtifact.class);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing, going to get team below
         }
      }
      if (useTeamDef == null) {
         TeamDefinitionDialog ld = new TeamDefinitionDialog("Select Team", "Select Team");
         ld.setInput(TeamDefinitionArtifact.getTeamDefinitions(Active.Both));
         int result = ld.open();
         if (result == 0) {
            useTeamDef = (TeamDefinitionArtifact) ld.getResult()[0];
         } else
            return;
      } else if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      String title = useTeamDef.getDescriptiveName() + " Version Report";
      PublishReportJob job = new PublishReportJob(title, teamDef, usePublishToFilename);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private class PublishReportJob extends Job {

      private final TeamDefinitionArtifact teamDef;
      private final String filename;

      public PublishReportJob(String title, TeamDefinitionArtifact teamDef, String filename) {
         super(title);
         this.teamDef = teamDef;
         this.filename = filename;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.ats.util.ReleaseReportJob#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            String html = VersionReportJob.getFullReleaseReport(teamDef, monitor);
            AFile.writeFile(filename, html);
            Program.launch(filename);
            AWorkbench.popup("Publish Complete", "Data Published To \"" + filename + "\"");
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }

         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
