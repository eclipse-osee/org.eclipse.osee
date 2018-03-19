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

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface TransactionId extends Id {
   TransactionId SENTINEL = valueOf(Id.SENTINEL);

   default boolean isOlderThan(TransactionId other) {
      return isLessThan(other);
   }

   public static TransactionId valueOf(String id) {
      return Id.valueOf(id, TransactionId::valueOf);
   }

   public static TransactionId valueOf(int id) {
      return valueOf(Long.valueOf(id));
   }

   public static TransactionId valueOf(Long id) {
      final class TransactionToken extends BaseId implements TransactionId {
         public TransactionToken(Long txId) {
            super(txId);
         }
      }
      return new TransactionToken(id);
   }
}