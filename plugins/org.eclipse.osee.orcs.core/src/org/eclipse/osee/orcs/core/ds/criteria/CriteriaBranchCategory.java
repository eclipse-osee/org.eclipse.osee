/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Audrey Denk
 */
public class CriteriaBranchCategory extends Criteria implements BranchCriteria {

   private final BranchCategoryToken category;

   public CriteriaBranchCategory(BranchCategoryToken category) {
      this.category = category;
   }

   public BranchCategoryToken getBranchCategory() {
      return category;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(category == null, "Branch Category cannot be null");
   }

   @Override
   public String toString() {
      return "CriteriaBranchCategory [branchCategory=" + category.getId() + "]";
   }
}
