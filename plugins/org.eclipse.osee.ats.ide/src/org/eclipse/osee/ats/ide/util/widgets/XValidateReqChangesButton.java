/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Date;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgramManager;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonWithLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XValidateReqChangesButton extends XButtonWithLabelDam {

   private IAtsTeamWorkflow teamWf;
   private IAtsProgramManager programMgr;

   public XValidateReqChangesButton() {
      super("Validate Requirement Changes", "Click to validate requirement changes",
         ImageManager.getImage(AtsImage.PLAY_GREEN));
      addXModifiedListener(listener);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      // Right-click runs validate without logging to artifact
      getLabelWidget().addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (event.button == 3) {
               handleSelection(false);
            }
         }
      });
   }

   @Override
   protected String getResultsText() {
      Date ranDate = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.ValidateChangesRanDate, null);
      if (ranDate != null) {
         AtsUser user = AtsApiService.get().getUserService().getUserByUserId(
            AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
               AtsAttributeTypes.ValidateChangesRanBy, AtsCoreUsers.UNASSIGNED_USER.getUserId()));
         labelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         return String.format("ran by %s on %s", user.getName(), DateUtil.getDateNow(ranDate, DateUtil.MMDDYYHHMM));
      }
      resultsLabelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      return "Not Yet Run";
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         handleSelection(true);
      }

   };

   private void handleSelection(final boolean logRun) {
      try {
         programMgr = AtsApiService.get().getProgramService().getProgramManager(teamWf);
         if (programMgr == null) {
            AWorkbench.popup("Workflow Not Configured",
               "Team Workflow\n\n%s\n\nnot configured for\n\nValidate Requirement Changes ", teamWf.toStringWithId());
            return;
         }
         boolean editable = AtsApiService.get().getBranchService().isWorkingBranchInWork(
            teamWf) || AtsApiService.get().getBranchService().isCommittedBranchExists(teamWf);
         if (!editable) {
            AWorkbench.popup("No branch or commit to run against.");
            return;
         }

         if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Validate Requirement Changes", String.format(
            "Run programatic checks against Requirement changes?%s", (logRun ? "" : "\n\n(Report Only)")))) {
            return;
         }
         IOperation operation = programMgr.createValidateReqChangesOp(teamWf);
         Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               if (logRun) {
                  IAtsChangeSet changes = AtsApiService.get().createChangeSet("Log Validate Changes Run");
                  changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ValidateChangesRanBy,
                     AtsApiService.get().getUserService().getCurrentUserId());
                  changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ValidateChangesRanDate, new Date());
                  changes.execute();
               }
            }
         });

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   };

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return (TeamWorkFlowArtifact) super.getArtifact();
   }

   @Override
   public boolean isEditable() {
      return teamWf != null;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact instanceof TeamWorkFlowArtifact) {
         this.teamWf = (TeamWorkFlowArtifact) artifact;
      }
   }

   public boolean isDisposed() {
      return !Widgets.isAccessible(labelWidget);
   }

}
