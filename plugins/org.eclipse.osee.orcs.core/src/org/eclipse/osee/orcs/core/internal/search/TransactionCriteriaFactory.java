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
package org.eclipse.osee.orcs.core.internal.search;

import java.sql.Timestamp;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllTxs;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateRange;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxComment;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetHead;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithTwoOperators;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxType;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCriteriaFactory {

   public Criteria createAllTransactionsCriteria() {
      return new CriteriaAllTxs();
   }

   public Criteria newCommentCriteria(String value, boolean isPattern) {
      return new CriteriaTxComment(value, isPattern);
   }

   public Criteria newTxTypeCriteria(Collection<TransactionDetailsType> types) {
      return new CriteriaTxType(types);
   }

   public Criteria newTxBranchIdCriteria(Collection<? extends BranchId> ids) {
      return new CriteriaTxBranchIds(ids);
   }

   public Criteria newByIdWithOperator(Operator op, int id) {
      return new CriteriaTxIdWithOperator(op, id);
   }

   public Criteria newByIdWithTwoOperators(Operator op1, int id1, Operator op2, int id2) {
      return new CriteriaTxIdWithTwoOperators(op1, id1, op2, id2);
   }

   public Criteria newByDateWithOperator(Operator op, Timestamp date) {
      return new CriteriaDateWithOperator(op, date);
   }

   public Criteria newByDateRange(Timestamp from, Timestamp to) {
      return new CriteriaDateRange(from, to);
   }

   public Criteria newGetHead(BranchId branch) {
      return new CriteriaTxGetHead(branch);
   }
}