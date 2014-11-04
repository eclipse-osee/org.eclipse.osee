/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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