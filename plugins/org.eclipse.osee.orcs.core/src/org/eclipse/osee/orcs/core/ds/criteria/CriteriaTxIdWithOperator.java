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
public class CriteriaTxIdWithOperator extends Criteria implements TxCriteria {

   private final Operator op;
   private final int id;

   public CriteriaTxIdWithOperator(Operator op, int id) {
      super();
      this.id = id;
      this.op = op;
   }

   public Operator getOperator() {
      return op;
   }

   public int getId() {
      return id;
   }

   @Override
   public String toString() {
      return "CriteriaTxIdWithOperator [operator=" + op + "  id=" + id + "]";
   }
}
