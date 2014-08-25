/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.branch;

import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.BranchCommitRequest;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class CommitBranchCallable extends AbstractBranchCallable<BranchCommitRequest, BranchCommitResponse> {

   public CommitBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(context, req, resp, translationService, orcsApi, "text/xml", CoreTranslatorId.BRANCH_COMMIT_REQUEST,
         CoreTranslatorId.BRANCH_COMMIT_RESPONSE);
   }

   @Override
   protected BranchCommitResponse executeCall(BranchCommitRequest request) throws Exception {
      IOseeBranch source = getBranchFromUuid(request.getSourceBranchId());
      IOseeBranch destination = getBranchFromUuid(request.getDestinationBranchId());

      ArtifactReadable committer = getArtifactById(request.getUserArtId());

      Callable<TransactionRecord> callable = getBranchOps().commitBranch(committer, source, destination);
      TransactionRecord transactionRecord = callAndCheckForCancel(callable);

      BranchCommitResponse responseData = new BranchCommitResponse();
      responseData.setTransaction(transactionRecord);

      if (request.isArchiveAllowed()) {
         Callable<Void> archiveCallable = getBranchOps().archiveUnarchiveBranch(source, ArchiveOperation.ARCHIVE);
         callAndCheckForCancel(archiveCallable);
      }
      return responseData;
   }

}
