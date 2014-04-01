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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.message.BranchCreationResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan D. Brooks
 */
public final class CreateBranchHttpRequestOperation extends AbstractOperation {
   private final BranchType branchType;
   private final TransactionRecord parentTransaction;
   private final String branchName;
   private final Artifact associatedArtifact;
   private final String creationComment;
   private final int mergeAddressingQueryId;
   private final long destinationBranchId;
   private Branch newBranch;
   private boolean txCopyBranchType;
   private final long branchUuid;

   public CreateBranchHttpRequestOperation(BranchType branchType, TransactionRecord parentTransaction, String branchName, long branchUuid, Artifact associatedArtifact, String creationComment, int mergeAddressingQueryId, long destinationBranchId) {
      super("Create branch " + branchName, Activator.PLUGIN_ID);
      this.branchType = branchType;
      this.parentTransaction = parentTransaction;
      this.branchName = branchName;
      this.branchUuid = branchUuid;
      this.associatedArtifact = associatedArtifact;
      this.creationComment = creationComment;
      this.mergeAddressingQueryId = mergeAddressingQueryId;
      this.destinationBranchId = destinationBranchId;
      this.txCopyBranchType = false;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.CREATE_BRANCH.name());

      BranchCreationRequest request =
         new BranchCreationRequest(branchType, parentTransaction.getId(), parentTransaction.getBranchId(),
            branchName, branchUuid, getAssociatedArtifactId(associatedArtifact), getAuthorId(), creationComment,
            mergeAddressingQueryId, destinationBranchId);

      request.setTxIsCopied(isTxCopyBranchType());

      BranchCreationResponse response =
         HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.BRANCH_CREATION_REQUEST,
            request, CoreTranslatorId.BRANCH_CREATION_RESPONSE);

      newBranch = BranchManager.getBranch(response.getBranchId());
      OseeEventManager.kickBranchEvent(getClass(), new BranchEvent(BranchEventType.Added, newBranch.getUuid()));
   }

   private static int getAssociatedArtifactId(Artifact associatedArtifact) throws OseeCoreException {
      int associatedArtifactId = -1;
      if (associatedArtifact == null && !DbUtil.isDbInit()) {
         associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
      }
      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }
      return associatedArtifactId;
   }

   private static int getAuthorId() throws OseeCoreException {
      return UserManager.getUser().getArtId();
   }

   public Branch getNewBranch() {
      return newBranch;
   }

   public boolean isTxCopyBranchType() {
      return txCopyBranchType;
   }

   public void setTxCopyBranchType(boolean value) {
      txCopyBranchType = value;
   }
}