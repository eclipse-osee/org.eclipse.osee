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

import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Retrieves the default OSEE server page
 * 
 * @author Roberto E. Escobar
 */
public class DefaultOseeArtifact {

   private static String OSEE_DEFAULT_SERVER_PAGE_GUID_KEY = "osee.default.server.page.guid";
   private static String OSEE_DEFAULT_SERVER_PAGE_BRANCH_UUID = "osee.default.server.page.branch.uuid";

   public static Pair<String, Long> get() throws Exception {

      final String artifactGuid = OseeInfo.getValue(OSEE_DEFAULT_SERVER_PAGE_GUID_KEY);
      final String defaultBranchUuidId = OseeInfo.getValue(OSEE_DEFAULT_SERVER_PAGE_BRANCH_UUID);

      if (!Strings.isValid(artifactGuid) || !Strings.isValid(defaultBranchUuidId)) {
         throw new OseeStateException(
            "OSEE default server page not found. Ensure values exist in OseeInfo table for [%s] and [%s] keys.",
            OSEE_DEFAULT_SERVER_PAGE_GUID_KEY, OSEE_DEFAULT_SERVER_PAGE_BRANCH_UUID);
      }
      return new Pair<String, Long>(artifactGuid, Long.valueOf(defaultBranchUuidId));
   }
}
