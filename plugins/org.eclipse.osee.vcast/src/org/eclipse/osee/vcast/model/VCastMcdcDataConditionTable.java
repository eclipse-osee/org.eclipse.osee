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
public class VCastMcdcDataConditionTable implements VCastTableData<VCastMcdcDataCondition> {

   @Override
   public String getName() {
      return "mcdc_data_conditions";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "mcdc_data_id", "cond_index", "cond_value"};
   }

   @Override
   public Collection<VCastMcdcDataCondition> getRows(VCastDataStore dataStore) {
      return dataStore.getAllMcdcDataConditions();
   }

   @Override
   public Object[] toRow(VCastMcdcDataCondition data) {
      Integer id = data.getId();
      Integer mcdcDataId = data.getMcdcDataId();
      Integer condIndex = data.getCondIndex();
      Boolean condValue = data.getCondValue();
      return new Object[] {id, mcdcDataId, condIndex, condValue};
   }
}
