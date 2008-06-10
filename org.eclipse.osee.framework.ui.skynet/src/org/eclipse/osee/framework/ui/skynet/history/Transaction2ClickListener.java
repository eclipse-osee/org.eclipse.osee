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
package org.eclipse.osee.framework.ui.skynet.history;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks
 */
public class Transaction2ClickListener implements IDoubleClickListener {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
    */
   public void doubleClick(DoubleClickEvent event) {
      Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
      if (selectedItem instanceof TransactionData) {
         openArtifact((TransactionData) selectedItem);
      } else {
         OSEELog.logWarning(SkynetGuiPlugin.class, "Selected item not of expected type", true);
      }
   }

   private void openArtifact(TransactionData transactionData) {
      try {
         TransactionId transactionId =
               TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(
                     transactionData.getTransactionNumber());
         Artifact artifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(transactionData.getAssociatedArtId(),
                     transactionId);
         ArtifactEditor.editArtifact(artifact);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}