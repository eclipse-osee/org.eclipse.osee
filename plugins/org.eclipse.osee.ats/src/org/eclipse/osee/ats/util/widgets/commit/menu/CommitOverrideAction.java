/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets.commit.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CommitOverrideAction extends Action {

   private final AtsApi atsApi;
   private final IAtsTeamWorkflow teamWf;
   private final IOseeBranch branch;

   public CommitOverrideAction(IAtsTeamWorkflow teamWf, IOseeBranch branch, AtsApi atsApi) {
      super(String.format("Override Commit for [%s]", branch.getName()));
      this.teamWf = teamWf;
      this.branch = branch;
      this.atsApi = atsApi;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN);
   }

   @Override
   public void run() {
      try {
         EntryDialog dialog = new EntryDialog("Override Commit", getText() + "\n\nEnter Reason");
         if (dialog.open() == 0) {
            Result result =
               atsApi.getBranchService().getCommitOverrideOps().setCommitOverride(teamWf, branch, dialog.getEntry());
            if (result.isFalse()) {
               AWorkbench.popup(getText(), result.getText());
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
