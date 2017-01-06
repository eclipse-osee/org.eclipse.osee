/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.action;

import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author David W. Miller
 */
public class WasIsCompareEditorChangeAction extends WasIsCompareEditorAction {

   @Override
   public void run() {
      try {
         ISelection selection =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
            if (localChanges.isEmpty() || localChanges.size() > 1) {
               AWorkbench.popup("Can only show Was/Is for single selection");
               return;
            }
            Change change = localChanges.iterator().next();
            if (change.getModificationType() != ModificationType.MODIFIED) {
               AWorkbench.popup(String.format("Can only show Was/Is for modified attributes, not %s",
                  change.getModificationType().toString()));
               return;
            }
            List<Artifact> artifactsFromStructuredSelection =
               Handlers.getArtifactsFromStructuredSelection(structuredSelection);
            Artifact artifact = artifactsFromStructuredSelection.iterator().next();
            TransactionId headTx = BranchManager.getBaseTransaction(artifact.getBranch());

            String was = change.getWasValue();
            AttributeId attrId = ((AttributeChange) change).getAttrId();
            if (!Strings.isValid(was) && change instanceof AttributeChange) {
               if (headTx != null && headTx.isValid()) {
                  was = loadAttributeValue(attrId, headTx, artifact);
               }
            }
            String is = change.getIsValue();
            TransactionId currentTxId = change.getTxDelta().getStartTx();
            if (!Strings.isValid(is) && change instanceof AttributeChange) {
               if (currentTxId != null && currentTxId.isValid()) {
                  is = loadAttributeValue(attrId, currentTxId, artifact);
               }
            }
            CompareHandler compareHandler = new CompareHandler(String.format("Compare [%s]", change),
               new CompareItem(String.format("Was [Transaction: %s]", headTx), was, System.currentTimeMillis(), true,
                  "was_trans_" + headTx),
               new CompareItem(String.format("Is [Transaction: %s]", currentTxId), is, System.currentTimeMillis(), true,
                  "is_trans_" + currentTxId),
               null);
            compareHandler.compare();
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
