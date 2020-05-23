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

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Roberto E. Escobar
 */
public final class JoinUtility {

   private JoinUtility() {
      // Utility Class
   }

   public static IdJoinQuery createIdJoinQuery(JdbcClient jdbcClient, JdbcConnection connection) {
      return new IdJoinQuery(jdbcClient, connection);
   }

   public static IdJoinQuery createIdJoinQuery() {
      return createIdJoinQuery(ConnectionHandler.getJdbcClient(), null);
   }

   public static Id4JoinQuery createId4JoinQuery(JdbcClient jdbcClient, JdbcConnection connection) {
      return new Id4JoinQuery(jdbcClient, connection);
   }

   public static Id4JoinQuery createId4JoinQuery() {
      return createId4JoinQuery(ConnectionHandler.getJdbcClient(), null);
   }
}