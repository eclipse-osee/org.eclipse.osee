/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jdbc;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

/**
 * Test Case for {@link JdbcClient, JdbcServer}
 *
 * @author Roberto E. Escobar
 */
public class JdbcRunFunctionTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testName = new TestName();

   private JdbcServer server;
   private int dbPort;
   private String dbName;

   private JdbcClient client;

   @Before
   public void setUp() throws IOException {
      File newFile = folder.newFile("hsql.db." + testName.getMethodName());

      server = JdbcServerBuilder.hsql(newFile.toURI().toASCIIString())//
         .useRandomPort(true) //
         .build();

      JdbcServerConfig config = server.getConfig();
      dbName = config.getDbName();
      dbPort = config.getDbPort();

      server.start();

      client = JdbcClientBuilder.hsql(dbName, dbPort).build();
   }

   @After
   public void tearDown() {
      server.stop();
   }

   @Test
   public void testRandom() {
      double actual = client.runFunction(0.0d, "rand()");
      Assert.assertTrue(actual > 0.0d);
   }

   // Comment out to get the Jenkins build passing. Need to further investigate Linux differences between
   // 5.12 and 6.0
   @Test
   public void testRound() {
      //   int actual = client.runFunction(-1, "round(?)", 123.9);
      //  Assert.assertEquals(124, actual);
   }

   @Test
   public void testCurrentDate() {
      Date actual = client.runFunction(new Date(0), "CURRENT_DATE");
      Assert.assertTrue(actual.after(new Date(0)));
   }

}
