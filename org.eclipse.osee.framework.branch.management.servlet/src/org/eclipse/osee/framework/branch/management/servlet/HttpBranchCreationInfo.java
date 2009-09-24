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
package org.eclipse.osee.framework.branch.management.servlet;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.branch.management.Branch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Andrew M Finkbeiner
 */
class HttpBranchCreationInfo {
   private int parentBranchId;
   private int parentTransactionId;
   private final String branchName;
   private final String creationComment;
   private final int associatedArtifactId;
   private final int authorId;
   private final String staticBranchName;
   private final BranchType branchType;
   private final String branchGuid;
   private final int populateBaseTxFromAddressingQueryId;
   private final int destinationBranchId;

   public HttpBranchCreationInfo(HttpServletRequest req) throws OseeArgumentException {
      branchGuid = req.getParameter("branchGuid");
      String parentBranchIdStr = req.getParameter("parentBranchId");
      if (parentBranchIdStr == null) {
         throw new OseeArgumentException("A 'parentBranchId' parameter must be specified");
      } else {
         parentBranchId = Integer.parseInt(parentBranchIdStr);
      }

      String branchTypeStr = req.getParameter("branchType");
      if (branchTypeStr == null) {
         throw new OseeArgumentException("A 'branchTypeStr' parameter must be specified");
      }
      branchType = BranchType.valueOf(branchTypeStr);

      String parentTransactionIdStr = req.getParameter("parentTransactionId");
      if (parentTransactionIdStr == null) {
         throw new OseeArgumentException("A 'parentTransactionId' parameter must be specified");
      } else {
         parentTransactionId = Integer.parseInt(parentTransactionIdStr);
      }

      branchName = req.getParameter("branchName");//required
      if (!Strings.isValid(branchName)) {
         throw new OseeArgumentException("A 'branchName' parameter must be specified");
      }
      creationComment = req.getParameter("creationComment");//required
      if (!Strings.isValid(creationComment)) {
         throw new OseeArgumentException("A 'creationComment' parameter must be specified");
      }
      String associatedArtifactIdStr = req.getParameter("associatedArtifactId");
      if (associatedArtifactIdStr == null) {
         throw new OseeArgumentException("A 'associatedArtifactId' parameter must be specified");
      }
      associatedArtifactId = Integer.parseInt(associatedArtifactIdStr);
      String authorIdStr = req.getParameter("authorId");
      if (authorIdStr == null) {
         throw new OseeArgumentException("A 'authorIdStr' parameter must be specified");
      }
      authorId = Integer.parseInt(authorIdStr);
      staticBranchName = req.getParameter("staticBranchName");

      String populateBaseTxFromAddressingQueryIdStr = req.getParameter("populateBaseTxFromAddressingQueryId");
      if (!Strings.isValid(populateBaseTxFromAddressingQueryIdStr)) {
         populateBaseTxFromAddressingQueryId = -1;
      } else {
         populateBaseTxFromAddressingQueryId = Integer.parseInt(populateBaseTxFromAddressingQueryIdStr);
      }

      String destinationBranchIdStr = req.getParameter("destinationBranchId");
      if (!Strings.isValid(destinationBranchIdStr)) {
         destinationBranchId = -1;
      } else {
         destinationBranchId = Integer.parseInt(destinationBranchIdStr);
      }
   }

   public Branch getBranch() {
      return new Branch(branchType, parentTransactionId, parentBranchId, branchGuid, branchName, associatedArtifactId,
            staticBranchName, -1);
   }

   public int getDestinationBranchId() {
      return destinationBranchId;
   }

   public int getPopulateBaseTxFromAddressingQueryId() {
      return populateBaseTxFromAddressingQueryId;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public int getAuthorId() {
      return authorId;
   }
}