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
import java.util.Collection;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.swt.widgets.Display;

/**
 * Donald G. Dunne
 */
public class GenerateReviewParticipationReport extends XNavigateItemAction {

   public GenerateReviewParticipationReport(XNavigateItem parent) {
      super(parent, "Generate Review Participation Report");
   }

   @Override
   public void run() throws SQLException {
      UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell());
      int result = ld.open();
      if (result == 0) {
         User selectedUser = (User) ld.getSelection();
         ParticipationReportJob job =
               new ParticipationReportJob("Review Participation Report - " + selectedUser, selectedUser);
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      }
   }

   private class ParticipationReportJob extends Job {

      private final User user;

      public ParticipationReportJob(String title, User user) {
         super(title);
         this.user = user;
      }

      public IStatus run(IProgressMonitor monitor) {
         try {
            final String html =
                  getReleaseReportHtml(getName() + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), user, monitor);
            Display.getDefault().asyncExec(new Runnable() {
               public void run() {
                  XResultView.getResultView().addResultPage(
                        new XResultPage(getName(), html, Manipulations.HTML_MANIPULATIONS));
                  AWorkbench.popup("Complete", getName() + " Complete...Results in ATS Results");
               }
            });

         } catch (SQLException ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }

   }

   public static String getReleaseReportHtml(String title, User user, IProgressMonitor monitor) throws SQLException {
      if (user == null) {
         AWorkbench.popup("ERROR", "Must select product, config and version.");
         return null;
      }
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title + " as of " + XDate.getDateNow(), user.getDescriptiveName()));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Id", "Legacy Id", "Date", "Type", "Role", "State",
            "Title"}));
      MyReviewWorkflowItem srch = new MyReviewWorkflowItem("", user, ReviewState.All);
      Collection<Artifact> reviewArts = srch.performSearchGetResults();
      for (Artifact art : reviewArts) {
         ReviewSMArtifact reviewArt = (ReviewSMArtifact) art;
         try {
            sb.append(AHTML.addRowMultiColumnTable(new String[] {reviewArt.getHumanReadableId(),
                  reviewArt.getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName()),
                  getDateString(reviewArt.getWorldViewCompletedDate()), reviewArt.getArtifactTypeName(),
                  getRolesStr(reviewArt, user), reviewArt.getCurrentStateName(), reviewArt.getDescriptiveName()}));
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private static String getRolesStr(ReviewSMArtifact reviewArt, User user) {
      String str = "";
      for (UserRole role : reviewArt.getUserRoleManager().getUserRoles()) {
         if (role.getUser().equals(user)) str += role.getRole().name() + ", ";
      }
      return str.replaceFirst(", $", "");
   }

   private static String getDateString(Date date) {
      if (date == null) return "";
      return XDate.getDateStr(date, XDate.MMDDYY);
   }

}
