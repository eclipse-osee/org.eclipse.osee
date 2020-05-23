/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Roberto E. Escobar
 */
public final class TagQueueJoinQuery extends AbstractJoinQuery {
   protected TagQueueJoinQuery(JdbcClient jdbcClient, JdbcConnection connection) {
      super(JoinItem.TAG_GAMMA_QUEUE, jdbcClient, connection);
   }

   public void add(Long gammaId) {
      addToBatch(gammaId);
   }
}