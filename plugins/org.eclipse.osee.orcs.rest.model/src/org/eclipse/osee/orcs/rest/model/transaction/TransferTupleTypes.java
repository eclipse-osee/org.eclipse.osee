/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.rest.model.transaction;

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.TransferFamily;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;

/**
 * @author David W. Miller
 */
public final class TransferTupleTypes {
   private static final TransferOpType[] transferOpTypes = TransferOpType.values();
   private static final TransferDBType[] transferDBTypes = TransferDBType.values();
   // Exported Branch, Transfer Type, Exported Transaction, Unique Common Transaction
   public static final Tuple4Type<BranchId, TransferOpType, TransactionId, TransactionId> TransferFile =
      Tuple4Type.valueOf(TransferFamily, 100L, BranchId::valueOf, ordinal -> transferOpTypes[ordinal.intValue()],
         TransactionId::valueOf, TransactionId::valueOf);

   // Exported Transaction ID?, Branch ID, Branch Base Transaction Id, Transfer Type
   public static final Tuple4Type<TransactionId, BranchId, TransactionId, TransferDBType> ExportedBranch =
      Tuple4Type.valueOf(TransferFamily, 101L, TransactionId::valueOf, BranchId::valueOf, TransactionId::valueOf,
         ordinal -> transferDBTypes[ordinal.intValue()]);

   // Lock Check TransactionId (BaseTx), BranchId, TransactionId(ExportId)
   public static final Tuple2Type<BranchId, TransactionId> TransferLocked =
      Tuple2Type.valueOf(TransferFamily, 102L, BranchId::valueOf, TransactionId::valueOf);

   private TransferTupleTypes() {
      /* Constants */
   }
}