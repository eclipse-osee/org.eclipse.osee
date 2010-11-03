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
      public int hashCode() {
         return 37 * value.hashCode();
      }

      @Override
      public String toString() {
         return value;
      }
   }

   protected CharJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.CHAR_ID, queryId);
   }

   public void add(String value) {
      entries.add(new CharJoinEntry(value));
   }
}