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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;

/**
 * @author b1528444
 */
public class HttpBranchCreation {

   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   public static Branch createChildBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws SQLException, OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", childBranchName);
      parameters.put("function", "createChildBranch");
      parameters.put("authorId", Integer.toString(getAuthorId()));
      parameters.put("parentBranchId", Integer.toString(parentTransactionId.getBranch().getBranchId()));
      parameters.put("associatedArtifactId", Integer.toString(getAssociatedArtifactId(associatedArtifact)));
      parameters.put(
            "creationComment",
            BranchPersistenceManager.NEW_BRANCH_COMMENT + parentTransactionId.getBranch().getBranchName() + "(" + parentTransactionId.getTransactionNumber() + ")");
      if (childBranchShortName != null && childBranchShortName.length() > 0) {
         parameters.put("shortBranchName", childBranchShortName);
      }
      return commonServletBranchingCode(parameters);
   }

   /**
    * Creates a new root branch. Should NOT be used outside BranchPersistenceManager. If programatic access is
    * necessary, setting the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName
    * @param branchName
    * @param staticBranchName null if no static key is desired
    * @return branch object
    * @throws SQLException
    * @throws OseeCoreException
    * @see BranchPersistenceManager#createRootBranch(String, String, int)
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public static Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName) throws SQLException, OseeCoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", branchName);
      parameters.put("function", "createRootBranch");
      parameters.put("authorId", "-1");
      parameters.put("associatedArtifactId", Integer.toString(getAssociatedArtifactId(null)));
      parameters.put("creationComment", String.format("Root Branch [%s] Creation", branchName));
      if (shortBranchName != null && shortBranchName.length() > 0) {
         parameters.put("shortBranchName", shortBranchName);
      }
      if (staticBranchName != null && staticBranchName.length() > 0) {
         parameters.put("staticBranchName", staticBranchName);
      }
      return commonServletBranchingCode(parameters);
   }

   private static Branch commonServletBranchingCode(Map<String, String> parameters) throws OseeCoreException {
      Branch branch = null;
      String response = "";
      try {
         response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("branch", parameters)));
         int branchId = Integer.parseInt(response);
         branch = BranchPersistenceManager.getInstance().getBranch(branchId);
      } catch (NumberFormatException ex) {
         throw new OseeCoreException(response);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
      eventManager.kick(new LocalNewBranchEvent(new Object(), branch.getBranchId()));
      RemoteEventManager.kick(new NetworkNewBranchEvent(branch.getBranchId(), SkynetAuthentication.getUser().getArtId()));
      return branch;
   }

   private static int getAssociatedArtifactId(Artifact associatedArtifact) throws OseeCoreException, SQLException {
      int associatedArtifactId = -1;
      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = SkynetAuthentication.getUser(UserEnum.NoOne);
      }
      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }
      return associatedArtifactId;
   }

   private static int getAuthorId() throws OseeCoreException, SQLException {
      if (SkynetDbInit.isDbInit()) {
         return -1;
      }
      User userToBlame = SkynetAuthentication.getUser();
      return (userToBlame == null) ? SkynetAuthentication.getUser(UserEnum.NoOne).getArtId() : userToBlame.getArtId();
   }
}
