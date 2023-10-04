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

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.KeyValueOps;

/**
 * @author David W. Miller
 */
public final class TransferFileLockUtil {
   private static final String LOCKED = "LOCKED";
   private static final String OPEN = "OPEN";

   public static boolean lock(KeyValueOps ops, Long lockId) {
      // if already locked, return false
      String lock = ops.getByKey(lockId);
      if (Strings.isInvalidOrBlank(lock)) {
         return ops.putWithKeyIfAbsent(lockId, TransferFileLockUtil.LOCKED);
      } else {
         if (lock.equals(TransferFileLockUtil.LOCKED)) {
            return false;
         } else if (lock.equals(TransferFileLockUtil.OPEN)) {
            return ops.updateByKey(lockId, TransferFileLockUtil.LOCKED);
         }
      }
      return false;
   }

   public static boolean unLock(KeyValueOps ops, Long lockId) {
      // if already open, return false
      String lock = ops.getByKey(lockId);
      if (Strings.isInvalidOrBlank(lock)) {
         return ops.putWithKeyIfAbsent(lockId, TransferFileLockUtil.OPEN);
      } else {
         if (lock.equals(TransferFileLockUtil.OPEN)) {
            return false;
         } else if (lock.equals(TransferFileLockUtil.LOCKED)) {
            return ops.updateByKey(lockId, TransferFileLockUtil.OPEN);
         }
      }
      return false;
   }

   public static boolean isLocked(KeyValueOps ops, Long lockId) {
      String lock = ops.getByKey(lockId);
      if (Strings.isInvalidOrBlank(lock)) {
         return false;
      } else {
         if (lock.equals(TransferFileLockUtil.LOCKED)) {
            return true;
         } else if (lock.equals(TransferFileLockUtil.OPEN)) {
            return false;
         }
      }
      return false;
   }
}