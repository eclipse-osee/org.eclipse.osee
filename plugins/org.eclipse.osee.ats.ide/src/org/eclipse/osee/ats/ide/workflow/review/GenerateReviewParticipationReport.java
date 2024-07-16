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

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.dialog.UserListDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerReviewRoleColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerSmaCompletedDateColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class GenerateReviewParticipationReport extends XNavigateItemAction {

   private static final String MASS_XVIEWER_CUSTOMIZE_NAMESPACE = "ReviewParticipationReport";

   private AtsUser selectedUser = null;

   public GenerateReviewParticipationReport() {
      super("Generate Review Participation Report", AtsImage.REPORT, AtsNavigateViewItems.REVIEW);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      AtsUser useUser = null;

      IAtsUserService userService = AtsApiService.get().getUserService();
      UserListDialog dialog = new UserListDialog(Displays.getActiveShell(), "Select User", Active.Active);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == 0) {
         if (dialog.getSelectedFirst() == null) {
            AWorkbench.popup("ERROR", "Must select user");
            return;
         }
         useUser = userService.getUserById(dialog.getSelection());
      }

      selectedUser = useUser;

      boolean forcePend = Arrays.asList(tableLoadOptions).contains(TableLoadOption.ForcePend);
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
   /**
    * @author Donald G. Dunne
    */
   private class ParticipationReportJob extends Job {

      private final AtsUser user;
      private final boolean forcePend;

      public ParticipationReportJob(String title, AtsUser user, boolean forcePend) {
         super(title);
         this.user = user;
         this.forcePend = forcePend;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         try {
            Set<Artifact> reviews = getResults();

            final MassArtifactEditorInput input = new MassArtifactEditorInput(
               getName() + " as of " + DateUtil.getDateNow(), reviews, new ReviewParticipationXViewerFactory(user));
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

   public Set<Artifact> getResults() {
      Set<Artifact> reviews = new HashSet<>();
      IAtsQuery query = AtsApiService.get().getQueryService().createQuery(WorkItemType.Review);
      query.andAssigneeWas(selectedUser);
      reviews.addAll(Collections.castAll(query.getResultArtifacts().getList()));

      query = AtsApiService.get().getQueryService().createQuery(WorkItemType.Review);
      query.andAssignee(selectedUser);
      reviews.addAll(Collections.castAll(query.getResultArtifacts().getList()));
      return reviews;
   }

   public class ReviewParticipationXViewerFactory extends SkynetXViewerFactory {

      public ReviewParticipationXViewerFactory(AtsUser user) {
         super(MASS_XVIEWER_CUSTOMIZE_NAMESPACE,
            new OseeTreeReportAdapter("Table Report - Review Participation Report"));
         registerColumns(ArtifactTypeColumn.getInstance());
         registerColumns(WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.AtsIdColumnShow));
         registerColumns(new XViewerAtsAttributeValueColumn(AtsColumnToken.LegacyPcrIdColumn));
         registerColumns(WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.StateColumn));
         registerColumns(new XViewerSmaCompletedDateColumn("Completed"));
         registerColumns(new XViewerReviewRoleColumn(user));
         registerColumns(RelatedToStateColumn.getInstance());
         registerColumns(new ArtifactNameColumn(true));
         registerColumns(new IdColumn(true));
         registerAllAttributeColumns();
      }

   }

   public void setSelectedUser(AtsUser selectedUser) {
      this.selectedUser = selectedUser;
   }
}
