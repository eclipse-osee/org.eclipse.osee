/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxIdWithTwoOperators extends Criteria implements TxCriteria {

   private final Operator op1, op2;
   private final int id1, id2;

   public CriteriaTxIdWithTwoOperators(Operator op1, int id1, Operator op2, int id2) {
      super();
      this.id1 = id1;
      this.op1 = op1;
      this.id2 = id2;
      this.op2 = op2;
   }

   public Operator getOperator1() {
      return op1;
   }

   public int getId1() {
      return id1;
   }

   public Operator getOperator2() {
      return op2;
   }

   public int getId2() {
      return id2;
   }

   @Override
   public String toString() {
      return "CriteriaTxIdWithTwoOperators [operator=" + op1 + "  id=" + id1 + " and operator=" + op1 + "  id=" + id1 + " and]";
   }
}
