/*
 * Created on Apr 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.artifact.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Roberto E. Escobar
 */
public class HttpArtifactFileInfo {
   private static final int INVALID_BRANCH_ID = -1;
   private static final String GUID_KEY = "guid";
   private static final String BRANCH_NAME_KEY = "branch";
   private static final String BRANCH_ID_KEY = "branchId";

   private String guid;
   private String branchName;
   private String branchId;

   public HttpArtifactFileInfo(HttpServletRequest request) {
      this.guid = request.getParameter(GUID_KEY);
      this.branchName = request.getParameter(BRANCH_NAME_KEY);
      this.branchId = request.getParameter(BRANCH_ID_KEY);
   }

   public String getGuid() {
      return guid;
   }

   public boolean isBranchNameValid() {
      return branchName != null && branchName.length() > 0;
   }

   public String getBranchName() {
      return branchName;
   }

   public int getBranchId() throws NumberFormatException {
      int toReturn = INVALID_BRANCH_ID;
      if (branchId != null && branchId.length() > 0) {
         toReturn = Integer.parseInt(branchId);
      }
      return toReturn;
   }
}