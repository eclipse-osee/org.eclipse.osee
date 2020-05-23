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

package org.eclipse.osee.orcs.db.internal.loader.processor;

import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractLoadProcessor<H> {

   public final int processResultSet(H handler, JdbcStatement chStmt, Options options) {
      int rowCount = 0;
      Object conditions = createPreConditions(options);
      while (chStmt.next()) {
         rowCount++;
         onRow(handler, chStmt, options, conditions);
      }
      onEnd(handler, options, conditions, rowCount);
      return rowCount;
   }

   protected Object createPreConditions(Options options) {
      return null;
   }

   protected abstract void onRow(H handler, JdbcStatement chStmt, Options options, Object conditions);

   protected void onEnd(H handler, Options options, Object conditions, int rowCount) {
      // do nothing;
   }
}
