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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.db.mock.OseeDatabase.integrationRule;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributesDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author John Misinco
 */
public class PurgeAttributeTest {

   private static final String UNIQUE_ATTR_VALUE = "AEqvcQoyRV8zNDZSBVAA";

   @Rule
   public TestRule db = integrationRule(this);

   @OsgiService
   public OrcsApi orcsApi;
   @OsgiService
   public JdbcService jdbcService;
   @OsgiService
   public SqlJoinFactory sqlJoinFactory;

   @After
   @Before
   public void setupAndTeardown() {
      TransactionBuilder tx = null;
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(COMMON).and(CoreAttributeTypes.Annotation,
         UNIQUE_ATTR_VALUE).getResults()) {
         if (tx == null) {
            tx = orcsApi.getTransactionFactory().createTransaction(COMMON, DemoUsers.Joe_Smith,
               getClass().getSimpleName());
         }
         tx.deleteArtifact(art);
      }
      if (tx != null) {
         tx.commit();
      }
   }

   @Test
   public void testPurgeAttribute() throws Exception {
      JdbcClient jdbcClient = jdbcService.getClient();

      if (jdbcClient.getConfig().isProduction()) {
         throw new OseeStateException("Test should not be run against a Production Database");
      }

      // Setup test
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(COMMON, DemoUsers.Joe_Smith, getClass().getSimpleName());
      tx.createAttribute(CoreArtifactTokens.UserGroups, CoreAttributeTypes.Annotation, UNIQUE_ATTR_VALUE);
      tx.createAttribute(CoreArtifactTokens.Everyone, CoreAttributeTypes.Annotation, UNIQUE_ATTR_VALUE);
      tx.createAttribute(CoreArtifactTokens.OseeAdmin, CoreAttributeTypes.Annotation, UNIQUE_ATTR_VALUE);
      tx.commit();

      int prePurgeAttributeCount = getCount(jdbcClient, "osee_attribute");
      int preAttributeRows = getCount(jdbcClient, "osee_attribute where value = ?", UNIQUE_ATTR_VALUE);

      Assert.assertEquals(3, preAttributeRows);

      List<AttributeId> toPurge = getAttributesToPurge(jdbcClient);

      PurgeAttributesDatabaseTxCallable callable =
         new PurgeAttributesDatabaseTxCallable(null, null, jdbcClient, sqlJoinFactory, toPurge, null);

      callable.call();

      int postPurgeAttributeCount = getCount(jdbcClient, "osee_attribute");
      int postAttributeRows =
         getCount(jdbcClient, String.format("osee_attribute where value = '%s'", UNIQUE_ATTR_VALUE));

      Assert.assertEquals(0, postAttributeRows);
      Assert.assertEquals(prePurgeAttributeCount - preAttributeRows, postPurgeAttributeCount);
      Assert.assertTrue(postPurgeAttributeCount < prePurgeAttributeCount);
   }

   private List<AttributeId> getAttributesToPurge(JdbcClient jdbcClient) {
      List<AttributeId> toPurge = new LinkedList<>();
      try (JdbcStatement stmt = jdbcClient.getStatement()) {
         stmt.runPreparedQuery(
            String.format("select attr_id from osee_attribute where value = '%s'", UNIQUE_ATTR_VALUE));
         while (stmt.next()) {
            toPurge.add(AttributeId.valueOf(stmt.getLong("attr_id")));
         }
      }
      return toPurge;
   }

   private int getCount(JdbcClient jdbcClient, String table, Object... data) {
      return jdbcClient.fetch(-1, "select count(1) from " + table, data);
   }
}
