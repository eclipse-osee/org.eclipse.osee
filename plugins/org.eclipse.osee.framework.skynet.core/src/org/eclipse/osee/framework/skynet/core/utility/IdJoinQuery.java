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
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.utility.DatabaseJoinAccessor.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public class IdJoinQuery extends AbstractJoinQuery {

   private final class TempIdEntry implements IJoinRow {
      private final Long id;

      private TempIdEntry(Long id) {
         this.id = id;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), id};
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof TempIdEntry)) {
            return false;
         }
         TempIdEntry other = (TempIdEntry) obj;
         return other.id.equals(this.id);
      }

      @Override
      public int hashCode() {
         return 37 * id.hashCode();
      }

      @Override
      public String toString() {
         return "id = " + id;
      }
   }

   public IdJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.ID, expiresIn, queryId);
   }

   public void add(Number id) {
      entries.add(new TempIdEntry(id.longValue()));
   }

   public void add(Id id) {
      entries.add(new TempIdEntry(id.getId()));
   }
}