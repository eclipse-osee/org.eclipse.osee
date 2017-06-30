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

package org.eclipse.osee.ats.version;

import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.result.Manipulations;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class GenerateFullVersionReportItem extends XNavigateItemAction {

   private final IAtsTeamDefinition teamDef;

   public GenerateFullVersionReportItem(XNavigateItem parent) {
      super(parent, "Generate Full Version Report", FrameworkImage.VERSION);
      this.teamDef = null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      IAtsTeamDefinition teamDef = getTeamDefinition();
      if (teamDef == null) {
         return;
      }
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      PublishReportJob job = new PublishReportJob(teamDef);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private IAtsTeamDefinition getTeamDefinition() {
      if (teamDef == null) {
         Set<IAtsTeamDefinition> teamDefinitions = null;
         try {
            teamDefinitions =
               TeamDefinitions.getTeamReleaseableDefinitions(Active.Active, AtsClientService.get().getQueryService());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading team definitions", ex);
         }

         TeamDefinitionDialog dialog = new TeamDefinitionDialog();
         dialog.setInput(teamDefinitions);
         int result = dialog.open();
         if (result == 0) {
            return dialog.getSelectedFirst();
         }
         return null;
      } else {
         return teamDef;
      }
   }

   private static class PublishReportJob extends Job {

      private final IAtsTeamDefinition teamDef;

      public PublishReportJob(IAtsTeamDefinition teamDef) {
         super(teamDef.getName() + " as of " + DateUtil.getDateNow());
         this.teamDef = teamDef;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            String html = VersionReportJob.getFullReleaseReport(teamDef, monitor);
            XResultData rd = new XResultData();
            rd.addRaw(html);
            XResultDataUI.report(rd, getName(), Manipulations.RAW_HTML);
         } catch (Exception ex) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.toString(), ex);
         }

         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
