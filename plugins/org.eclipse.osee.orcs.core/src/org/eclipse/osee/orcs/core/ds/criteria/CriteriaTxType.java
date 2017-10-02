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

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxType extends Criteria implements TxCriteria {

   private final Collection<TransactionDetailsType> types;

   public CriteriaTxType(Collection<TransactionDetailsType> types) {
      super();
      this.types = types;
   }

   public Collection<TransactionDetailsType> getTypes() {
      return types;
   }

   @Override
   public void checkValid(Options options)  {
      Conditions.checkExpressionFailOnTrue(getTypes().isEmpty(), "Transaction Types cannot be empty");
   }

   @Override
   public String toString() {
      return "CriteriaTxType [types=" + types + "]";
   }

}
