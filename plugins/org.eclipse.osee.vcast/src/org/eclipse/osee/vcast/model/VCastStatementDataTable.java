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
public class VCastStatementDataTable implements VCastTableData<VCastStatementData> {

   @Override
   public String getName() {
      return "statement_data";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "statement_id", "result_id", "result_line", "hit"};
   }

   @Override
   public Collection<VCastStatementData> getRows(VCastDataStore dataStore) {
      return dataStore.getAllStatementData();
   }

   @Override
   public Object[] toRow(VCastStatementData data) {
      Integer id = data.getId();
      Integer statementId = data.getStatementId();
      Integer resultId = data.getResultId();
      Integer resultLine = data.getResultLine();
      Boolean hit = data.getHit();
      return new Object[] {id, statementId, resultId, resultLine, hit};
   }
}
