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
import java.util.Date;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.dialog.ActionableItemListDialog;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewItem extends XNavigateItemAction {

   /**
    * @param parent
    */
   public NewPeerToPeerReviewItem(XNavigateItem parent) {
      super(parent, "New Stand-alone Peer To Peer Review");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException {
      final ActionableItemListDialog ld = new ActionableItemListDialog(Active.Both);
      ld.setMessage("Select Actionable Items to Review\n\nNOTE: To create a review against " + "an Action and Team Workflow\nopen the object in ATS and select the " + "review to create from the editor.");
      int result = ld.open();
      if (result == 0) {
         final EntryDialog ed = new EntryDialog("Peer Review Title", "Enter Peer Review Title");
         if (ed.open() == 0) {
            try {
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
                        @Override
                        protected void handleTxWork() throws OseeCoreException, SQLException {
                           PeerToPeerReviewArtifact peerArt =
                                 ReviewManager.createNewPeerToPeerReview(null, ed.getEntry(), null,
                                       SkynetAuthentication.getUser(), new Date());
                           peerArt.getActionableItemsDam().setActionableItems(ld.getSelected());
                           peerArt.persistAttributesAndRelations();
                           AtsLib.openAtsAction(peerArt, AtsOpenOption.OpenAll);
                        }
                     };
               txWrapper.execute();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      }
   }
}
