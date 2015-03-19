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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchImportOptions;

/**
 * @author Roberto E. Escobar
 */
public class HttpBranchExchange {

   public static void importBranches(String path, boolean cleanAllBeforeImport, boolean allAsRootBranches, long... branchUuids) throws OseeCoreException {
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      BranchEndpoint endpoint = oseeClient.getBranchEndpoint();

      BranchImportOptions options = new BranchImportOptions();
      options.setExchangeFile(path);
      options.setCleanBeforeImport(cleanAllBeforeImport);
      options.setAllAsRootBranches(allAsRootBranches);

      if (branchUuids != null && branchUuids.length > 0) {
         List<Long> ids = new ArrayList<Long>();
         for (Long entry : branchUuids) {
            ids.add(entry);
         }
         options.setBranchUuids(ids);
      }
      try {
         endpoint.importBranches(options);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

}
