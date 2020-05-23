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
import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxGetHead extends Criteria implements TxCriteria {

   private final BranchId branch;

   public CriteriaTxGetHead(BranchId branch) {
      super();
      this.branch = branch;
   }

   public BranchId getBranch() {
      return branch;
   }

   @Override
   public String toString() {
      return "CriteriaTxGetHead [branchUuid=" + branch + "]";
   }

}
