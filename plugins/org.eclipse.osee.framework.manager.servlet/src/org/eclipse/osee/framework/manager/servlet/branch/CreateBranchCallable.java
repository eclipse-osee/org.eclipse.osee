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
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.message.BranchCreationResponse;
import org.eclipse.osee.framework.core.model.BranchReadable;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchCallable extends AbstractBranchCallable<BranchCreationRequest, BranchCreationResponse> {

   public CreateBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(context, req, resp, translationService, orcsApi, "text/xml", CoreTranslatorId.BRANCH_CREATION_REQUEST,
         CoreTranslatorId.BRANCH_CREATION_RESPONSE);
   }

   @Override
   protected BranchCreationResponse executeCall(BranchCreationRequest request) throws Exception {
      CreateBranchData createData = new CreateBranchData();

      createData.setGuid(request.getBranchGuid());
      createData.setName(request.getBranchName());
      if (Long.valueOf(request.getBranchUuid()) > 0) {
         createData.setUuid(request.getBranchUuid());
      }
      createData.setBranchType(request.getBranchType());
      createData.setCreationComment(request.getCreationComment());

      createData.setFromTransaction(TokenFactory.createTransaction(request.getSourceTransactionId()));

      ArtifactReadable createdBy = getArtifactById(request.getAuthorId());
      ArtifactReadable associatedWith = getArtifactById(request.getAssociatedArtifactId());

      createData.setUserArtifact(createdBy);
      createData.setAssociatedArtifact(associatedWith);

      createData.setMergeDestinationBranchId(request.getMergeDestinationBranchId());
      createData.setMergeAddressingQueryId(request.getMergeAddressingQueryId());
      createData.setTxCopyBranchType(request.txIsCopied());
      Callable<BranchReadable> callable;

      callable = getBranchOps().createBranch(createData);

      BranchReadable newBranch = callAndCheckForCancel(callable);

      BranchCreationResponse creationResponse = new BranchCreationResponse(newBranch.getId());
      return creationResponse;
   }

}
