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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.message.BranchCreationResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class HttpBranchCreation {

   /**
    * Creates a new root branch. Should NOT be used outside BranchManager.
    * 
    * @return the newly created branch
    */
   public static Branch createBranch(BranchType branchType, int sourceTransactionId, int parentBranchId, String branchName, String branchGuid, Artifact associatedArtifact, String creationComment, int populateBaseTxFromAddressingQueryId, int destinationBranchId) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.CREATE_BRANCH.name());

      BranchCreationRequest request =
         new BranchCreationRequest(branchType, sourceTransactionId, parentBranchId, branchGuid, branchName,
            getAssociatedArtifactId(associatedArtifact), getAuthorId(), creationComment,
            populateBaseTxFromAddressingQueryId, destinationBranchId);

      BranchCreationResponse response =
         HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, CoreTranslatorId.BRANCH_CREATION_REQUEST,
            request, CoreTranslatorId.BRANCH_CREATION_RESPONSE);

      Branch branch = BranchManager.getBranch(response.getBranchId());
      try {
         OseeEventManager.kickBranchEvent(HttpBranchCreation.class,
            new BranchEvent(BranchEventType.Added, branch.getGuid()), branch.getId());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return branch;
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
}
