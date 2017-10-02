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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastMcdcCoveragePairRowTable implements VCastTableData<VCastMcdcCoveragePairRow> {

   @Override
   public String getName() {
      return "mcdc_coverage_pair_rows";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "mcdc_id", "row_value", "row_result", "hit_count", "max_hit_count"};
   }

   @Override
   public Collection<VCastMcdcCoveragePairRow> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllMcdcCoveragePairRows();
   }

   @Override
   public Object[] toRow(VCastMcdcCoveragePairRow data) {
      Integer id = data.getId();
      Integer mcdcId = data.getMcdcId();
      Integer rowValue = data.getRowValue();
      Integer rowResult = data.getRowResult();
      Integer hitCount = data.getHitCount();
      Integer maxHitCount = data.getMaxHitCount();
      return new Object[] {id, mcdcId, rowValue, rowResult, hitCount, maxHitCount};
   }
}
