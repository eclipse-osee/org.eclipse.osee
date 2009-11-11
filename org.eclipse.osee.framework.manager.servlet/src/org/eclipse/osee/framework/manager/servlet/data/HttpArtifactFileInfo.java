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

   public HttpArtifactFileInfo(String guid, String branchName, String branchId) {
      this.guid = guid;
      this.branchName = branchName;
      this.branchId = branchId;
   }

   public HttpArtifactFileInfo(HttpServletRequest request) {
      this(request.getParameter(GUID_KEY), request.getParameter(BRANCH_NAME_KEY), request.getParameter(BRANCH_ID_KEY));
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

   public int getId() throws NumberFormatException {
      int toReturn = INVALID_BRANCH_ID;
      if (branchId != null && branchId.length() > 0) {
         toReturn = Integer.parseInt(branchId);
      }
      return toReturn;
   }
}