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
import org.eclipse.osee.framework.core.access.AccessDetail;
import org.eclipse.osee.framework.core.access.Scope;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {
   private MockDataFactory() {
      // Utility Class
   }

   public static <T> AccessDetail<T> createAccessDetails(T expAccessObject, PermissionEnum expPermission, String expReason, Scope scope) {
      AccessDetail<T> target;
      if (expReason != null) {
         target = new AccessDetail<>(expAccessObject, expPermission, scope, expReason);
      } else {
         target = new AccessDetail<>(expAccessObject, expPermission, scope);
      }
      return target;
   }

   public static TransactionRecord createTransaction(int index, long branchUuid) {
      TransactionDetailsType type =
         TransactionDetailsType.values[Math.abs(index % TransactionDetailsType.values.length)];
      int value = index;
      if (value == 0) {
         value++;
      }
      IOseeBranch branch = IOseeBranch.create(branchUuid, "fake test branch");
      return new TransactionRecord(value * 47L, branch, "comment_" + value, new Date(), DemoUsers.Joe_Smith,
         ArtifactId.valueOf(value * 42), type, 0L);
   }
}