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

import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.Baselined;
import static org.eclipse.osee.framework.core.enums.TransactionDetailsType.NonBaselined;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.eclipse.osee.orcs.api.TestBranches.CIS_Bld_1;
import static org.eclipse.osee.orcs.api.TestBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
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
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private final static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   private QueryFactory factory;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      factory = orcsApi.getQueryFactory(context);
   }

   @Test
   public void testGetAll() throws Exception {
      TransactionQuery query = factory.transactionQuery();

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(24, results.size());
      assertEquals(24, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 2,  NonBaselined, "2013-05-06 14:18:05.205", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 3,  NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 5,  NonBaselined, "2013-05-06 14:18:05.487", "Add Types to Common Branch", 570, 0, 0);
      assertTx(it.next(), 6,  NonBaselined, "2013-05-06 14:18:06.454", "Add Common Branch", 570, 0, 0);
      assertTx(it.next(), 7,  NonBaselined, "2013-05-06 14:18:06.517", "Add Framework Access Model", 570, 0, 0);
      assertTx(it.next(), 8,  NonBaselined, "2013-05-06 14:18:06.720", "Add Dev Users", 570, 0, 0);
      assertTx(it.next(), 9,  NonBaselined, "2013-05-06 14:18:06.798", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 10, NonBaselined, "2013-05-06 14:18:06.860", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 11, NonBaselined, "2013-05-06 14:18:06.954", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 12, NonBaselined, "2013-05-06 14:18:07.032", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 13, NonBaselined, "2013-05-06 14:18:07.110", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 14, NonBaselined, "2013-05-06 14:18:07.204", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on
   }

   @Test
   public void testGetByTxId() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(3, 23);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 3, NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(3), ids.next());
      assertEquals(Integer.valueOf(23), ids.next());
   }

   @Test
   public void testGetByCommentEquals() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentEquals("ClientUser");

      TransactionReadable actual = query.getResults().getExactlyOne();

      assertEquals(1, query.getCount());
      assertTx(actual, 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      assertEquals(Integer.valueOf(24), query.getResultsAsIds().getExactlyOne());
   }

   @Test
   public void testGetByCommentPatternEquals() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andCommentPattern("Branch Creation for.*");

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);      
      //@formatter:on

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(4), ids.next());
      assertEquals(Integer.valueOf(15), ids.next());
      assertEquals(Integer.valueOf(17), ids.next());
      assertEquals(Integer.valueOf(23), ids.next());
   }

   @Test
   public void testGetByType() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();

      query.andIs(Baselined);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(5, results.size());
      assertEquals(5, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      //@formatter:on

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(1), ids.next());
      assertEquals(Integer.valueOf(4), ids.next());
      assertEquals(Integer.valueOf(15), ids.next());
      assertEquals(Integer.valueOf(17), ids.next());
      assertEquals(Integer.valueOf(23), ids.next());
   }

   @Test
   public void testGetByBranchIds() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andBranchIds(5, 4);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      //@formatter:on

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(17), ids.next());
      assertEquals(Integer.valueOf(18), ids.next());
      assertEquals(Integer.valueOf(23), ids.next());
   }

   @Test
   public void testGetByBranchUuids() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andBranch(SAW_Bld_1, CIS_Bld_1);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(8, results.size());
      assertEquals(8, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      //@formatter:on

      Iterator<Integer> ids = query.getResultsAsIds().iterator();
      assertEquals(Integer.valueOf(15), ids.next());
      assertEquals(Integer.valueOf(16), ids.next());
      assertEquals(Integer.valueOf(17), ids.next());
      assertEquals(Integer.valueOf(18), ids.next());
      assertEquals(Integer.valueOf(19), ids.next());
      assertEquals(Integer.valueOf(20), ids.next());
      assertEquals(Integer.valueOf(21), ids.next());
      assertEquals(Integer.valueOf(22), ids.next());
   }

   @Test
   public void testGetLTIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN_EQ, 2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 1, Baselined, "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 2, NonBaselined, "2013-05-06 14:18:05.205", "AddCommonBranchForClientDemo", 1, 0, 0);

      query = factory.transactionQuery();
      query.andTxId(Operator.LESS_THAN, 2);
      results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      it = results.iterator();
      assertTx(it.next(), 1, Baselined, "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
   }

   @Test
   public void testGetGTIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163",
         "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0,
         0);
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21);
      results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      it = results.iterator();
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0,
         0);
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);

   }

   @Test
   public void testGetEQIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.EQUAL, 21);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163",
         "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);

      query = factory.transactionQuery();
      query.andTxId(Operator.NOT_EQUAL, 10);
      results = query.getResults();
      assertEquals(23, results.size());
      assertEquals(23, query.getCount());

      it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 2,  NonBaselined, "2013-05-06 14:18:05.205", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 3,  NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 5,  NonBaselined, "2013-05-06 14:18:05.487", "Add Types to Common Branch", 570, 0, 0);
      assertTx(it.next(), 6,  NonBaselined, "2013-05-06 14:18:06.454", "Add Common Branch", 570, 0, 0);
      assertTx(it.next(), 7,  NonBaselined, "2013-05-06 14:18:06.517", "Add Framework Access Model", 570, 0, 0);
      assertTx(it.next(), 8,  NonBaselined, "2013-05-06 14:18:06.720", "Add Dev Users", 570, 0, 0);
      assertTx(it.next(), 9,  NonBaselined, "2013-05-06 14:18:06.798", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 11, NonBaselined, "2013-05-06 14:18:06.954", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 12, NonBaselined, "2013-05-06 14:18:07.032", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 13, NonBaselined, "2013-05-06 14:18:07.110", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 14, NonBaselined, "2013-05-06 14:18:07.204", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on
   }

   @Test
   public void testGetTwoIdOperators() throws OseeCoreException {
      TransactionQuery query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN, 21, Operator.LESS_THAN, 23);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0,
         0);

      query = factory.transactionQuery();
      query.andTxId(Operator.GREATER_THAN_EQ, 21, Operator.LESS_THAN_EQ, 23);
      results = query.getResults();
      assertEquals(3, results.size());
      assertEquals(3, query.getCount());

      it = results.iterator();
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163",
         "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0,
         0);
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);

   }

   @Test
   public void testGetDateOperator() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      Timestamp date = Timestamp.valueOf("2013-05-06 14:18:11.570");
      query.andDate(Operator.GREATER_THAN, date);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);

      query = factory.transactionQuery();
      query.andDate(Operator.LESS_THAN, date);
      results = query.getResults();
      assertEquals(22, results.size());
      assertEquals(22, query.getCount());

      it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 2,  NonBaselined, "2013-05-06 14:18:05.205", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 3,  NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 5,  NonBaselined, "2013-05-06 14:18:05.487", "Add Types to Common Branch", 570, 0, 0);
      assertTx(it.next(), 6,  NonBaselined, "2013-05-06 14:18:06.454", "Add Common Branch", 570, 0, 0);
      assertTx(it.next(), 7,  NonBaselined, "2013-05-06 14:18:06.517", "Add Framework Access Model", 570, 0, 0);
      assertTx(it.next(), 8,  NonBaselined, "2013-05-06 14:18:06.720", "Add Dev Users", 570, 0, 0);
      assertTx(it.next(), 9,  NonBaselined, "2013-05-06 14:18:06.798", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 10, NonBaselined, "2013-05-06 14:18:06.860", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 11, NonBaselined, "2013-05-06 14:18:06.954", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 12, NonBaselined, "2013-05-06 14:18:07.032", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 13, NonBaselined, "2013-05-06 14:18:07.110", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 14, NonBaselined, "2013-05-06 14:18:07.204", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      //@formatter:on
   }

   @Test
   public void testGetDateRange() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      Timestamp date1 = Timestamp.valueOf("2013-05-06 14:18:07.110");
      Timestamp date2 = Timestamp.valueOf("2013-05-06 14:18:11.659");
      query.andDate(date1, date2);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(11, results.size());
      assertEquals(11, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 13, NonBaselined, "2013-05-06 14:18:07.110", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 14, NonBaselined, "2013-05-06 14:18:07.204", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      assertTx(it.next(), 23, Baselined, "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      //@formatter:on
   }

   @Test
   public void testGetAuthorId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorIds(-1);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(5, results.size());
      assertEquals(5, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      //@formatter:on

      query = factory.transactionQuery();
      query.andAuthorIds(-1, 17);
      results = query.getResults();
      assertEquals(6, results.size());
      assertEquals(6, query.getCount());

      it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on

      query = factory.transactionQuery();
      ArrayList<Integer> list = new ArrayList<Integer>(2);
      list.add(-1);
      list.add(17);
      query.andAuthorIds(list);
      results = query.getResults();
      assertEquals(6, results.size());
      assertEquals(6, query.getCount());

      it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on

   }

   @Test(expected = OseeArgumentException.class)
   public void testGetCommitId() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andNullCommitId();
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(24, query.getCount());
      assertEquals(24, results.size());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 1,  Baselined,    "2013-05-06 14:18:02.530", "System Root Branch Creation", 1, -1, 0);
      assertTx(it.next(), 2,  NonBaselined, "2013-05-06 14:18:05.205", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 3,  NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 5,  NonBaselined, "2013-05-06 14:18:05.487", "Add Types to Common Branch", 570, 0, 0);
      assertTx(it.next(), 6,  NonBaselined, "2013-05-06 14:18:06.454", "Add Common Branch", 570, 0, 0);
      assertTx(it.next(), 7,  NonBaselined, "2013-05-06 14:18:06.517", "Add Framework Access Model", 570, 0, 0);
      assertTx(it.next(), 8,  NonBaselined, "2013-05-06 14:18:06.720", "Add Dev Users", 570, 0, 0);
      assertTx(it.next(), 9,  NonBaselined, "2013-05-06 14:18:06.798", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 10, NonBaselined, "2013-05-06 14:18:06.860", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 11, NonBaselined, "2013-05-06 14:18:06.954", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 12, NonBaselined, "2013-05-06 14:18:07.032", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 13, NonBaselined, "2013-05-06 14:18:07.110", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 14, NonBaselined, "2013-05-06 14:18:07.204", "SimpleTemplateProviderTask", 570, 0, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 16, NonBaselined, "2013-05-06 14:18:07.360", "AddSawBranch", 3, 0, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 18, NonBaselined, "2013-05-06 14:18:07.630", "AddCisBranch", 4, 0, 0);
      assertTx(it.next(), 19, NonBaselined, "2013-05-06 14:18:07.911", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 20, NonBaselined, "2013-05-06 14:18:08.775", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 21, NonBaselined, "2013-05-06 14:18:10.163", "ArtifactImportOperationFactory: Artifact Import Wizard transaction", 3, 0, 0);
      assertTx(it.next(), 22, NonBaselined, "2013-05-06 14:18:11.564", "Populate Demo DB - Create Traceability", 3, 0, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on

      query = factory.transactionQuery();
      query.andCommitIds(-1, 1);
      results = query.getResults();
      assertEquals(0, results.size());
      assertEquals(0, query.getCount());

      query = factory.transactionQuery();
      ArrayList<Integer> list = new ArrayList<Integer>(2);
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
      query.andCommitIds(null, -1, 1);
      results = query.getResults();

   }

   @Test
   public void testGetHead() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(1);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
     assertTx(it.next(), 3,  NonBaselined, "2013-05-06 14:18:05.393", "AddCommonBranchForClientDemo", 1, 0, 0);
      //@formatter:on
   }

   @Test
   public void testGetHead2() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andIsHead(CoreBranches.COMMON);

      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on
   }

   @Test
   public void testAuthorLocal() throws Exception {
      QueryFactory branchFactory = orcsApi.getQueryFactory(null);
      QueryBuilder branchQuery = branchFactory.fromBranch(CoreBranches.COMMON);
      /*********************************************************
       * Only valid user is 17
       */
      Integer seventeen = new Integer(17);
      branchQuery.andLocalIds(Arrays.asList(seventeen));
      ResultSet<ArtifactReadable> userIds = branchQuery.getResults();
      Iterator<ArtifactReadable> itUsers = userIds.iterator();
      ArtifactId user17 = itUsers.next();

      TransactionQuery query = factory.transactionQuery();
      query.andAuthorLocalIds(user17);
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on

      /**********************************************************
       * Only valid user is 17. Verify the multiple entry by entering the same user twice
       */
      query = factory.transactionQuery();
      query.andAuthorLocalIds(user17, user17);
      results = query.getResults();
      assertEquals(1, query.getCount());
      assertEquals(1, results.size());

      it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 24, NonBaselined, "2013-05-06 14:18:18.542", "ClientUser", 570, 17, 0);
      //@formatter:on  
   }

   @Test
   public void testMultifield() throws Exception {
      TransactionQuery query = factory.transactionQuery();
      query.andAuthorIds(-1);
      query.andIs(Baselined);
      query.andCommentPattern("Branch Creation for.*");
      ResultSet<TransactionReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      Iterator<TransactionReadable> it = results.iterator();
      //@formatter:off
      assertTx(it.next(), 4,  Baselined,    "2013-05-06 14:18:05.424", "Branch Creation for Common", 570, -1, 0);
      assertTx(it.next(), 15, Baselined,    "2013-05-06 14:18:07.235", "Branch Creation for SAW_Bld_1", 3, -1, 0);
      assertTx(it.next(), 17, Baselined,    "2013-05-06 14:18:07.516", "Branch Creation for CIS_Bld_1", 4, -1, 0);
      assertTx(it.next(), 23, Baselined,    "2013-05-06 14:18:11.659", "Branch Creation for SAW_Bld_2", 5, -1, 0);
      //@formatter:on
   }

   private static void assertTx(TransactionReadable actual, Integer localId, TransactionDetailsType type, String date, String comment, long branchUuid, int authorId, int commitId) {
      assertEquals(localId, actual.getLocalId());
      assertEquals(type, actual.getTxType());
      assertEquals(date, DATE_FORMATTER.format(actual.getDate()));
      assertEquals(comment, actual.getComment());

      assertEquals(authorId, actual.getAuthorId());
      assertEquals((Long) branchUuid, actual.getBranchId());
      assertEquals(commitId, actual.getCommit());
   }
}
