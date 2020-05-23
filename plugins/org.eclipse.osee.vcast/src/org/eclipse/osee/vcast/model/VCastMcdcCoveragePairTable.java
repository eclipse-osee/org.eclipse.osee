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
