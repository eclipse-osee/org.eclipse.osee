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
package org.eclipse.osee.framework.artifact.servlet;

import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Retrieves the default OSEE server page
 * 
 * @author Roberto E. Escobar
 */
public class DefaultOseeArtifact {

   private static String OSEE_DEFAULT_SERVER_PAGE_GUID_KEY = "osee.default.server.page.guid";
   private static String OSEE_DEFAULT_SERVER_PAGE_BRANCH_ID = "osee.default.server.page.branch.id";

   public static Pair<String, String> get() throws Exception {

      final String artifactGuid = OseeInfo.getValue(OSEE_DEFAULT_SERVER_PAGE_GUID_KEY);
      String defaultBranchId = OseeInfo.getValue(OSEE_DEFAULT_SERVER_PAGE_BRANCH_ID);
      int artifactBranchId = -1;
      try {
         artifactBranchId = Integer.parseInt(defaultBranchId);
      } catch (Exception ex) {
         // Do Nothing
      }

      if (!Strings.isValid(artifactGuid) || artifactBranchId < 1) {
         throw new OseeStateException(
               String.format(
                     "OSEE default server page not found. Ensure values exist in OseeInfo table for [%s] and [%s] keys.",
                     OSEE_DEFAULT_SERVER_PAGE_GUID_KEY, OSEE_DEFAULT_SERVER_PAGE_BRANCH_ID));
      }
      return new Pair<String, String>(artifactGuid, String.valueOf(artifactBranchId));
   }
}
