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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxIdWithOperator extends Criteria {

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
