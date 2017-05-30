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
public final class CharJoinQuery extends AbstractJoinQuery {

   private final class CharJoinEntry implements IJoinRow {
      private final String value;

      private CharJoinEntry(String value) {
         this.value = value;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), value};
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
         CharJoinEntry other = (CharJoinEntry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (value == null) {
            if (other.value != null) {
               return false;
            }
         } else if (!value.equals(other.value)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (value == null ? 0 : value.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return value;
      }

      private CharJoinQuery getOuterType() {
         return CharJoinQuery.this;
      }
   }

   public CharJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.CHAR_ID, expiresIn, queryId);
   }

   public void add(String value) {
      entries.add(new CharJoinEntry(value));
   }
}