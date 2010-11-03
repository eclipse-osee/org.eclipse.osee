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
public final class SearchTagJoinQuery extends AbstractJoinQuery {

   private final class TagEntry implements IJoinRow {
      private final long value;

      private TagEntry(Long value) {
         this.value = value;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), value};
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof TagEntry)) {
            return false;
         }
         TagEntry other = (TagEntry) obj;
         return this.value == other.value;
      }

      @Override
      public int hashCode() {
         return Long.valueOf(37 * value).hashCode();
      }

      @Override
      public String toString() {
         return String.format("tag=%s", value);
      }
   }

   protected SearchTagJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.SEARCH_TAGS, queryId);
   }

   public void add(Long tag) {
      entries.add(new TagEntry(tag));
   }
}