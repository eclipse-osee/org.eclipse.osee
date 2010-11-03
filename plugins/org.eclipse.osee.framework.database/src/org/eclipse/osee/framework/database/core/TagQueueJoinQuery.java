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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public final class TagQueueJoinQuery extends AbstractJoinQuery {

   private final class GammaEntry implements IJoinRow {
      private final long gammaId;

      private GammaEntry(Long gammaId) {
         this.gammaId = gammaId;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), gammaId};
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof GammaEntry)) {
            return false;
         }
         GammaEntry other = (GammaEntry) obj;
         return this.gammaId == other.gammaId;
      }

      @Override
      public int hashCode() {
         return Long.valueOf(37 * gammaId).hashCode();
      }

      @Override
      public String toString() {
         return String.format("gammaId=%s", gammaId);
      }
   }

   protected TagQueueJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.TAG_GAMMA_QUEUE, queryId);
   }

   public void add(Long gammaId) {
      entries.add(new GammaEntry(gammaId));
   }
}