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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.data.HasBranch;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaBranch extends Criteria implements HasBranch, BranchCriteria {

   private final Long branchId;

   public CriteriaBranch(Long branchId) {
      this.branchId = branchId;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      Conditions.checkNotNull(branchId, "branch");
   }

   @Override
   public Long getBranchUuid() {
      return branchId;
   }

   @Override
   public String toString() {
      return "CriteriaBranch [branch=" + branchId + "]";
   }

}