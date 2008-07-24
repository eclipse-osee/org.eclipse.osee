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
import org.eclipse.osee.ats.util.xviewer.column.XViewerReviewRoleColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerSmaCompletedDateColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerSmaStateColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerAttributeSortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerGuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;
import org.eclipse.swt.SWT;
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
         if (ld.getResult().length == 0) {
            AWorkbench.popup("ERROR", "Must select user");
            return;
         }
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
            MyReviewWorkflowItem srch = new MyReviewWorkflowItem("", user, ReviewState.All);
            Collection<Artifact> reviewArts = srch.performSearchGetResults();
            MassArtifactEditorInput input =
                  new MassArtifactEditorInput(getName() + " as of " + XDate.getDateNow(), reviewArts, getColumns(user));
            MassArtifactEditor.editArtifacts(input);
         } catch (Exception ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }

   }

   private static List<XViewerColumn> getColumns(User user) throws OseeCoreException, SQLException {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      columns.add(new XViewerArtifactTypeColumn("Type", null));
      columns.add(new XViewerHridColumn("ID", null));
      columns.add(WorldXViewerFactory.Legacy_PCR_Col);
      columns.add(new XViewerSmaStateColumn(null));
      columns.add(new XViewerSmaCompletedDateColumn("Completed", null));
      columns.add(new XViewerReviewRoleColumn(null, user));
      columns.add(WorldXViewerFactory.Related_To_State_Col);
      columns.add(new XViewerArtifactNameColumn("Name", null));
      columns.add(new XViewerGuidColumn("Guid", null));
      for (AttributeType attributeType : AttributeTypeManager.getTypes(AtsPlugin.getAtsBranch())) {
         columns.add(new XViewerAttributeColumn("attr." + attributeType.getName(), attributeType.getName(),
               attributeType.getName(), 75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType), false,
               null));
      }
      return columns;
   }

}
