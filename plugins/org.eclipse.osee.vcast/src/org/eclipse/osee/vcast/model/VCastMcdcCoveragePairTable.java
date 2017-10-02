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
public class VCastMcdcCoveragePairTable implements VCastTableData<VCastMcdcCoveragePair> {

   @Override
   public String getName() {
      return "mcdc_coverage_pairs";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "mcdc_cond_id", "pair_row1", "pair_row2"};
   }

   @Override
   public Collection<VCastMcdcCoveragePair> getRows(VCastDataStore dataStore) {
      return dataStore.getAllMcdcCoveragePairs();
   }

   @Override
   public Object[] toRow(VCastMcdcCoveragePair data) {
      Integer id = data.getId();
      Integer mcdcCondId = data.getMcdcCondId();
      Integer pairRow1 = data.getPairRow1();
      Integer pairRow2 = data.getPairRow2();
      return new Object[] {id, mcdcCondId, pairRow1, pairRow2};
   }
}
