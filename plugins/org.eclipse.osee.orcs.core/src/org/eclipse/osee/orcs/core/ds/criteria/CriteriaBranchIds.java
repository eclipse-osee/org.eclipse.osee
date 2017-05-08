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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaBranchIds extends Criteria implements BranchCriteria {

   private final Collection<? extends BranchId> branchIds;

   public CriteriaBranchIds(Collection<? extends BranchId> branchIds) {
      this.branchIds = branchIds;
   }

   public CriteriaBranchIds(BranchId branchId) {
      List<BranchId> ids = new ArrayList<>(1);
      ids.add(branchId);
      this.branchIds = ids;
   }

   public Collection<? extends BranchId> getIds() {
      return branchIds;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(getIds().isEmpty(), "Branch Uuids cannot be empty");
   }

   @Override
   public String toString() {
      return "CriteriaBranchIds " + branchIds;
   }
}