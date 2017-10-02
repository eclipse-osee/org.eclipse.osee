/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

import java.util.Collection;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastStatementCoverageTable implements VCastTableData<VCastStatementCoverage> {

   @Override
   public String getName() {
      return "statement_coverage";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "function_id", "line", "hit_count", "max_hit_count"};
   }

   @Override
   public Collection<VCastStatementCoverage> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllStatementCoverages();
   }

   @Override
   public Object[] toRow(VCastStatementCoverage data) {
      Integer id = data.getId();
      Integer functionId = data.getFunctionId();
      Integer line = data.getLine();
      Integer hitCount = data.getHitCount();
      Integer maxHitCount = data.getMaxHitCount();
      return new Object[] {id, functionId, line, hitCount, maxHitCount};
   }
}
