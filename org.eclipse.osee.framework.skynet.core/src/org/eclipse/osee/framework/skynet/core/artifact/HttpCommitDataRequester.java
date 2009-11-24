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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslationIds;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Megumi Telles
 */
public class HttpCommitDataRequester {

   private static final String ARTIFACT_CHANGES =
         "select av1.art_id, branch_id FROM osee_txs txs1, osee_tx_details txd1, osee_artifact_version av1 WHERE txs1.transaction_id = ? AND txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = av1.gamma_id UNION ALL SELECT ar1.art_id, branch_id FROM osee_txs txs2, osee_tx_details txd2, osee_relation_link rl1, osee_artifact ar1 WHERE (rl1.a_art_id = ar1.art_id OR rl1.b_art_id = ar1.art_id) AND txs2.transaction_id = ? AND txs2.transaction_id = txd2.transaction_id AND txs2.gamma_id = rl1.gamma_id";

   public static void commitBranch(IProgressMonitor monitor, User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.BRANCH_COMMIT.name());

      BranchCommitRequest requestData =
            new BranchCommitRequest(user, sourceBranch, destinationBranch, isArchiveAllowed);

      BranchCommitResponse response =
            HttpMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslationIds.BRANCH_COMMIT_REQUEST,
                  requestData, CoreTranslationIds.BRANCH_COMMIT_RESPONSE);

      TransactionRecord newTransaction = response.getTransaction();

      // Update commit artifact cache with new information
      if (sourceBranch.getAssociatedArtifact().getArtId() > 0) {
         TransactionManager.cacheCommittedArtifactTransaction((IArtifact) sourceBranch.getAssociatedArtifact(),
               newTransaction);
      }
      // reload the committed artifacts since the commit changed them on the destination branch
      Object[] queryData = new Object[] {newTransaction.getId(), newTransaction.getId()};
      ArtifactLoader.getArtifacts(ARTIFACT_CHANGES, queryData, 400, ArtifactLoad.FULL, true, null, true);
      // Kick commit event
      OseeEventManager.kickBranchEvent(HttpCommitDataRequester.class, BranchEventType.Committed, sourceBranch.getId());
   }
}
