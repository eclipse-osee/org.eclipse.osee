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
package org.eclipse.osee.ats.review;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.column.AtsIdColumn;
import org.eclipse.osee.ats.column.LegacyPcrIdColumn;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.column.StateColumn;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerReviewRoleColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerSmaCompletedDateColumn;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.GuidColumn;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Donald G. Dunne
 */
public class GenerateReviewParticipationReport extends XNavigateItemAction {

   private static final String MASS_XVIEWER_CUSTOMIZE_NAMESPACE = "org.eclipse.osee.ats.ReviewParticipationReport";

   private IAtsUser selectedUser = null;

   public GenerateReviewParticipationReport(XNavigateItem parent) {
      super(parent, "Generate Review Participation Report", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      IAtsUser useUser = null;
      if (selectedUser != null) {
         useUser = selectedUser;
      } else {
         IAtsUserServiceClient userServiceClient = AtsClientService.get().getUserServiceClient();
         UserListDialog ld =
            new UserListDialog(Displays.getActiveShell(), "Select User",
               userServiceClient.getOseeUsers(AtsClientService.get().getUserService().getUsers(Active.Active)));
         int result = ld.open();
         if (result == 0) {
            if (ld.getResult().length == 0) {
               AWorkbench.popup("ERROR", "Must select user");
               return;
            }
            useUser = userServiceClient.getUserFromOseeUser(ld.getSelection());
         }
      }

      boolean forcePend = Collections.getAggregate(tableLoadOptions).contains(TableLoadOption.ForcePend);
      if (useUser != null) {
         ParticipationReportJob job =
            new ParticipationReportJob("Review Participation Report - " + useUser, useUser, forcePend);
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
         if (forcePend) {
            try {
               job.join();
            } catch (InterruptedException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }
   private static class ParticipationReportJob extends Job {

      private final IAtsUser user;
      private final boolean forcePend;

      public ParticipationReportJob(String title, IAtsUser user, boolean forcePend) {
         super(title);
         this.user = user;
         this.forcePend = forcePend;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            MyReviewWorkflowItem srch = new MyReviewWorkflowItem("", user, ReviewState.All);
            Collection<Artifact> reviewArts = srch.performSearchGetResults();
            final MassArtifactEditorInput input =
               new MassArtifactEditorInput(getName() + " as of " + DateUtil.getDateNow(), reviewArts,
                  new ReviewParticipationXViewerFactory(user));
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  MassArtifactEditor.editArtifacts(input);
               }

            }, forcePend);

         } catch (Exception ex) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   public static class ReviewParticipationXViewerFactory extends SkynetXViewerFactory {

      public ReviewParticipationXViewerFactory(IAtsUser user) {
         super(MASS_XVIEWER_CUSTOMIZE_NAMESPACE);
         registerColumns(ArtifactTypeColumn.getInstance());
         registerColumns(new AtsIdColumn());
         registerColumns(LegacyPcrIdColumn.getInstance());
         registerColumns(StateColumn.getInstance());
         registerColumns(new XViewerSmaCompletedDateColumn("Completed"));
         registerColumns(new XViewerReviewRoleColumn(user));
         registerColumns(RelatedToStateColumn.getInstance());
         registerColumns(new ArtifactNameColumn(true));
         registerColumns(new GuidColumn(true));
         registerAllAttributeColumns();
      }

   }

   public IAtsUser getSelectedUser() {
      return selectedUser;
   }

   public void setSelectedUser(IAtsUser selectedUser) {
      this.selectedUser = selectedUser;
   }
}
