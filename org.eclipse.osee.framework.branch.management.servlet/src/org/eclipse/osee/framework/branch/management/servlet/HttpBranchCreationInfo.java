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
   private String branchShortName;
   private String branchName;
   private String creationComment;
   private int associatedArtifactId;
   private int authorId;
   private String staticBranchName;
   private String[] compressArtTypeIds;
   private String[] preserveArtTypeIds;

   public HttpBranchCreationInfo(HttpServletRequest req) throws Exception {
      isFunctionValid(req.getParameter("function"));

      String parentBranchIdStr = req.getParameter("parentBranchId");
      if (parentBranchIdStr != null) {
         parentBranchId = Integer.parseInt(parentBranchIdStr);
      } else {
         parentBranchId = -1;
      }
      branchName = req.getParameter("branchName");//required
      if (branchName == null || branchName.length() == 0) {
         throw new IllegalArgumentException("A 'branchName' parameter must be specified");
      }
      branchShortName = req.getParameter("branchShortName");
      if (branchShortName == null) {
         branchShortName = branchName;
      }
      branchShortName = StringFormat.truncate(branchShortName, 25);
      creationComment = req.getParameter("creationComment");//required
      if (creationComment == null || creationComment.length() == 0) {
         throw new IllegalArgumentException("A 'creationComment' parameter must be specified");
      }
      String associatedArtifactIdStr = req.getParameter("associatedArtifactId");
      if (associatedArtifactIdStr == null) {
         throw new IllegalArgumentException("A 'associatedArtifactId' parameter must be specified");
      }
      associatedArtifactId = Integer.parseInt(associatedArtifactIdStr);
      String authorIdStr = req.getParameter("authorId");
      if (authorIdStr == null) {
         throw new IllegalArgumentException("A 'authorIdStr' parameter must be specified");
      }
      authorId = Integer.parseInt(authorIdStr);
      staticBranchName = req.getParameter("staticBranchName");
      
      String compressArtTypeIdsString = req.getParameter("compressArtTypes");
	  if (compressArtTypeIdsString != null) {
		  compressArtTypeIds = compressArtTypeIdsString.split(",");
	  }
      String preserveArtTypeIdsString = req.getParameter("preserveArtTypes");
      if(preserveArtTypeIdsString != null){
    	  preserveArtTypeIds = preserveArtTypeIdsString.split(",");
      }
   }

   private void isFunctionValid(String function) throws Exception {
      if (function == null) {
         throw new Exception("A 'function' parameter must be defined.");
      }
      try {
         this.function = BranchCreationFunction.valueOf(function);
      } catch (IllegalArgumentException ex) {
         throw new Exception(String.format("[%s] is not a valid function.", function), ex);
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
	
	public boolean branchWithFiltering(){
		return (getCompressArtTypeIds() != null && getCompressArtTypeIds().length > 0) || (getPreserveArtTypeIds() != null && getPreserveArtTypeIds().length > 0);
	}

}
