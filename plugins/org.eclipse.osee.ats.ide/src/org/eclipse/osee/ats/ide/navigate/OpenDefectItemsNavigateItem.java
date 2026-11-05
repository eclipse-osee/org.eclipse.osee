/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.review.ReviewDefectData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common action support for opening a user's resolved but not closed peer review defects.
 */
abstract class OpenDefectItemsNavigateItem extends XNavigateItemAction {

   protected OpenDefectItemsNavigateItem(String name) {
      super(name, AtsImage.DEFECT, AtsNavigateViewItems.REVIEW);
   }

   protected void openDefectItems(AtsUser user, String confirmationMessage, String resultsEditorName,
      TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), confirmationMessage)) {
         return;
      }
      ReviewDefectData data = new ReviewDefectData();
      data.setUserArtifactId(user.getArtifactId());
      Jobs.startJob(new Job("Loading " + getName()) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            ReviewDefectData result = AtsApiService.get().getServerEndpoints().getReviewEp().getOpenDefectItems(data);
            if (result.getRd().isErrors()) {
               Displays.ensureInDisplayThread(() -> XResultDataUI.report(result.getRd(), getName()));
            }
            ReviewDefectResultsEditor.open(result, resultsEditorName);
            return Status.OK_STATUS;
         }
      }, true);
   }
}
