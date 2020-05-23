/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.db.internal.sql;

import java.util.Comparator;

/**
 * @author Roberto E. Escobar
 */
public final class SqlHandlerComparator implements Comparator<SqlHandler<?>> {

   @Override
   public int compare(SqlHandler<?> left, SqlHandler<?> right) {
      int result = left.getLevel() - right.getLevel();
      if (result == 0) {
         result = left.getPriority() - right.getPriority();
      }
      return result;
   }

}