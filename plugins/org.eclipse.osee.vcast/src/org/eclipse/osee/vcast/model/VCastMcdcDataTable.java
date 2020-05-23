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
public class VCastMcdcDataTable implements VCastTableData<VCastMcdcData> {

   @Override
   public String getName() {
      return "mcdc_data";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "mcdc_id", "result_id", "result_line", "pair_value", "used_value"};
   }

   @Override
   public Collection<VCastMcdcData> getRows(VCastDataStore dataStore) {
      return dataStore.getAllMcdcData();
   }

   @Override
   public Object[] toRow(VCastMcdcData data) {
      Integer id = data.getId();
      Integer mcdcId = data.getMcdcId();
      Integer resultId = data.getResultId();
      Integer resultLine = data.getResultLine();
      Integer pairValue = data.getPairValue();
      Integer usedValue = data.getUsedValue();
      return new Object[] {id, mcdcId, resultId, resultLine, pairValue, usedValue};
   }
}
