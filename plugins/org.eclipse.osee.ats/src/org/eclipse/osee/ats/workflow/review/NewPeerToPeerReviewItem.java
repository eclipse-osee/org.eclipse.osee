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

package org.eclipse.osee.ats.workflow.review;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewItem extends XNavigateItemAction {

   public NewPeerToPeerReviewItem(XNavigateItem parent) {
      super(parent, "New Stand-alone Peer To Peer Review", AtsImage.PEER_REVIEW);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         NewPeerReviewDialog dialog = new NewPeerReviewDialog("Add New Stand-alone Peer to Peer Review",
            "Enter Title, Select Review Type and select Actionable Item", null, null,
            ActionableItems.getTopLevelActionableItems(Active.Active, AtsClientService.get()));
         if (dialog.open() == 0) {
            if (!Strings.isValid(dialog.getReviewTitle())) {
               AWorkbench.popup("ERROR", "Must enter review title");
               return;
            }
            ReviewFormalType reviewType = null;
            if (Strings.isValid(dialog.getReviewFormalType())) {
               reviewType = ReviewFormalType.valueOf(dialog.getReviewFormalType());
            }
            NewPeerToPeerReviewJob job = new NewPeerToPeerReviewJob(null,
               dialog.getSelectedActionableItems().iterator().next(), dialog.getReviewTitle(), null, null, reviewType);
            job.setUser(true);
            job.setPriority(Job.LONG);
            job.schedule();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
