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
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Megumi Telles
 */
public class HttpCommitDataRequester {

   private static final String ARTIFACT_CHANGES =
         "SELECT av.art_id, txs1.branch_id FROM osee_txs txs1, osee_artifact_version av WHERE txs1.branch_id = ? AND txs1.transaction_id = ? AND txs1.gamma_id = av.gamma_id UNION ALL SELECT art.art_id, txs2.branch_id FROM osee_txs txs2, osee_relation_link rel, osee_artifact art WHERE txs2.branch_id = ? and txs2.transaction_id = ? AND txs2.gamma_id = rel.gamma_id AND (rel.a_art_id = art.art_id OR rel.b_art_id = art.art_id) UNION ALL SELECT att.art_id, txs3.branch_id FROM osee_txs txs3, osee_attribute att WHERE txs3.branch_id = ? AND txs3.transaction_id = ? AND txs3.gamma_id = att.gamma_id";

   public static void commitBranch(IProgressMonitor monitor, User user, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.BRANCH_COMMIT.name());

      BranchCommitRequest requestData =
            new BranchCommitRequest(user.getArtId(), sourceBranch.getId(), destinationBranch.getId(), isArchiveAllowed);

      BranchCommitResponse response =
            HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters,
                  CoreTranslatorId.BRANCH_COMMIT_REQUEST, requestData, CoreTranslatorId.BRANCH_COMMIT_RESPONSE);

      if (response != null) {
         TransactionRecord newTransaction = response.getTransaction();
         AccessControlManager.removeAllPermissionsFromBranch(null, sourceBranch);
         // Update commit artifact cache with new information
         if (sourceBranch.getAssociatedArtifact().getArtId() > 0) {
            TransactionManager.cacheCommittedArtifactTransaction((IArtifact) sourceBranch.getAssociatedArtifact(),
                  newTransaction);
         }
         BranchManager.getCache().reloadCache();
         // reload the committed artifacts since the commit changed them on the destination branch
         Object[] queryData =
               new Object[] {newTransaction.getBranchId(), newTransaction.getId(), newTransaction.getBranchId(),
                     newTransaction.getId(), newTransaction.getBranchId(), newTransaction.getId()};
         ArtifactLoader.getArtifacts(ARTIFACT_CHANGES, queryData, 400, ArtifactLoad.FULL, true, null, true);
         // Kick commit event
         OseeEventManager.kickBranchEvent(HttpCommitDataRequester.class, BranchEventType.Committed,
               sourceBranch.getId());
      }
   }
}
