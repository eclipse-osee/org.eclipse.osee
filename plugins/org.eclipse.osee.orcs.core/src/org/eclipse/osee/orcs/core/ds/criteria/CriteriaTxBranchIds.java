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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxBranchIds extends Criteria implements TxCriteria {

   private final Collection<Long> ids;

   public CriteriaTxBranchIds(Collection<Long> ids) {
      super();
      this.ids = ids;
   }

   public Collection<Long> getIds() {
      return ids;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(getIds().isEmpty(), "Branch Uuids cannot be empty");
   }

   @Override
   public String toString() {
      return "CriteriaTxBranchIds [ids=" + ids + "]";
   }

}
