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
   public Collection<VCastStatementData> getRows(VCastDataStore dataStore)  {
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
