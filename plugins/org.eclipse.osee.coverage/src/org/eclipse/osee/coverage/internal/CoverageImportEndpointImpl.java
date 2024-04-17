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
import org.eclipse.osee.coverage.CoverageImportEndpoint;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Stephen J. Molaro
 */
public class CoverageImportEndpointImpl implements CoverageImportEndpoint {

   private final BranchId branch;
   private final CoverageImportApi coverageImportApi;

   public CoverageImportEndpointImpl(BranchId branch, CoverageImportApi coverageImportApi) {
      this.branch = branch;
      this.coverageImportApi = coverageImportApi;
   }

   @Override
   public void importCoverage() {
      this.coverageImportApi.importCoverage(branch);
   }

}
