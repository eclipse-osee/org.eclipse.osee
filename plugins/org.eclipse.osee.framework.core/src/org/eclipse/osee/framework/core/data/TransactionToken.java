/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface TransactionToken extends TransactionId, HasBranchId {
   TransactionToken SENTINEL = valueOf(Id.SENTINEL, BranchToken.SENTINEL);

   public static TransactionToken valueOf(TransactionId id, BranchId branch) {
      return valueOf(id.getId(), branch);
   }

   public static TransactionToken valueOf(long id, BranchId branch) {
      final class TransactionTokenImpl extends BaseId implements TransactionToken {
         private final BranchId branch;

         public TransactionTokenImpl(Long txId, BranchId branch) {
            super(txId);
            this.branch = branch;
         }

         @Override
         public BranchId getBranch() {
            return branch;
         }
      }
      return new TransactionTokenImpl(id, branch);
   }
}