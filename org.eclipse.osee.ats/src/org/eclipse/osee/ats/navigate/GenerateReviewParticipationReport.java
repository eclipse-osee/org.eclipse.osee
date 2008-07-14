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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerReviewCompletionDateColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerSmaStateColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerUserRoleColumn;
import org.eclipse.osee.ats.world.AtsXColumn;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerGuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;
import org.eclipse.swt.widgets.Display;

/**
 * Donald G. Dunne
 */
public class GenerateReviewParticipationReport extends XNavigateItemAction {

   public GenerateReviewParticipationReport(XNavigateItem parent) {
      super(parent, "Generate Review Participation Report");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
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
            String title = getName() + " - " + XDate.getDateNow(XDate.MMDDYYHHMM);
            MyReviewWorkflowItem srch = new MyReviewWorkflowItem("", user, ReviewState.All);
            Collection<Artifact> reviewArts = srch.performSearchGetResults();
            MassArtifactEditorInput input =
                  new MassArtifactEditorInput(title + " as of " + XDate.getDateNow(), reviewArts,
                        getXViewerColumns(user));
            MassArtifactEditor.editArtifacts(input);
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }

   }

   private static List<XViewerColumn> getXViewerColumns(User user) {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      columns.add(new XViewerArtifactTypeColumn("Type", null, 0));
      columns.add(new XViewerHridColumn("ID", null, 0));
      columns.add(new XViewerSmaStateColumn(null, 0));
      columns.add(AtsXColumn.Legacy_PCR_Col.getXViewerAttributeColumn(true));
      columns.add(new XViewerUserRoleColumn("Role", null, 0, user));
      columns.add(new XViewerReviewCompletionDateColumn(null, 0));
      columns.add(AtsXColumn.Related_To_State_Col.getXViewerAttributeColumn(true));
      columns.add(new XViewerArtifactNameColumn("Title", null, 0));
      columns.add(new XViewerGuidColumn(null, 0));
      return columns;
   }

}
