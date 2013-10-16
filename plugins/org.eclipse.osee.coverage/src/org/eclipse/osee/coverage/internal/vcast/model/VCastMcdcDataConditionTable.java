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
package org.eclipse.osee.coverage.internal.vcast.model;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.vcast.VCastDataStore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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
   public Collection<VCastMcdcDataCondition> getRows(VCastDataStore dataStore) throws OseeCoreException {
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
