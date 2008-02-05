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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class RevertArtifactHandler extends AbstractSelectionChangedHandler {
   private static final RevisionManager myRevisionManager = RevisionManager.getInstance();
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();
   private ArtifactChange artifactChange;

   public RevertArtifactHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      // This is serious stuff, make sure the user understands the impact.
      if (MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(),
            "Confirm Revert of " + artifactChange.getName(),
            "All attribute changes for the artifact and all link changes that involve the artifact on this branch will be reverted." + "\n\nTHIS IS IRREVERSIBLE" + "\n\nOSEE must be restarted after all reverting is finished to see the results")) {

         TransactionId toTransactionId =
               artifactChange.getModType() == ModificationType.DELETE ? artifactChange.getDeletedTransactionId() : artifactChange.getToTransactionId();
         Jobs.startJob(new RevertJob(artifactChange.getName(), artifactChange.getArtId(),
               artifactChange.getFromTransactionId(), toTransactionId));
      }
      return null;
   }
   private class RevertJob extends Job {

      private final int artId;
      private final TransactionId baseTransactionId;
      private final TransactionId toTransactionId;

      /**
       * @param name
       * @param artId
       */
      public RevertJob(String name, int artId, TransactionId baseTransactionId, TransactionId toTransactionId) {
         super("Reverting Artifact " + name);
         this.artId = artId;
         this.baseTransactionId = baseTransactionId;
         this.toTransactionId = toTransactionId;
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            new RevertDbTx(getName(), artId, baseTransactionId, toTransactionId, monitor).execute();
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }
         return Status.OK_STATUS;
      }
   }

   private final class RevertDbTx extends AbstractDbTxTemplate {

      private final IProgressMonitor monitor;
      private final int artId;
      private final String txName;
      private final TransactionId baseTransactionId;
      private final TransactionId toTransactionId;

      public RevertDbTx(String txName, int artId, TransactionId baseTransactionId, TransactionId toTransactionId, IProgressMonitor monitor) {
         this.monitor = monitor;
         this.txName = txName;
         this.artId = artId;
         this.baseTransactionId = baseTransactionId;
         this.toTransactionId = toTransactionId;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         monitor.beginTask(txName, 7);

         monitor.subTask("Calculating change set");

         Collection<RevisionChange> revisionChanges =
               myRevisionManager.getAllTransactionChanges(OUTGOING, baseTransactionId.getTransactionNumber(),
                     toTransactionId.getTransactionNumber(), artId, null);
         int worstSize = revisionChanges.size();
         Collection<Long> attributeGammas = new ArrayList<Long>(worstSize);
         Collection<Long> linkGammas = new ArrayList<Long>(worstSize);
         Collection<Long> artifactGammas = new ArrayList<Long>(worstSize);
         Collection<Long> allGammas = new ArrayList<Long>(worstSize);

         // Categorize all of the changes
         for (RevisionChange change : revisionChanges) {
            if (change instanceof AttributeChange) {
               attributeGammas.add(change.getGammaId());
            } else if (change instanceof RelationLinkChange) {
               linkGammas.add(change.getGammaId());
            } else if (change instanceof ArtifactChange) {
               artifactGammas.add(change.getGammaId());
            }
            allGammas.add(change.getGammaId());
         }

         monitor.worked(1);
         isCanceled();

         monitor.subTask("Cleaning up bookkeeping data");
         if (allGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + TRANSACTIONS_TABLE + " WHERE " + TRANSACTIONS_TABLE.column("gamma_id") + " IN" + Collections.toString(
                  allGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting Artifact gammas");
         if (artifactGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + ARTIFACT_VERSION_TABLE + " WHERE " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  artifactGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting attributes");
         if (attributeGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  attributeGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Reverting links");
         if (linkGammas.size() > 0) {
            ConnectionHandler.runPreparedUpdate("DELETE FROM " + RELATION_LINK_VERSION_TABLE + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " IN " + Collections.toString(
                  linkGammas, "(", ",", ")"));
         }
         monitor.worked(1);
         isCanceled();

         monitor.subTask("Cleaning up empty transactions");
         ConnectionHandler.runPreparedUpdate(
               "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " NOT IN " + "(SELECT " + TRANSACTIONS_TABLE.column("transaction_id") + " FROM " + TRANSACTIONS_TABLE + ")",
               SQL3DataType.INTEGER, baseTransactionId.getBranch().getBranchId());
         monitor.worked(1);

      }

      private boolean isCanceled() throws Exception {
         boolean toReturn = monitor.isCanceled();
         if (false != toReturn) {
            throw new IllegalStateException("User Cancelled Operation");
         }
         return toReturn;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         monitor.done();
      }

   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            List<ArtifactChange> artifactChanges =
                  Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            if (artifactChanges.isEmpty()) {
               return false;
            }

            artifactChange = artifactChanges.get(0);

            isEnabled =
                  myAccessControlManager.checkObjectPermission(artifactChange.getArtifact(), PermissionEnum.WRITE);
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
      return isEnabled;
   }
}
