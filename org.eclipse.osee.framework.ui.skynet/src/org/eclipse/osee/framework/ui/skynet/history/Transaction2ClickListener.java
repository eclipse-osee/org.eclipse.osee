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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;

/**
 * @author Ryan D. Brooks
 */
public class Transaction2ClickListener implements IDoubleClickListener {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(Transaction2ClickListener.class);
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

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
         logger.log(Level.WARNING, selectedItem.getClass().getName());
      }
   }

   private void openArtifact(TransactionData transactionData) {
      try {
         TransactionId transactionId =
               transactionIdManager.getPossiblyEditableTransactionIfFromCache(transactionData.getTransactionNumber());
         Artifact artifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(transactionData.getAssociatedArtId(),
                     transactionId);
         ArtifactEditor.editArtifact(artifact);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }
}