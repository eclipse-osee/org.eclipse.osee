/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.coverage.internal;

import org.eclipse.osee.coverage.CoverageImportApi;
import org.eclipse.osee.coverage.PartitionDefApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public class CoverageImportApiImpl implements CoverageImportApi {

   private final OrcsApi orcsApi;
   private final PartitionDefApi partitionDefApi;

   public CoverageImportApiImpl(OrcsApi orcsApi, PartitionDefApi partitionDefApi) {
      this.orcsApi = orcsApi;
      this.partitionDefApi = partitionDefApi;
   }

   @Override
   public void importCoverage(BranchId branch) {
      //TODO: Bring over implementation from DispoApiImpl function runOperation()
   }

}