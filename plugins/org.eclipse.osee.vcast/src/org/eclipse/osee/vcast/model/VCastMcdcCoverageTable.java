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
public class VCastMcdcCoverageTable implements VCastTableData<VCastMcdcCoverage> {

   @Override
   public String getName() {
      return "mcdc_coverage";
   }

   @Override
   public String[] getColumns() {
      return new String[] {
         "id",
         "function_id",
         "line",
         "source_line",
         "num_conditions",
         "actual_expr",
         "simplified_expr"};
   }

   @Override
   public Collection<VCastMcdcCoverage> getRows(VCastDataStore dataStore) {
      return dataStore.getAllMcdcCoverages();
   }

   @Override
   public Object[] toRow(VCastMcdcCoverage data) {
      Integer id = data.getId();
      Integer function_id = data.getFunctionId();
      Integer line = data.getLine();
      Integer source_line = data.getSourceLine();
      Integer num_conditions = data.getNumConditions();
      String actual_expr = data.getActualExpr();
      String simplified_expr = data.getSimplifiedExpr();
      return new Object[] {id, function_id, line, source_line, num_conditions, actual_expr, simplified_expr};
   }
}
