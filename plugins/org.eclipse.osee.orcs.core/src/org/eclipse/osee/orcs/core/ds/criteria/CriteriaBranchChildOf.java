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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaBranchChildOf extends Criteria implements BranchCriteria {

   private final BranchId parent;

   public CriteriaBranchChildOf(BranchId parent) {
      super();
      this.parent = parent;
   }

   public BranchId getParent() {
      return parent;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(getParent(), "Parent branch");
   }

   @Override
   public String toString() {
      return "CriteriaBranchChildOf [parent=" + parent + "]";
   }

}
