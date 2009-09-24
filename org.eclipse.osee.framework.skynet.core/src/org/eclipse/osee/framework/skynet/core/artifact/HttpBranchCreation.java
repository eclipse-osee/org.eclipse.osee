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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class HttpBranchCreation {

   /**
    * Creates a new root branch. Should NOT be used outside BranchManager. If programmatic access is necessary, setting
    * the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param branchType
    * @param parentTransactionNumber
    * @param parentBranchId
    * @param branchName
    * @param staticBranchName
    * @param associatedArtifact
    * @return the newly created branch
    * @throws OseeCoreException
    * @see BranchManager#createRootBranch(String, String, int)
    * @see BranchManager#getKeyedBranch(String)
    */
   public static Branch createFullBranch(BranchType branchType, int parentTransactionNumber, int parentBranchId, String branchName, String staticBranchName, String branchGuid, Artifact associatedArtifact, String creationComment, int populateBaseTxFromAddressingQueryId, int destinationBranchId) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      if (GUID.isValid(branchGuid)) {
         parameters.put("branchGuid", branchGuid);
      }
      parameters.put("branchName", branchName);
      parameters.put("function", "createFullBranch");
      parameters.put("authorId", getAuthorId());
      parameters.put("parentBranchId", Integer.toString(parentBranchId));
      parameters.put("parentTransactionId", Integer.toString(parentTransactionNumber));
      parameters.put("associatedArtifactId", getAssociatedArtifactId(associatedArtifact));

      if (Strings.isValid(staticBranchName)) {
         parameters.put("staticBranchName", staticBranchName);
      }

      parameters.put("branchType", branchType.name());
      parameters.put("creationComment", creationComment);
      if (populateBaseTxFromAddressingQueryId > 0) {
         parameters.put("populateBaseTxFromAddressingQueryId", Integer.toString(populateBaseTxFromAddressingQueryId));
      }
      if (destinationBranchId > 0) {
         parameters.put("destinationBranchId", Integer.toString(destinationBranchId));
      }
      return commonServletBranchingCode(parameters);
   }

   private static Branch commonServletBranchingCode(Map<String, String> parameters) throws OseeCoreException {
      Branch branch = null;
      String response = "";
      try {
         response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                     OseeServerContext.BRANCH_CREATION_CONTEXT, parameters)));
         int branchId = Integer.parseInt(response);
         branch = BranchManager.getBranch(branchId);
      } catch (NumberFormatException ex) {
         throw new OseeCoreException(response);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

      // Kick events
      OseeEventManager.kickBranchEvent(HttpBranchCreation.class, BranchEventType.Added, branch.getBranchId());

      return branch;
   }

   private static String getAssociatedArtifactId(Artifact associatedArtifact) throws OseeCoreException {
      int associatedArtifactId = -1;
      if (associatedArtifact == null && !DbUtil.isDbInit()) {
         associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
      }
      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }
      return Integer.toString(associatedArtifactId);
   }

   private static String getAuthorId() throws OseeCoreException {
      return Integer.toString(UserManager.getUser().getArtId());
   }
}
