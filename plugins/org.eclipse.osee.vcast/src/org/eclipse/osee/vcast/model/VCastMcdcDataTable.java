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
