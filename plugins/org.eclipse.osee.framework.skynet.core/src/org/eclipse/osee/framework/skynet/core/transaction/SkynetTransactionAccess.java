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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Roberto E. Escobar
 */
public class SkynetTransactionAccess {

   public final IAccessControlService service;
   private final User currentUser;
   private final Branch txBranch;

   public SkynetTransactionAccess(IAccessControlService service, User currentUser, Branch txBranch) {
      super();
      this.service = service;
      this.currentUser = currentUser;
      this.txBranch = txBranch;
   }

   private IAccessControlService getService() {
      return service;
   }

   private User getUserArtifact() {
      return currentUser;
   }

   private Branch getBranch() {
      return txBranch;
   }

   public void checkAccess(Artifact artifact) throws OseeCoreException {
      checkBranch(artifact);
      checkNotHistorical(artifact);
      checkAccessControl(artifact);
   }

   public void checkAccess(RelationLink link) throws OseeCoreException {
      checkBranch(link);
   }

   public void checkAccessControl(Artifact artifact) throws OseeCoreException {
      Collection<?> items = Collections.singleton(artifact);
      try {
         AccessDataQuery accessContext = getService().getAccessData(getUserArtifact(), items);
         if (!accessContext.matchesAll(PermissionEnum.WRITE)) {
            throw new OseeCoreException(
               String.format(
                  "Access Denied - [%s] does not have valid permission to edit this artifact\n itemsToPersist:[%s]\n accessContext:[%s]",
                  getUserArtifact(), items, accessContext));
         }
      } catch (OseeCoreException ex) {
         throw new OseeCoreException("Error during access check", ex);
      }
   }

   private void checkBranch(Artifact artifact) throws OseeCoreException {
      Branch txBranch = getBranch();
      if (!artifact.getBranch().equals(txBranch)) {
         String msg =
            String.format("The artifact [%s] is on branch [%s] but this transaction is for branch [%s]",
               artifact.getGuid(), artifact.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }
      if (!isBranchWritable(txBranch)) {
         throw new OseeStateException("The artifact [%s] is on a non-editable branch [%s] ", artifact, txBranch);
      }
   }

   private void checkBranch(RelationLink link) throws OseeCoreException {
      Branch txBranch = getBranch();
      if (!link.getBranch().equals(txBranch)) {
         String msg =
            String.format("The relation link [%s] is on branch [%s] but this transaction is for branch [%s]",
               link.getId(), link.getBranch(), txBranch);
         throw new OseeStateException(msg);
      }
      if (!isBranchWritable(txBranch)) {
         throw new OseeStateException("The relation link [%s] is on a non-editable branch [%s] ", link, txBranch);
      }
   }

   private void checkNotHistorical(Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) {
         throw new OseeStateException("The artifact [%s] must be at the head of the branch to be edited.",
            artifact.getGuid());
      }
   }

   private boolean isBranchWritable(Branch branch) throws OseeCoreException {
      boolean toReturn = true;
      if (!UserManager.duringMainUserCreation()) {
         toReturn = getService().hasPermission(branch, PermissionEnum.WRITE) && branch.isEditable();
      }
      return toReturn;
   }
}
