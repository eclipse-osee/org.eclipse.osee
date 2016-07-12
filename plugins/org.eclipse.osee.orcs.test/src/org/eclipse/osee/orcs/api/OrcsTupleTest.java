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

import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
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
   public TestRule osgi = integrationRule(this);

   @Rule
   public final ExpectedException exception = ExpectedException.none();

   @OsgiService
   private OrcsApi orcsApi;

   private KeyValueOps keyValueOps;

   @Before
   public void setUp() throws Exception {
      keyValueOps = orcsApi.getKeyValueOps();
   }

   @Test
   public void testPutIfAbsent() throws OseeCoreException {
      String newValue = "hello";
      Long key = keyValueOps.putIfAbsent(newValue);

      Assert.assertTrue(key > 0L);

      Long keyAttempt2 = keyValueOps.putIfAbsent(newValue);
      Assert.assertEquals(key, keyAttempt2);
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple2() throws OseeCoreException {
      TupleTypeId createTuple2Type = TokenFactory.createTupleType(24L);
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON_ID, OseeSystem, "Add Tuple2 Test");
      Long gamma_id = transaction.addTuple(createTuple2Type, 234L, "t");
      transaction.commit();

      Assert.assertTrue(gamma_id > 0L);

      gamma_id = transaction.addTuple(createTuple2Type, 234L, "t");
      transaction.commit();
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple3() throws OseeCoreException {
      TupleTypeId createTuple3Type = TokenFactory.createTupleType(44L);
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON_ID, OseeSystem, "Add Tuple3 Test");
      Long gamma_id = transaction.addTuple(createTuple3Type, 244L, 12L, "three");
      transaction.commit();

      Assert.assertTrue(gamma_id > 0L);

      gamma_id = transaction.addTuple(createTuple3Type, 244L, 12L, "three");
      transaction.commit();
   }

   @Test(expected = JdbcException.class)
   public void testAddTuple4() throws OseeCoreException {
      TupleTypeId createTuple4Type = TokenFactory.createTupleType(44L);
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON_ID, OseeSystem, "Add Tuple4 Test");
      Long gamma_id = transaction.addTuple(createTuple4Type, 244L, 12L, "four", "four2");
      transaction.commit();

      Assert.assertTrue(gamma_id > 0L);

      gamma_id = transaction.addTuple(createTuple4Type, 244L, 12L, "four", "four2");
      transaction.commit();
   }
}
