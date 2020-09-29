/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.mocks;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {
   private MockDataFactory() {
      // Utility Class
   }

   public static TransactionRecord createTransaction(int index, long branchUuid) {
      TransactionDetailsType type =
         TransactionDetailsType.values[Math.abs(index % TransactionDetailsType.values.length)];
      int value = index;
      if (value == 0) {
         value++;
      }
      BranchToken branch = BranchToken.create(branchUuid, "fake test branch");
      return new TransactionRecord(value * 47L, branch, "comment_" + value, new Date(), DemoUsers.Joe_Smith,
         ArtifactId.valueOf(value * 42), type, 0L);
   }
}