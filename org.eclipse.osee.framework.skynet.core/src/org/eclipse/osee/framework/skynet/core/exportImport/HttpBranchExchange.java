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
package org.eclipse.osee.framework.skynet.core.exportImport;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class HttpBranchExchange {
   private static final String BRANCH_EXPORT = "exportBranch";
   private static final String BRANCH_IMPORT = "importBranch";

   private HttpBranchExchange() {
   }

   public static void exportBranches(String exportFileName, int... branchIds) throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", BRANCH_EXPORT);
      if (Strings.isValid(exportFileName)) {
         parameters.put("filename", exportFileName);
      }
      addBranchIds(parameters, branchIds);
      execute(parameters);
   }

   public static void importBranches(String path, boolean cleanAllBeforeImport, boolean allAsRootBranches, int... branchIds) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      parameters.put("function", BRANCH_IMPORT);
      if (!path.startsWith("exchange://")) {
         path = "exchange://" + path;
      }
      parameters.put("uri", path);
      if (allAsRootBranches) {
         parameters.put("all_as_root_branches", Boolean.toString(allAsRootBranches));
      }
      if (cleanAllBeforeImport) {
         parameters.put("clean_before_import", Boolean.toString(cleanAllBeforeImport));
      }
      addBranchIds(parameters, branchIds);
      execute(parameters);
   }

   private static void execute(Map<String, String> parameters) throws OseeDataStoreException {
      try {
         String returnVal =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                     OseeServerContext.BRANCH_EXCHANGE_CONTEXT, parameters)));
         OseeLog.log(HttpBranchExchange.class, Level.INFO, returnVal);
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   private static void addBranchIds(Map<String, String> parameters, int... branchIds) {
      if (branchIds != null && branchIds.length > 0) {
         StringBuffer ids = new StringBuffer();
         for (int index = 0; index < branchIds.length; index++) {
            ids.append(branchIds[index]);
            if (index + 1 < branchIds.length) {
               ids.append(",");
            }
         }
         parameters.put("branchIds", ids.toString());
      }
   }
}
