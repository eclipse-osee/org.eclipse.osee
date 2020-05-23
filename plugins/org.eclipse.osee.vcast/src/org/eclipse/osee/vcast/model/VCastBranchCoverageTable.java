/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

import java.util.Collection;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastBranchCoverageTable implements VCastTableData<VCastBranchCoverage> {

   @Override
   public String getName() {
      return "branch_coverage";
   }

   @Override
   public String[] getColumns() {
      return new String[] {
         "id",
         "function_id",
         "line",
         "num_conditions",
         "true_count",
         "false_count",
         "max_true_count",
         "max_false_count"};
   }

   @Override
   public Collection<VCastBranchCoverage> getRows(VCastDataStore dataStore) {
      return dataStore.getAllBranchCoverages();
   }

   @Override
   public Object[] toRow(VCastBranchCoverage data) {
      Integer id = data.getId();
      Integer function_id = data.getFunctionId();
      Integer line = data.getLine();
      Integer num_conditions = data.getNumConditions();
      Integer true_count = data.getTrueCount();
      Integer false_count = data.getFalseCount();
      Integer max_true_count = data.getMaxTrueCount();
      Integer max_false_count = data.getMaxFalseCount();
      return new Object[] {
         id,
         function_id,
         line,
         num_conditions,
         true_count,
         false_count,
         max_true_count,
         max_false_count};
   }
}