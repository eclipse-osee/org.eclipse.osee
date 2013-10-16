/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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

public class VCastBranchDataTable implements VCastTableData<VCastBranchData> {

   @Override
   public String getName() {
      return "branch_data";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "branch_id", "result_id", "result_line", "taken"};
   }

   @Override
   public Collection<VCastBranchData> getRows(VCastDataStore dataStore) throws OseeCoreException {
      return dataStore.getAllBranchData();
   }

   @Override
   public Object[] toRow(VCastBranchData data) {
      int id = data.getId();
      Integer branchId = data.getBranchId();
      Integer resultId = data.getResultId();
      Integer resultLine = data.getResultLine();
      Boolean taken = data.getTaken();
      return new Object[] {id, branchId, resultId, resultLine, taken};
   }
}