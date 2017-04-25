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