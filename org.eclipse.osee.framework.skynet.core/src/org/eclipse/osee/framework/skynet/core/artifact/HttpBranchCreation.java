/*
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.connection.OseeApplicationServerContext;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserEnum;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author b1528444
 */
public class HttpBranchCreation {

   public static Branch createChildBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<Integer> compressArtTypeIds, Collection<Integer> preserveArtTypeIds) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", childBranchName);
      parameters.put("function", "createChildBranch");
      parameters.put("authorId", getAuthorId());
      parameters.put("parentBranchId", Integer.toString(parentTransactionId.getBranchId()));
      parameters.put("associatedArtifactId", getAssociatedArtifactId(associatedArtifact));

      if (compressArtTypeIds != null && !compressArtTypeIds.isEmpty()) {
         parameters.put("compressArtTypes", Collections.toString(",", compressArtTypeIds));
      }

      if (preserveArtTypeIds != null && !preserveArtTypeIds.isEmpty()) {
         parameters.put("preserveArtTypes", Collections.toString(",", preserveArtTypeIds));
      }

      parameters.put(
            "creationComment",
            BranchManager.NEW_BRANCH_COMMENT + parentTransactionId.getBranch().getBranchName() + "(" + parentTransactionId.getTransactionNumber() + ")");
      if (childBranchShortName != null && childBranchShortName.length() > 0) {
         parameters.put("shortBranchName", childBranchShortName);
      }
      return commonServletBranchingCode(parameters);
   }

   /**
    * Creates a new root branch. Should NOT be used outside BranchManager. If programatic access is necessary, setting
    * the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName
    * @param branchName
    * @param staticBranchName null if no static key is desired
    * @return branch object
    * @throws OseeCoreException
    * @see BranchManager#createRootBranch(String, String, int)
    * @see BranchManager#getKeyedBranch(String)
    */
   public static Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName, int parentBranchId, boolean systemRootBranch) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", branchName);
      parameters.put("function", "createRootBranch");
      parameters.put("authorId", getAuthorId());
      parameters.put("parentBranchId", Integer.toString(parentBranchId));
      parameters.put("associatedArtifactId", getAssociatedArtifactId(null));
      parameters.put("creationComment", String.format("Root Branch [%s] Creation", branchName));
      if (shortBranchName != null && shortBranchName.length() > 0) {
         parameters.put("shortBranchName", shortBranchName);
      }
      if (staticBranchName != null && staticBranchName.length() > 0) {
         parameters.put("staticBranchName", staticBranchName);
      }

      if (systemRootBranch) {
         parameters.put("systemRootBranch", "true");
      }
      return commonServletBranchingCode(parameters);
   }

   private static Branch commonServletBranchingCode(Map<String, String> parameters) throws OseeCoreException {
      Branch branch = null;
      String response = "";
      try {
         response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                     OseeApplicationServerContext.BRANCH_CREATION_CONTEXT, parameters)));
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
      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = SkynetAuthentication.getUser(UserEnum.NoOne);
      }
      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }
      return Integer.toString(associatedArtifactId);
   }

   private static String getAuthorId() throws OseeCoreException {
      if (SkynetDbInit.isDbInit()) {
         return "-1";
      }
      User userToBlame = SkynetAuthentication.getUser();
      return Integer.toString((userToBlame == null) ? SkynetAuthentication.getUser(UserEnum.NoOne).getArtId() : userToBlame.getArtId());
   }
}
