/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.Everyone;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.LruDataTypes;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.ViewApplicability;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OrcsIntegrationByClassRule;
import org.eclipse.osee.orcs.db.mock.OseeClassDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

/**
 * @author Angel Avila
 */
public class OrcsTupleTest {

   @Rule
   public TestRule db = OrcsIntegrationByClassRule.integrationRule(this);

   @Rule
   public final ExpectedException exception = ExpectedException.none();

   @OsgiService
   private OrcsApi orcsApi;

   private KeyValueOps keyValueOps;

   @Before
   public void setUp() throws Exception {
      keyValueOps = orcsApi.getKeyValueOps();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      OseeClassDatabase.cleanup();
   }

   @Test
   public void testPutIfAbsent()  {
      String newValue = "hello";
      Long key = keyValueOps.putIfAbsent(newValue);

      Assert.assertTrue(key > 0L);

      Long keyAttempt2 = keyValueOps.putIfAbsent(newValue);
      Assert.assertEquals(key, keyAttempt2);
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple2()  {
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(COMMON, OseeSystem, "Add Tuple2 Test");
      GammaId gamma_id = transaction.addTuple2(ViewApplicability, Everyone, "t");
      transaction.commit();

      Assert.assertTrue(gamma_id.isValid());

      transaction.addTuple2(ViewApplicability, Everyone, "t");
      transaction.commit();
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple3()  {
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(COMMON, OseeSystem, "Add Tuple3 Test");
      GammaId gamma_id = transaction.addTuple3(LruDataTypes, "three", 244L, 12L);
      transaction.commit();

      Assert.assertTrue(gamma_id.isValid());

      transaction.addTuple3(LruDataTypes, "three", 244L, 12L);
      transaction.commit();
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple4()  {
      Tuple4Type<Long, Long, String, String> createTuple4Type = Tuple4Type.valueOf(DefaultFamily, 40L);
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(COMMON, OseeSystem, "Add Tuple4 Test");
      GammaId gamma_id = transaction.addTuple4(createTuple4Type, 244L, 12L, "four", "four2");
      transaction.commit();

      Assert.assertTrue(gamma_id.isValid());

      transaction.addTuple4(createTuple4Type, 244L, 12L, "four", "four2");
      transaction.commit();
   }

   @Test
   public void testGetTupleType2()  {
      ResultSet<ArtifactReadable> branchViewArts =
         orcsApi.getQueryFactory().fromBranch(DemoBranches.SAW_Bld_1).andTypeEquals(
            CoreArtifactTypes.BranchView).getResults();
      List<ApplicabilityToken> result = new ArrayList<>();

      BiConsumer<Long, String> consumer = (id, name) -> result.add(new ApplicabilityToken(id, name));
      orcsApi.getQueryFactory().tupleQuery().getTuple2KeyValuePair(CoreTupleTypes.ViewApplicability,
         branchViewArts.getList().get(0), DemoBranches.SAW_Bld_1, consumer);

      Assert.assertEquals(5, result.size());
   }
}
