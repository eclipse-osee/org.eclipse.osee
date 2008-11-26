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
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;

/**
 * @author Andrew M Finkbeiner
 */
class HttpBranchCreationInfo {

   enum BranchCreationFunction {
      createRootBranch, createChildBranch
   };

   private BranchCreationFunction function;
   private int parentBranchId;
   private int parentTransactionId;
   private String branchShortName;
   private String branchName;
   private String creationComment;
   private int associatedArtifactId;
   private int authorId;
   private String staticBranchName;
   private String[] compressArtTypeIds;
   private String[] preserveArtTypeIds;
   private boolean systemRootBranch;

   public HttpBranchCreationInfo(HttpServletRequest req) throws OseeArgumentException {
      ensureFunctionValid(req.getParameter("function"));

      String parentBranchIdStr = req.getParameter("parentBranchId");
      if (parentBranchIdStr == null) {
         throw new OseeArgumentException("A 'parentBranchId' parameter must be specified");
      } else {
         parentBranchId = Integer.parseInt(parentBranchIdStr);
      }

      if (function == BranchCreationFunction.createChildBranch) {
         String parentTransactionIdStr = req.getParameter("parentTransactionId");
         if (parentTransactionIdStr == null) {
            throw new OseeArgumentException("A 'parentTransactionId' parameter must be specified");
         } else {
            parentTransactionId = Integer.parseInt(parentTransactionIdStr);
         }
      }

      branchName = req.getParameter("branchName");//required
      if (branchName == null || branchName.length() == 0) {
         throw new OseeArgumentException("A 'branchName' parameter must be specified");
      }
      branchShortName = req.getParameter("branchShortName");
      if (branchShortName == null) {
         branchShortName = branchName;
      }
      branchShortName = StringFormat.truncate(branchShortName, 25);
      creationComment = req.getParameter("creationComment");//required
      if (creationComment == null || creationComment.length() == 0) {
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

      String compressArtTypeIdsString = req.getParameter("compressArtTypes");
      if (compressArtTypeIdsString != null) {
         compressArtTypeIds = compressArtTypeIdsString.split(",");
      }
      String preserveArtTypeIdsString = req.getParameter("preserveArtTypes");
      if (preserveArtTypeIdsString != null) {
         preserveArtTypeIds = preserveArtTypeIdsString.split(",");
      }

      systemRootBranch = Boolean.parseBoolean(req.getParameter("systemRootBranch"));
   }

   private void ensureFunctionValid(String function) throws OseeArgumentException {
      if (function == null) {
         throw new OseeArgumentException("A 'function' parameter must be defined.");
      }
      try {
         this.function = BranchCreationFunction.valueOf(function);
      } catch (IllegalArgumentException ex) {
         throw new OseeArgumentException(String.format("[%s] is not a valid function.", function));
      }
   }

   /**
    * @return the parentBranchId
    */
   public int getParentBranchId() {
      return parentBranchId;
   }

   /**
    * @return the branchShortName
    */
   public String getBranchShortName() {
      return branchShortName;
   }

   /**
    * @return the branchName
    */
   public String getBranchName() {
      return branchName;
   }

   /**
    * @return the creationComment
    */
   public String getCreationComment() {
      return creationComment;
   }

   /**
    * @return the associatedArtifactId
    */
   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   /**
    * @return the authorId
    */
   public int getAuthorId() {
      return authorId;
   }

   /**
    * @return the staticBranchName
    */
   public String getStaticBranchName() {
      return staticBranchName;
   }

   /**
    * @return the function
    */
   public BranchCreationFunction getFunction() {
      return function;
   }

   /**
    * @return the preserveArtTypes
    */
   public String[] getPreserveArtTypeIds() {
      return preserveArtTypeIds;
   }

   /**
    * @return the compressArtTypes
    */
   public String[] getCompressArtTypeIds() {
      return compressArtTypeIds;
   }

   public boolean branchWithFiltering() {
      return (getCompressArtTypeIds() != null && getCompressArtTypeIds().length > 0) || (getPreserveArtTypeIds() != null && getPreserveArtTypeIds().length > 0);
   }

   /**
    * @return the systemRootBranch
    */
   public boolean isSystemRootBranch() {
      return systemRootBranch;
   }

   /**
    * @return the parentTransactionId
    */
   public int getParentTransactionId() {
      return parentTransactionId;
   }
}