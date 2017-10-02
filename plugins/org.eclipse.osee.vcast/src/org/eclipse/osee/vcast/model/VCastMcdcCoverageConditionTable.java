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
public class VCastMcdcCoverageConditionTable implements VCastTableData<VCastMcdcCoverageCondition> {

   @Override
   public String getName() {
      return "mcdc_coverage_conditions";
   }

   @Override
   public String[] getColumns() {
      return new String[] {
         "id",
         "mcdc_id",
         "cond_index",
         "true_count",
         "false_count",
         "max_true_count",
         "max_false_count",
         "cond_variable",
         "cond_expr"};
   }

   @Override
   public Collection<VCastMcdcCoverageCondition> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllMcdcCoverageConditions();
   }

   @Override
   public Object[] toRow(VCastMcdcCoverageCondition data) {
      Integer id = data.getId();
      Integer mcdcIid = data.getMcdcId();
      Integer condIindex = data.getCondIndex();
      Integer trueCount = data.getTrueCount();
      Integer falseCount = data.getFalseCount();
      Integer maxTrueCount = data.getMaxTrueCount();
      Integer maxFalseCount = data.getMaxFalseCount();
      String condVariable = data.getCondVariable();
      String condExpr = data.getCondExpr();
      return new Object[] {
         id,
         mcdcIid,
         condIindex,
         trueCount,
         falseCount,
         maxTrueCount,
         maxFalseCount,
         condVariable,
         condExpr};
   }
}
