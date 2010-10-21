/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCommitRequest;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Megumi Telles
 * @author Ryan D. Brooks
 */
public final class CommitBranchHttpRequestOperation extends AbstractOperation {
   private final User user;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final boolean isArchiveAllowed;

   private static final String ARTIFACT_CHANGES =
      "SELECT av.art_id, txs1.branch_id FROM osee_txs txs1, osee_artifact av WHERE txs1.branch_id = ? AND txs1.transaction_id = ? AND txs1.gamma_id = av.gamma_id " + "UNION ALL " + "SELECT art.art_id, txs2.branch_id FROM osee_txs txs2, osee_relation_link rel, osee_artifact art WHERE txs2.branch_id = ? and txs2.transaction_id = ? AND txs2.gamma_id = rel.gamma_id AND (rel.a_art_id = art.art_id OR rel.b_art_id = art.art_id) " + "UNION ALL " + "SELECT att.art_id, txs3.branch_id FROM osee_txs txs3, osee_attribute att WHERE txs3.branch_id = ? AND txs3.transaction_id = ? AND txs3.gamma_id = att.gamma_id";

   public CommitBranchHttpRequestOperation(User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) {
      super("Commit " + sourceBranch, Activator.PLUGIN_ID);
      this.user = user;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.isArchiveAllowed = isArchiveAllowed;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.BRANCH_COMMIT.name());

      BranchCommitRequest requestData =
         new BranchCommitRequest(user.getArtId(), sourceBranch.getId(), destinationBranch.getId(), isArchiveAllowed);

      BranchCommitResponse response =
         HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.BRANCH_COMMIT_REQUEST,
            requestData, CoreTranslatorId.BRANCH_COMMIT_RESPONSE);

      if (response != null) {
         handleResponse(response, sourceBranch);
      }
   }

   private void handleResponse(BranchCommitResponse response, Branch sourceBranch) throws OseeCoreException {
      TransactionRecord newTransaction = response.getTransaction();
      Activator.getInstance().getAccessControlService().removePermissions(sourceBranch);
      // Update commit artifact cache with new information
      if (sourceBranch.getAssociatedArtifactId() > 0) {
         TransactionManager.cacheCommittedArtifactTransaction(BranchManager.getAssociatedArtifact(sourceBranch),
            newTransaction);
      }
      BranchManager.getCache().reloadCache();

      reloadCommittedArtifacts(newTransaction);

      OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Committed, sourceBranch.getGuid()),
         sourceBranch.getId());
   }

   private void reloadCommittedArtifacts(TransactionRecord newTransaction) throws OseeCoreException {
      Branch txBranch = BranchManager.getBranch(newTransaction.getBranchId());
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         Object[] queryData =
            new Object[] {
               newTransaction.getBranchId(),
               newTransaction.getId(),
               newTransaction.getBranchId(),
               newTransaction.getId(),
               newTransaction.getBranchId(),
               newTransaction.getId()};
         chStmt.runPreparedQuery(ARTIFACT_CHANGES, queryData);
         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            ArtifactQuery.reloadArtifactFromId(artId, txBranch);
         }
      } finally {
         chStmt.close();
      }
   }
}