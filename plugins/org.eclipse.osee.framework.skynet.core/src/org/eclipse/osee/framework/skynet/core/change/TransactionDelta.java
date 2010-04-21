/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionDelta {
   private final TransactionRecord startTx;
   private final TransactionRecord endTx;

   public TransactionDelta(TransactionRecord startTx, TransactionRecord endTx) {
      super();
      this.startTx = startTx;
      this.endTx = endTx;
   }

   public TransactionRecord getStartTx() {
      return startTx;
   }

   public TransactionRecord getEndTx() {
      return endTx;
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof TransactionDelta) {
         TransactionDelta other = (TransactionDelta) obj;
         boolean left = startTx == null ? other.startTx == null : startTx.equals(other.startTx);
         boolean right = endTx == null ? other.endTx == null : endTx.equals(other.endTx);
         result = left && right;
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (startTx != null) {
         result = prime * result + startTx.hashCode();
      } else {
         result = prime * result;
      }
      if (endTx != null) {
         result = prime * result + endTx.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   @Override
   public String toString() {
      String firstString = String.valueOf(getStartTx());
      String secondString = String.valueOf(getEndTx());
      return String.format("[start:%s, end:%s]", firstString, secondString);
   }
}