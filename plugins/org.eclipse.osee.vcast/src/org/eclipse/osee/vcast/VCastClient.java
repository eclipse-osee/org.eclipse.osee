/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
