/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.DemoUsers.Joe_Smith;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.Baselined;
import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.NonBaselined;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTxQueryTest {

   @Rule
   public TestRule osgi = integrationRule(this);

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private final static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   private QueryFactory factory;
   private final String CommonBranchTransComment = "Cleanup PopulateDemoActionsTest";
   private final int NumberCommonTransactions = 132;

   private final TransactionToken CommonBranchHeadTransaction = TransactionToken.valueOf(178, CoreBranches.COMMON);
   private final TransactionId CommonBranchHeadPriorTransaction = TransactionId.valueOf(177);
   private final TransactionId tx1 = TransactionId.valueOf(1);
   private final TransactionId tx2 = TransactionId.valueOf(2);
   private final TransactionId tx3 = TransactionId.valueOf(3);
   private final TransactionId tx4 = TransactionId.valueOf(4);
   private final TransactionId tx5 = TransactionId.valueOf(5);

   private final TransactionId tx21 = TransactionId.valueOf(21);
   private final TransactionId tx22 = TransactionId.valueOf(22);
   private final TransactionId tx23 = TransactionId.valueOf(23);

   private final TransactionId tx30 = TransactionId.valueOf(30);
   private final TransactionId tx31 = TransactionId.valueOf(31);
   private final TransactionId tx32 = TransactionId.valueOf(32);
   private final TransactionId tx33 = TransactionId.valueOf(33);
   private final TransactionId tx34 = TransactionId.valueOf(34);
   private final TransactionId tx65 = TransactionId.valueOf(65);
   private final TransactionId tx69 = TransactionId.valueOf(69);
   private final TransactionId tx70 = TransactionId.valueOf(70);
   private final TransactionId tx71 = TransactionId.valueOf(71);
   private final TransactionId tx72 = TransactionId.valueOf(72);
   private final TransactionId tx73 = TransactionId.valueOf(73);

   @Before
   public void setup() {
      factory = orcsApi.getQueryFactory();
   }

   @Test
   public void testGetAll() throws Exception {
      TransactionQuery query = factory.transactionQuery();

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction, Long.valueOf(results.size()));

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, tx4, Baselined, "New Branch from System Root Branch (3)", COMMON, OseeSystem);
      assertTx(transactions, tx5, NonBaselined, "Add Types to Common Branch", COMMON, OseeSystem);
   }

   @Test
   public void testGetByTxId() {
      TransactionQuery query = factory.transactionQuery();
      query.andTxIds(Arrays.asList(TransactionId.valueOf(3), TransactionId.valueOf(23)));

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx3);
      assertTxExists(transactions, tx23);

      Iterator<TransactionId> ids = query.getResultsAsIds().iterator();
      assertEquals(tx3, ids.next());
      assertEquals(tx23, ids.next());
   }

   @Test
   public void testGetByCommentEquals() {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentEquals("New Branch from System Root Branch (3)");

      List<TransactionReadable> actual = query.getResults().getList();

      assertEquals(3, query.getCount());
      assertTx(actual, tx30, Baselined, "New Branch from System Root Branch (3)", SAW_Bld_1, OseeSystem);
   }

   @Test
   public void testGetByCommentPatternEquals() {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentPattern("New Branch from.*");

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(7, results.size());
      assertEquals(7, query.getCount());

   }

   @Test
   public void testGetByType() {
      TransactionQuery query = factory.transactionQuery();

      query.andIs(Baselined);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(8, results.size());
      assertEquals(8, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, tx1, Baselined, "System Root Branch Creation", SYSTEM_ROOT, OseeSystem);
      assertTx(transactions, tx4, Baselined, "New Branch from System Root Branch (3)", COMMON, OseeSystem);
      assertTx(transactions, tx30, Baselined, "New Branch from System Root Branch (3)", SAW_Bld_1, OseeSystem);
      assertTx(transactions, tx33, Baselined, "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);
      assertTx(transactions, tx73, Baselined, "New Branch from SAW_Bld_1 (72)", SAW_Bld_2, Joe_Smith);
   }

   @Test
   public void testGetByBranchIds() {
      TransactionQuery query = factory.transactionQuery();
      query.andBranchIds(Arrays.asList(BranchId.valueOf(5L), BranchId.valueOf(4L)));

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, tx33, Baselined, "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);
      assertTx(transactions, tx73, Baselined, "New Branch from SAW_Bld_1 (72)", SAW_Bld_2, Joe_Smith);
   }

   @Test
   public void testGetByBranchUuids() {
      TransactionQuery query = factory.transactionQuery();
      query.andBranch(SAW_Bld_1, CIS_Bld_1);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(9, results.size());
      assertEquals(9, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      //@formatter:off
      assertTx(transactions, tx30, Baselined,    "New Branch from System Root Branch (3)", SAW_Bld_1, OseeSystem);
      assertTx(transactions, tx31, NonBaselined, "DemoDatabaseConfig", SAW_Bld_1, OseeSystem);
      assertTx(transactions, tx32, NonBaselined, "Create Demo Applicability", SAW_Bld_1, OseeSystem);
      assertTx(transactions, tx33, Baselined,    "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);
      assertTx(transactions, tx34, NonBaselined, "DemoDatabaseConfig", CIS_Bld_1,OseeSystem);

      assertTx(transactions, tx69, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", SAW_Bld_1, Joe_Smith);
      assertTx(transactions, tx70, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", SAW_Bld_1, Joe_Smith);
      assertTx(transactions, tx71, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", SAW_Bld_1, Joe_Smith);
      assertTx(transactions, tx72, NonBaselined, "Populate Demo DB - Create Traceability", SAW_Bld_1, Joe_Smith);
      //@formatter:on

   }

   @Test
   public void testGetLTIdOperators() {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN_EQ, 2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx1);
      assertTxExists(transactions, tx2);

      query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN, 2);
      results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      transactions = results.getList();
      assertTxExists(transactions, tx1);
   }

   @Test
   public void testGetGTIdOperators() {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction.getId() - 21 + 1, results.size());

      List<TransactionReadable> transactions = results.getList();
      for (Long x = tx21.getId(); x <= CommonBranchHeadTransaction.getId(); x++) {
         assertNotNull(getTransaction(transactions, x));
      }

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21);
      results = query.getResults();
      assertEquals(CommonBranchHeadTransaction.getId() - 21, results.size());

      transactions = results.getList();
      Assert.assertNull(getTransaction(transactions, tx21));
      for (Long x = tx22.getId(); x <= CommonBranchHeadTransaction.getId(); x++) {
         assertNotNull(getTransaction(transactions, x));
      }

   }

   @Test
   public void testGetEQIdOperators() {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.EQUAL, 21);
      query.andBranch(COMMON);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx21);

      query = factory.transactionQuery();
      query.andTxId(Operator.NOT_EQUAL, 10);
      query.andBranch(COMMON);
      results = query.getResults();

      transactions = results.getList();
      assertEquals(NumberCommonTransactions - 1 + 5, results.size());
   }

   @Test
   public void testGetTwoIdOperators() {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21, Operator.LESS_THAN, 23);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx22);

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21, Operator.LESS_THAN_EQ, 23);
      results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      transactions = results.getList();
      assertTxExists(transactions, tx21);
      assertTxExists(transactions, tx22);
      assertTxExists(transactions, tx23);

   }

   @Test
   public void testGetDateOperator() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(tx65);
      Timestamp date = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andDate(Operator.GREATER_THAN, date);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction.getId() - tx65.getId(), results.size());

      query = factory.transactionQuery();
      query.andDate(Operator.LESS_THAN, date);
      results = query.getResults();
      assertEquals(64, results.size());

   }

   @Test
   public void testGetDateRange() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(TransactionId.valueOf(1));
      Timestamp date1 = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andTxId(tx65);
      Timestamp date2 = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andDate(date1, date2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(65, results.size());
   }

   @Test
   public void testGetAuthorId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorId(OseeSystem);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(128, results.size());
      assertEquals(128, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, tx33, Baselined, "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);

      query = factory.transactionQuery();
      query.andAuthorIds(Arrays.asList(OseeSystem, Joe_Smith));
      results = query.getResults();
      assertEquals(178, results.size());
      assertEquals(178, query.getCount());

      transactions = results.getList();
      //@formatter:off
      assertTx(transactions, tx33, Baselined, "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);
      assertTx(transactions, tx69, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", SAW_Bld_1, Joe_Smith);
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, COMMON, Joe_Smith);
      //@formatter:on

      query = factory.transactionQuery();
      ArrayList<ArtifactId> list = new ArrayList<>(2);
      list.add(OseeSystem);
      list.add(Joe_Smith);
      query.andAuthorIds(list);
      results = query.getResults();
      assertEquals(178, results.size());
      assertEquals(178, query.getCount());

      transactions = results.getList();
      //@formatter:off
      assertTx(transactions, tx33, Baselined, "New Branch from System Root Branch (3)", CIS_Bld_1, OseeSystem);
      assertTx(transactions, tx69, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", SAW_Bld_1, Joe_Smith);
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, COMMON, Joe_Smith);
      //@formatter:on

   }

   @Test(expected = OseeArgumentException.class)
   public void testGetCommitId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andNullCommitId();
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(177, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx1);
      assertTxExists(transactions, tx2);
      assertTxExists(transactions, tx3);
      assertTxExists(transactions, tx4);
      assertTxExists(transactions, tx5);

      query = factory.transactionQuery();
      query.andCommitIds(OseeSystem.getId().intValue(), 1);
      results = query.getResults();
      assertEquals(0, results.size());
      assertEquals(0, query.getCount());

      query = factory.transactionQuery();
      ArrayList<ArtifactId> list = new ArrayList<>(2);
      list.add(ArtifactId.valueOf(1));
      list.add(ArtifactId.valueOf(2));
      query.andAuthorIds(list);
      results = query.getResults();
      assertEquals(0, results.size());
      assertEquals(0, query.getCount());

      /************************************************************
       * null not allowed in multiple argument case --
       */
      query = factory.transactionQuery();
      query.andCommitIds(null, OseeSystem.getId().intValue(), 1);
      results = query.getResults();

   }

   @Test
   public void testGetHead() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(CoreBranches.SYSTEM_ROOT);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, tx3);
   }

   @Test
   public void testGetHead2() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(COMMON);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, COMMON, Joe_Smith);
   }

   @Test
   public void testAuthorLocal() throws Exception {
      QueryFactory branchFactory = orcsApi.getQueryFactory();
      QueryBuilder branchQuery = branchFactory.fromBranch(COMMON);
      /*********************************************************
       * Only valid user is Joe Smith
       */
      branchQuery.andUuid(Joe_Smith.getId());
      ResultSet<ArtifactReadable> userIds = branchQuery.getResults();
      Iterator<ArtifactReadable> itUsers = userIds.iterator();
      ArtifactId joeArt = itUsers.next();

      TransactionQuery query = factory.transactionQuery();
      query.andAuthorId(joeArt);
      assertEquals(50, query.getCount());

      /**********************************************************
       * Only valid user is 17. Verify the multiple entry by entering the same user twice
       */
      query = factory.transactionQuery();
      query.andAuthorIds(Arrays.asList(joeArt, joeArt));
      assertEquals(50, query.getCount());

   }

   @Test
   public void testMultifield() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorId(OseeSystem);
      query.andIs(Baselined);
      query.andCommentPattern("New Branch from.*");
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      for (TransactionReadable trans : query.getResults()) {
         assertTrue(trans.getComment().startsWith("New Branch from"));
      }
   }

   @Test
   public void testGetPriorTx() {
      TransactionQuery query = factory.transactionQuery();
      query.andIsPriorTx(CommonBranchHeadTransaction);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, CommonBranchHeadPriorTransaction);

      Iterator<TransactionId> ids = query.getResultsAsIds().iterator();
      assertEquals(CommonBranchHeadPriorTransaction, ids.next());
   }

   private static void assertTx(List<TransactionReadable> transactions, TransactionId id, TransactionDetailsType type, String comment, BranchId branch, ArtifactId author) {
      TransactionReadable transaction = getTransaction(transactions, id);
      assertEquals(id, transaction);
      assertEquals(type, transaction.getTxType());
      assertNotNull(DATE_FORMATTER.format(transaction.getDate()));
      assertEquals(comment, transaction.getComment());

      assertEquals(author, transaction.getAuthor());
      assertEquals(branch, transaction.getBranch());
      assertTrue(transaction.getCommitArt().isInvalid());
   }

   private void assertTxExists(List<TransactionReadable> transactions, TransactionId txId) {
      assertTrue(getTransaction(transactions, txId) != null);
   }

   private static TransactionReadable getTransaction(List<TransactionReadable> transactions, Long txId) {
      for (TransactionReadable tx : transactions) {
         if (tx.equals(txId)) {
            return tx;
         }
      }
      return null;
   }

   private static TransactionReadable getTransaction(List<TransactionReadable> transactions, TransactionId txId) {
      return getTransaction(transactions, txId.getId());
   }
}
