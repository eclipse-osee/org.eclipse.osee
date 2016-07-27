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

import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.Baselined;
import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.NonBaselined;
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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsIntegrationByClassRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.mock.OseeClassDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.junit.AfterClass;
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
   public TestRule db = OrcsIntegrationByClassRule.integrationRule(this);

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private final static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   private QueryFactory factory;
   private final int OseeSystemId = SystemUser.OseeSystem.getId().intValue();
   private final int JoeSmithId = 61106791;
   private final int CommonBranchHeadTransaction = 117;
   private final String CommonBranchTransComment = "Create new Agile Feature Group";
   private final int NumberCommonTransactions = 77;

   @Before
   public void setup() {
      factory = orcsApi.getQueryFactory();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      OseeClassDatabase.cleanup();
   }

   @Test
   public void testGetAll() throws Exception {
      TransactionQuery query = factory.transactionQuery();

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, 4, Baselined, "Branch Creation for Common", 570, OseeSystemId, 0);
      assertTx(transactions, 5, NonBaselined, "Add Types to Common Branch", 570, OseeSystemId, 0);
   }

   @Test
   public void testGetByTxId() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(3, 23);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 3);
      assertTxExists(transactions, 23);

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(3), ids.next());
      assertEquals(Integer.valueOf(23), ids.next());
   }

   @Test
   public void testGetByCommentEquals() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentEquals("Branch Creation for SAW_Bld_1");

      TransactionReadable actual = query.getResults().getExactlyOne();

      assertEquals(1, query.getCount());
      assertTx(Arrays.asList(actual), 26, Baselined, "Branch Creation for SAW_Bld_1", 3, OseeSystemId, 0);
      assertEquals(Integer.valueOf(26), query.getResultsAsIds().getExactlyOne());
   }

   @Test
   public void testGetByCommentPatternEquals() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentPattern("Branch Creation for.*");

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

   }

   @Test
   public void testGetByType() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();

      query.andIs(Baselined);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(8, results.size());
      assertEquals(8, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, 1, Baselined, "System Root Branch Creation", 1, OseeSystemId, 0);
      assertTx(transactions, 4, Baselined, "Branch Creation for Common", 570, OseeSystemId, 0);
      assertTx(transactions, 26, Baselined, "Branch Creation for SAW_Bld_1", 3, OseeSystemId, 0);
      assertTx(transactions, 28, Baselined, "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);
      assertTx(transactions, 66, Baselined, "Branch Creation for SAW_Bld_2", 5, JoeSmithId, 0);
   }

   @Test
   public void testGetByBranchIds() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andBranchIds(5, 4);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, 28, Baselined, "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);
      assertTx(transactions, 66, Baselined, "Branch Creation for SAW_Bld_2", 5, JoeSmithId, 0);

   }

   @Test
   public void testGetByBranchUuids() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andBranch(SAW_Bld_1, CIS_Bld_1);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(8, results.size());
      assertEquals(8, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      //@formatter:off
      assertTx(transactions, 26, Baselined,    "Branch Creation for SAW_Bld_1", 3, OseeSystemId, 0);
      assertTx(transactions, 27, NonBaselined, "DemoDatabaseConfig", 3, OseeSystemId, 0);
      assertTx(transactions, 28, Baselined,    "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);
      assertTx(transactions, 29, NonBaselined, "DemoDatabaseConfig", 4,OseeSystemId, 0);
      assertTx(transactions, 62, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, JoeSmithId, 0);
      assertTx(transactions, 63, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, JoeSmithId, 0);
      assertTx(transactions, 64, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, JoeSmithId, 0);
      assertTx(transactions, 65, NonBaselined, "Populate Demo DB - Create Traceability", 3, JoeSmithId, 0);
      //@formatter:on

   }

   @Test
   public void testGetLTIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN_EQ, 2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 1);
      assertTxExists(transactions, 2);

      query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN, 2);
      results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      transactions = results.getList();
      assertTxExists(transactions, 1);
   }

   @Test
   public void testGetGTIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction - 21 + 1, results.size());

      List<TransactionReadable> transactions = results.getList();
      for (int x = 21; x <= CommonBranchHeadTransaction; x++) {
         assertNotNull(getTransaction(transactions, x));
      }

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21);
      results = query.getResults();
      assertEquals(CommonBranchHeadTransaction - 21, results.size());

      transactions = results.getList();
      Assert.assertNull(getTransaction(transactions, 21));
      for (int x = 22; x <= CommonBranchHeadTransaction; x++) {
         assertNotNull(getTransaction(transactions, x));
      }

   }

   @Test
   public void testGetEQIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.EQUAL, 21);
      query.andBranch(CoreBranches.COMMON);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 21);

      query = factory.transactionQuery();
      query.andTxId(Operator.NOT_EQUAL, 10);
      query.andBranch(CoreBranches.COMMON);
      results = query.getResults();

      transactions = results.getList();
      assertEquals(NumberCommonTransactions - 1, results.size());
   }

   @Test
   public void testGetTwoIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21, Operator.LESS_THAN, 23);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 22);

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21, Operator.LESS_THAN_EQ, 23);
      results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      transactions = results.getList();
      assertTxExists(transactions, 21);
      assertTxExists(transactions, 22);
      assertTxExists(transactions, 23);

   }

   @Test
   public void testGetDateOperator() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(65);
      Timestamp date = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andDate(Operator.GREATER_THAN, date);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(CommonBranchHeadTransaction - 65, results.size());

      query = factory.transactionQuery();
      query.andDate(Operator.LESS_THAN, date);
      results = query.getResults();
      assertEquals(64, results.size());

   }

   @Test
   public void testGetDateRange() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(1);
      Timestamp date1 = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andTxId(65);
      Timestamp date2 = new Timestamp(query.getResults().getAtMostOneOrNull().getDate().getTime());

      query = factory.transactionQuery();
      query.andDate(date1, date2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(65, results.size());
   }

   @Test
   public void testGetAuthorId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorIds(OseeSystemId);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(72, results.size());
      assertEquals(72, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, 28, Baselined, "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);

      query = factory.transactionQuery();
      query.andAuthorIds(OseeSystemId, JoeSmithId);
      results = query.getResults();
      assertEquals(117, results.size());
      assertEquals(117, query.getCount());

      transactions = results.getList();
      //@formatter:off
      assertTx(transactions, 28, Baselined, "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);
      assertTx(transactions, 62, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, JoeSmithId, 0);
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, 570, OseeSystemId, 0);
      //@formatter:on

      query = factory.transactionQuery();
      ArrayList<Integer> list = new ArrayList<>(2);
      list.add(OseeSystemId);
      list.add(JoeSmithId);
      query.andAuthorIds(list);
      results = query.getResults();
      assertEquals(117, results.size());
      assertEquals(117, query.getCount());

      transactions = results.getList();
      //@formatter:off
      assertTx(transactions, 28, Baselined, "Branch Creation for CIS_Bld_1", 4, OseeSystemId, 0);
      assertTx(transactions, 62, NonBaselined, "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, JoeSmithId, 0);
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, 570, OseeSystemId, 0);
      //@formatter:on

   }

   @Test(expected = OseeArgumentException.class)
   public void testGetCommitId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andNullCommitId();
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(116, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 1);
      assertTxExists(transactions, 2);
      assertTxExists(transactions, 3);
      assertTxExists(transactions, 4);
      assertTxExists(transactions, 5);

      query = factory.transactionQuery();
      query.andCommitIds(OseeSystemId, 1);
      results = query.getResults();
      assertEquals(0, results.size());
      assertEquals(0, query.getCount());

      query = factory.transactionQuery();
      ArrayList<Integer> list = new ArrayList<>(2);
      list.add(1);
      list.add(2);
      query.andAuthorIds(list);
      results = query.getResults();
      assertEquals(0, results.size());
      assertEquals(0, query.getCount());

      /************************************************************
       * null not allowed in multiple argument case -- throws OseeArgumentException
       */
      query = factory.transactionQuery();
      query.andCommitIds(null, OseeSystemId, 1);
      results = query.getResults();

   }

   @Test
   public void testGetHead() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(1);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, 3);
   }

   @Test
   public void testGetHead2() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(CoreBranches.COMMON);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      List<TransactionReadable> transactions = results.getList();
      assertTx(transactions, CommonBranchHeadTransaction, NonBaselined, CommonBranchTransComment, 570, OseeSystemId, 0);
   }

   @Test
   public void testAuthorLocal() throws Exception {
      QueryFactory branchFactory = orcsApi.getQueryFactory();
      QueryBuilder branchQuery = branchFactory.fromBranch(CoreBranches.COMMON);
      /*********************************************************
       * Only valid user is Joe Smith
       */
      branchQuery.andUuids(Arrays.asList(Long.valueOf(JoeSmithId)));
      ResultSet<ArtifactReadable> userIds = branchQuery.getResults();
      Iterator<ArtifactReadable> itUsers = userIds.iterator();
      ArtifactId joeArt = itUsers.next();

      TransactionQuery query = factory.transactionQuery();
      query.andAuthorLocalIds(joeArt);
      assertEquals(45, query.getCount());

      /**********************************************************
       * Only valid user is 17. Verify the multiple entry by entering the same user twice
       */
      query = factory.transactionQuery();
      query.andAuthorLocalIds(joeArt, joeArt);
      assertEquals(45, query.getCount());

   }

   @Test
   public void testMultifield() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorIds(OseeSystemId);
      query.andIs(Baselined);
      query.andCommentPattern("Branch Creation for.*");
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      for (TransactionReadable trans : query.getResults()) {
         assertTrue(trans.getComment().startsWith("Branch Creation for"));
      }
   }

   @Test
   public void testGetPriorTx() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andIsPriorTx(CommonBranchHeadTransaction);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      List<TransactionReadable> transactions = results.getList();
      assertTxExists(transactions, CommonBranchHeadTransaction - 1);

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(CommonBranchHeadTransaction - 1), ids.next());
   }

   private static void assertTx(List<TransactionReadable> transactions, Integer localId, TransactionDetailsType type, String comment, long branchUuid, int authorId, int commitId) {
      TransactionReadable transaction = getTransaction(transactions, localId);
      assertEquals(localId, transaction.getLocalId());
      assertEquals(type, transaction.getTxType());
      assertNotNull(DATE_FORMATTER.format(transaction.getDate()));
      assertEquals(comment, transaction.getComment());

      assertEquals(authorId, transaction.getAuthorId());
      assertEquals((Long) branchUuid, transaction.getBranchId());
      assertEquals(commitId, transaction.getCommit());
   }

   private void assertTxExists(List<TransactionReadable> transactions, int localId) {
      assertTrue(getTransaction(transactions, localId) != null);
   }

   private static TransactionReadable getTransaction(List<TransactionReadable> transactions, Integer localId) {
      TransactionReadable transaction = null;
      for (TransactionReadable tx : transactions) {
         if (tx.getLocalId().equals(localId)) {
            transaction = tx;
            break;
         }
      }
      return transaction;
   }
}
