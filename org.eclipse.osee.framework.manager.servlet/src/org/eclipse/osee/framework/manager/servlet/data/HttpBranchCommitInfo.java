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
package org.eclipse.osee.framework.manager.servlet.data;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Megumi Telles
 */
public class HttpBranchCommitInfo {

   private IOseeUser user;
   private Branch sourceBranch;
   private Branch destinationBranch;
   private boolean archiveSourceBranch;

   public HttpBranchCommitInfo(HttpServletRequest req) throws OseeCoreException {
      user = (IOseeUser) UserManager.getUserByName(req.getParameter("User"));
      archiveSourceBranch = Boolean.parseBoolean(req.getParameter("Archive Source Branch"));
      sourceBranch = getBranch(req.getParameter("Source Branch"));
      destinationBranch = getBranch(req.getParameter("Destination Branch"));

   }

   private Branch getBranch(String branchName) throws OseeCoreException {
      Branch branch = null;
      if (!branchName.equals("")) {
         branch = BranchManager.getBranch(branchName);
      }
      return branch;
   }

   public IOseeUser getUser() {
      return user;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }

   public Branch getDestinationBranch() {
      return destinationBranch;
   }

   public boolean isArchiveSourceBranch() {
      return archiveSourceBranch;
   }

}