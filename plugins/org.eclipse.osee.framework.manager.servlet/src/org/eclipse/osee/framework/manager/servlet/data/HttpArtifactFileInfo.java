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
   private static final String GUID_KEY = "guid";
   private static final String BRANCH_NAME_KEY = "branch";
   private static final String BRANCH_UUID_KEY = "branchUuid";

   private final String guid;
   private final String branchName;
   private final Long branchUuid;

   public HttpArtifactFileInfo(String guid, String branchName, Long branchUuid) {
      this.guid = guid;
      this.branchName = branchName;
      this.branchUuid = branchUuid;
   }

   public HttpArtifactFileInfo(HttpServletRequest request) {
      this(request.getParameter(GUID_KEY), request.getParameter(BRANCH_NAME_KEY),
         Long.valueOf(request.getParameter(BRANCH_UUID_KEY)));
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

   public Long getBranchGuid() {
      return branchUuid;
   }

   public boolean isBranchGuidValid() {
      return branchUuid != null && branchUuid > 0;
   }

}