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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCommitRequest;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Megumi Telles
 */
public class HttpCommitDataRequester {

   private static final String ARTIFACT_CHANGES =
      "SELECT av.art_id, txs1.branch_id FROM osee_txs txs1, osee_artifact av WHERE txs1.branch_id = ? AND txs1.transaction_id = ? AND txs1.gamma_id = av.gamma_id " + "UNION ALL " + "SELECT art.art_id, txs2.branch_id FROM osee_txs txs2, osee_relation_link rel, osee_artifact art WHERE txs2.branch_id = ? and txs2.transaction_id = ? AND txs2.gamma_id = rel.gamma_id AND (rel.a_art_id = art.art_id OR rel.b_art_id = art.art_id) " + "UNION ALL " + "SELECT att.art_id, txs3.branch_id FROM osee_txs txs3, osee_attribute att WHERE txs3.branch_id = ? AND txs3.transaction_id = ? AND txs3.gamma_id = att.gamma_id";

   public static void commitBranch(IProgressMonitor monitor, User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) throws OseeCoreException {
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

   private static IAccessControlService getAccessControlService() {
      return Activator.getInstance().getAccessControlService();
   }

   private static void handleResponse(BranchCommitResponse response, Branch sourceBranch) throws OseeCoreException {
      TransactionRecord newTransaction = response.getTransaction();
      getAccessControlService().removePermissions(sourceBranch);
      // Update commit artifact cache with new information
      if (sourceBranch.getAssociatedArtifactId() > 0) {
         TransactionManager.cacheCommittedArtifactTransaction(BranchManager.getAssociatedArtifact(sourceBranch),
            newTransaction);
      }
      BranchManager.getCache().reloadCache();

      reloadCommittedArtifacts(newTransaction);
      kickCommitEvent(sourceBranch);
   }

   private static void reloadCommittedArtifacts(TransactionRecord newTransaction) throws OseeCoreException {
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

   private static void kickCommitEvent(Branch sourceBranch) {
      try {
         OseeEventManager.kickBranchEvent(HttpCommitDataRequester.class, new BranchEvent(BranchEventType.Committed,
            sourceBranch.getGuid()), sourceBranch.getId());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}
