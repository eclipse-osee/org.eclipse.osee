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
package org.eclipse.osee.ats.ide.util.widgets.xx;

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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXValidateReqChangesButtonWfeWidget extends XXButtonHyperWidget {

   public static WidgetId ID = WidgetIdAts.XXValidateReqChangesButtonWfeWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private IAtsProgramManager programMgr;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXValidateReqChangesButtonWfeWidget() {
      super(ID, "Validate Requirement Changes");
      setToolTip("Click to validate requirement changes");
      setOseeImage(AtsImage.PLAY_GREEN);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getDisplayValue() {
      Date ranDate = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(xxWid.getTeamWf(),
         AtsAttributeTypes.ValidateChangesRanDate, null);
      if (ranDate != null) {
         AtsUser user = AtsApiService.get().getUserService().getUserByUserId(
            AtsApiService.get().getAttributeResolver().getSoleAttributeValue(xxWid.getTeamWf(),
               AtsAttributeTypes.ValidateChangesRanBy, AtsCoreUsers.UNASSIGNED_USER.getUserId()));
         getValueWidget().setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         return String.format("ran by %s on %s", user.getName(), DateUtil.getDateNow(ranDate, DateUtil.MMDDYYHHMM));
      }
      getValueWidget().setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      return "Not Yet Run";
   }

   @Override
   public boolean handleSelection() {
      try {
         programMgr = AtsApiService.get().getProgramService().getProgramManager(xxWid.getTeamWf());
         if (programMgr == null) {
            AWorkbench.popup("Workflow Not Configured",
               "Team Workflow\n\n%s\n\nnot configured for\n\nValidate Requirement Changes ",
               xxWid.getTeamWf().toStringWithId());
            return false;
         }
         boolean editable = AtsApiService.get().getBranchService().isWorkingBranchInWork(
            xxWid.getTeamWf()) || AtsApiService.get().getBranchService().isCommittedBranchExists(xxWid.getTeamWf());
         if (!editable) {
            AWorkbench.popup("No branch or commit to run against.");
            return false;
         }

         if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Validate Requirement Changes",
            "Run programatic checks against Requirement changes?")) {
            return false;
         }
         IOperation operation = programMgr.createValidateReqChangesOp(xxWid.getTeamWf());
         Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               IAtsChangeSet changes = AtsApiService.get().createChangeSet("Log Validate Changes Run");
               changes.setSoleAttributeValue(xxWid.getTeamWf(), AtsAttributeTypes.ValidateChangesRanBy,
                  AtsApiService.get().getUserService().getCurrentUserId());
               changes.setSoleAttributeValue(xxWid.getTeamWf(), AtsAttributeTypes.ValidateChangesRanDate, new Date());
               changes.execute();
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   };

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public XResultData isValid() {
      // This is a required action button, not a value-backed widget. Its completion is represented by the
      // ValidateChangesRanBy attribute, so generic required-value validation does not apply.
      XResultData status = XWidgetStatus.OK;
      if (isRequiredEntry()) {
         try {
            if (getArtifact() != null) {
               if ((AtsApiService.get().getBranchService().isWorkingBranchInWork(xxWid.getTeamWf()) || //
                  AtsApiService.get().getBranchService().isCommittedBranchExists(xxWid.getTeamWf())) //
                  //
                  && !AtsApiService.get().getAttributeResolver().hasAttribute(xxWid.getTeamWf(),
                     AtsAttributeTypes.ValidateChangesRanBy)) {
                  status = new XResultData(XResultType.Error, "[" + getLabel() + "] Must Be Run");
               }
            }
         } catch (OseeCoreException ex) {
            status = new XResultData(XResultType.Error, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   public boolean isEditable() {
      return xxWid.getTeamWf() != null;
   }

   public boolean isDisposed() {
      return !Widgets.isAccessible(labelWidget);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
