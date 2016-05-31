/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface TransactionToken extends TransactionId, HasBranch {
   TransactionToken SENTINEL = valueOf(Id.SENTINEL, BranchId.SENTINEL);

   public static TransactionToken valueOf(TransactionId id, BranchId branch) {
      return valueOf(id.getId(), branch);
   }

   public static TransactionToken valueOf(long id, BranchId branch) {
      final class TransactionTokenImpl extends BaseIdentity<Integer> implements TransactionToken {
         private final BranchId branch;

         public TransactionTokenImpl(Integer txId, BranchId branch) {
            super(txId);
            this.branch = branch;
         }

         @Override
         public BranchId getBranch() {
            return branch;
         }
      }
      return new TransactionTokenImpl((int) id, branch);
   }
}