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
package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.framework.core.enums.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public final class TagQueueJoinQuery extends AbstractJoinQuery {

   private final class GammaEntry implements IJoinRow {
      private final Long gammaId;

      private GammaEntry(Long gammaId) {
         this.gammaId = gammaId;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), gammaId};
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         GammaEntry other = (GammaEntry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (gammaId == null) {
            if (other.gammaId != null) {
               return false;
            }
         } else if (!gammaId.equals(other.gammaId)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (gammaId == null ? 0 : gammaId.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return String.format("gammaId=%s", gammaId);
      }

      private TagQueueJoinQuery getOuterType() {
         return TagQueueJoinQuery.this;
      }
   }

   protected TagQueueJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.TAG_GAMMA_QUEUE, expiresIn, queryId);
   }

   public void add(Long gammaId) {
      entries.add(new GammaEntry(gammaId));
   }
}