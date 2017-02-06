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
package org.eclipse.osee.framework.ui.skynet.action;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class WasIsCompareEditorAction extends Action {

   private static String ATTRIBUTE_TRANSACTIONS_QUERY_DESC =
      "SELECT txs.transaction_id, txs.gamma_id FROM osee_attribute atr, osee_txs txs WHERE atr.attr_id = ? AND atr.gamma_id = txs.gamma_id AND txs.branch_id = ? and transaction_id < ? order by transaction_id desc";

   public WasIsCompareEditorAction() {
      this("View Was/Is Comparison");
   }

   public WasIsCompareEditorAction(String name) {
      super(name);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.COMPARE_DOCUMENTS);
   }

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
            TransactionId transactionId = change.getTxDelta().getEndTx();
            List<Artifact> artifactsFromStructuredSelection =
               Handlers.getArtifactsFromStructuredSelection(structuredSelection);
            Artifact artifact = artifactsFromStructuredSelection.iterator().next();

            String was = change.getWasValue();
            AttributeId attrId = ((AttributeChange) change).getAttrId();
            TransactionId previousTransaction = getPreviousTransaction(artifact.getBranch(), attrId, transactionId);
            if (!Strings.isValid(was) && change instanceof AttributeChange) {
               if (previousTransaction.isValid()) {
                  was = loadAttributeValue(attrId, previousTransaction, artifact);
               }
            }

            String is = change.getIsValue();
            if (!Strings.isValid(is) && change instanceof AttributeChange) {
               is = loadAttributeValue(attrId, transactionId, artifact);
            }

            was = performanStringManipulation(was);
            is = performanStringManipulation(is);

            CompareHandler compareHandler = new CompareHandler(String.format("Compare [%s]", change),
               new CompareItem(String.format("Was [Transaction: %s]", previousTransaction), was,
                  System.currentTimeMillis(), true, "was_trans_" + previousTransaction),
               new CompareItem(String.format("Is [Transaction: %s]", transactionId), is, System.currentTimeMillis(),
                  true, "is_trans_" + previousTransaction),
               null);
            compareHandler.compare();
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Override to perform string manipulation before opening in compare editor
    */
   protected String performanStringManipulation(String str) {
      return str;
   }

   private TransactionId getPreviousTransaction(BranchId branch, AttributeId attrId, TransactionId transactionId) {
      try (JdbcStatement chStmt = ConnectionHandler.getStatement()) {
         chStmt.runPreparedQuery(ATTRIBUTE_TRANSACTIONS_QUERY_DESC, attrId, branch, transactionId);
         if (chStmt.next()) {
            return TransactionId.valueOf(chStmt.getLong("transaction_id"));
         }
      }
      return TransactionId.SENTINEL;
   }

   protected String loadAttributeValue(AttributeId attrId, TransactionId transactionId, Artifact artifact) {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri =
         UriBuilder.fromUri(appServer).path("orcs").path("branch").path(String.valueOf(artifact.getBranchId())).path(
            "artifact").path(artifact.getIdString()).path("attribute").path(String.valueOf(attrId)).path(
               "version").path(String.valueOf(transactionId)).path("text").build();
      try {
         return JaxRsClient.newClient().target(uri).request(MediaType.TEXT_PLAIN).get(String.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }

   }

   protected static ISelectionProvider getSelectionProvider() {
      ISelectionProvider selectionProvider = null;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isStarting() && !workbench.isClosing()) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  selectionProvider = site.getSelectionProvider();
               }
            }
         }
      }
      return selectionProvider;
   }

   public static boolean isEnabledStatic() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean isEnabled = false;

      ISelectionProvider selectionProvider = getSelectionProvider();
      if (selectionProvider != null) {
         ISelection selection = selectionProvider.getSelection();
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            isEnabled = structuredSelection.size() == 1;
            if (isEnabled) {
               List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
               if (localChanges.isEmpty() || localChanges.size() > 1) {
                  isEnabled = false;
               } else {
                  Change change = localChanges.iterator().next();
                  if (change instanceof AttributeChange) {
                     isEnabled = true;
                  } else {
                     isEnabled = false;
                  }
               }
            }
         }
      }
      return isEnabled;
   }
}
