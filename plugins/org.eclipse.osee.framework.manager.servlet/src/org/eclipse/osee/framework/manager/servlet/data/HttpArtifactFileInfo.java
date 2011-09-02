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
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class HttpArtifactFileInfo {
   public static final int INVALID_BRANCH_ID = -1;
   private static final String GUID_KEY = "guid";
   private static final String BRANCH_NAME_KEY = "branch";
   private static final String BRANCH_ID_KEY = "branchId";
   private static final String BRANCH_GUID_KEY = "branchGuid";

   private final String guid;
   private final String branchName;
   private final String branchId;
   private final String branchGuid;

   public HttpArtifactFileInfo(String guid, String branchName, String branchId, String branchGuid) {
      this.guid = guid;
      this.branchName = branchName;
      this.branchId = branchId;
      this.branchGuid = branchGuid;
   }

   public HttpArtifactFileInfo(HttpServletRequest request) {
      this(request.getParameter(GUID_KEY), request.getParameter(BRANCH_NAME_KEY), request.getParameter(BRANCH_ID_KEY),
         request.getParameter(BRANCH_GUID_KEY));
   }

   public String getGuid() {
      return guid;
   }

   public boolean isBranchNameValid() {
      return Strings.isValid(branchName);
   }

   public String getBranchName() {
      return branchName;
   }

   public String getBranchGuid() {
      return branchGuid;
   }

   public boolean isBranchGuidValid() {
      return Strings.isValid(branchGuid);
   }

   public boolean isBranchIdValid() {
      return Strings.isValid(branchId);
   }

   public int getId() throws NumberFormatException {
      int toReturn = INVALID_BRANCH_ID;
      if (isBranchIdValid()) {
         toReturn = Integer.parseInt(branchId);
      }
      return toReturn;
   }
}