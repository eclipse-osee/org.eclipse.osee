/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.exportImport;

import java.util.Arrays;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchImportOptions;

/**
 * @author Roberto E. Escobar
 */
public class HttpBranchExchange {

   public static void importBranches(String path, boolean cleanAllBeforeImport, boolean allAsRootBranches, BranchId... branchIds) {
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      BranchEndpoint endpoint = oseeClient.getBranchEndpoint();

      BranchImportOptions options = new BranchImportOptions();
      options.setExchangeFile(path);
      options.setCleanBeforeImport(cleanAllBeforeImport);
      options.setAllAsRootBranches(allAsRootBranches);

      options.setBranchUuids(Arrays.asList(branchIds));
      endpoint.importBranches(options);
   }
}