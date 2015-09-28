/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.intergration;

import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributesDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author John Misinco
 */
public class PurgeAttributeTest {

   @Rule
   public TestRule db = integrationRule(this);

   @OsgiService
   public JdbcService jdbcService;
   @OsgiService
   public SqlJoinFactory sqlJoinFactory;

   @Test
   public void testPurgeAttribute() throws Exception {
      JdbcClient jdbcClient = jdbcService.getClient();
      JdbcStatement stmt = jdbcClient.getStatement();

      int prePurgeAttributeCount = getCount(jdbcClient, "osee_attribute");
      int preAttributeRows = getCount(jdbcClient, "osee_attribute where value = 'Software Requirements'");

      Assert.assertTrue(preAttributeRows > 0);

      int prePurgeTxsCount = getCount(jdbcClient, "osee_txs");

      stmt.runPreparedQuery("select attr_id from osee_attribute where value = 'Software Requirements'");
      List<Long> toPurge = new LinkedList<>();
      while (stmt.next()) {
         toPurge.add(stmt.getLong("attr_id"));
      }

      PurgeAttributesDatabaseTxCallable callable =
         new PurgeAttributesDatabaseTxCallable(null, null, jdbcClient, sqlJoinFactory, toPurge, null);

      callable.call();

      int postPurgeAttributeCount = getCount(jdbcClient, "osee_attribute");
      int postAttributeRows = getCount(jdbcClient, "osee_attribute where value = 'Software Requirements'");
      int postPurgeTxsCount = getCount(jdbcClient, "osee_txs");

      Assert.assertEquals(0, postAttributeRows);
      Assert.assertEquals(prePurgeAttributeCount - preAttributeRows, postPurgeAttributeCount);
      Assert.assertTrue(postPurgeAttributeCount < prePurgeAttributeCount);
      Assert.assertTrue(postPurgeTxsCount < prePurgeTxsCount);
   }

   private int getCount(JdbcClient jdbcClient, String table) {
      JdbcStatement stmt = jdbcClient.getStatement();
      int toReturn = -1;
      stmt.runPreparedQuery("select count(1) from " + table);
      while (stmt.next()) {
         toReturn = stmt.getInt(1);
      }
      return toReturn;
   }
}
