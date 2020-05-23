/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;
import org.eclipse.osee.vcast.internal.VCastDataStoreImpl;

/**
 * @author Roberto E. Escobar
 */
public class VCastClient {

   private static final String JDBC_SQLITE_DRIVER = "org.sqlite.JDBC";
   private static final String JDBC_SQLITE__CONNECTION_TEMPLATE = "jdbc:sqlite:%s";

   private VCastClient() {
      // Static Factory
   }

   public static VCastDataStore newDataStore(String dbPath) {
      JdbcClient jdbcClient = JdbcClientBuilder.newBuilder()//
         .dbDriver(JDBC_SQLITE_DRIVER)//
         .dbUri(JDBC_SQLITE__CONNECTION_TEMPLATE, dbPath)//
         .build();
      return new VCastDataStoreImpl(jdbcClient);
   }
}
