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

import java.util.Date;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.ActionableItemListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewItem extends XNavigateItemAction {

   public NewPeerToPeerReviewItem(XNavigateItem parent) {
      super(parent, "New Stand-alone Peer To Peer Review", AtsImage.PEER_REVIEW);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      final ActionableItemListDialog ld = new ActionableItemListDialog(Active.Active,
         "Select Actionable Items to Review\n\nNOTE: To create a review against " + "an Action and Team Workflow\nopen the object in ATS and select the " + "review to create from the editor.");
      int result = ld.open();
      if (result == Window.OK) {
         final EntryDialog ed = new EntryDialog("Peer Review Title", "Enter Peer Review Title");
         if (ed.open() == Window.OK) {
            try {
               if (ld.getSelected().isEmpty()) {
                  AWorkbench.popup("Must select at least one Actionable Item");
                  return;
               }

               AtsChangeSet changes = new AtsChangeSet("New Stand-alone Peer To Peer Review");
               PeerToPeerReviewArtifact peerArt =
                  PeerToPeerReviewManager.createNewPeerToPeerReview(ld.getSelected().iterator().next(), ed.getEntry(),
                     null, new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
               changes.execute();

               AtsUtil.openATSAction(peerArt, AtsOpenOption.OpenAll);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }
}
