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
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;

/**
 * @author David W. Miller
 */
public final class TransferTupleTypes {
   private static final TransferOpType[] transferOpTypes = TransferOpType.values();
   private static final TransferDBType[] transferDBTypes = TransferDBType.values();
   // repository, code unit, commitArtId, changeType
   public static final Tuple4Type<BranchId, TransactionId, TransactionId, TransferOpType> TransferFile =
      Tuple4Type.valueOf(TransferFamily, 100L, BranchId::valueOf, TransactionId::valueOf, TransactionId::valueOf,
         ordinal -> transferOpTypes[ordinal.intValue()]);

   // repository, code unit, latest commitArtId, latest baseline commitArtId
   public static final Tuple3Type<TransactionId, BranchId, TransferDBType> ExportedBranch = Tuple3Type.valueOf(
      TransferFamily, 101L, TransactionId::valueOf, BranchId::valueOf, ordinal -> transferDBTypes[ordinal.intValue()]);

   private TransferTupleTypes() {
      // Constants
   }
}