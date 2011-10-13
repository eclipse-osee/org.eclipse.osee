/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.mocks.SqlUtility;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link SqlWriter}
 * 
 * @author Roberto E. Escobar
 */
public class SqlWriterTest {

   private static final Criteria GUIDS = new CriteriaArtifactGuids(Arrays.asList(GUID.create(), GUID.create()));
   private static final Criteria IDS = new CriteriaArtifactIds(Arrays.asList(1, 2, 3, 4, 5));
   private static final Criteria HRIDS = new CriteriaArtifactHrids(Arrays.asList("ABCDE", "FGHIJ"));
   private static final Criteria TYPES = new CriteriaArtifactType(Arrays.asList(CoreArtifactTypes.CodeUnit));

   private static final Criteria ATTR_TYPE_EXITS = new CriteriaAttributeTypeExists(
      Arrays.asList(CoreAttributeTypes.Name));
   private static final Criteria REL_TYPE_EXISTS = new CriteriaRelationTypeExists(
      CoreRelationTypes.Default_Hierarchical__Child);

   //   private static final Criteria TWO_TYPES = new CriteriaArtifactType(Arrays.asList(CoreArtifactTypes.CodeUnit,
   //      CoreArtifactTypes.Artifact));
   //   private static final Criteria ATTRIBUTE_EXITS = new CriteriaAttributeExists(CoreAttributeTypes.Name);
   //   private static final Criteria RELATION_EXISTS = new CriteriaRelationExists(
   //      CoreRelationTypes.Default_Hierarchical__Child);

   @Test
   public void testBuildWithBranch() throws OseeCoreException {
      int branchId = 4;
      IOseeDatabaseService service = null;
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      StringBuilder output = new StringBuilder();
      SqlWriter writer = new SqlWriter(service, branchId, context, output);

      List<SqlHandler> handlers = SqlUtility.createHandlers(createCriteria());

      writer.writeTables(handlers);
      Assert.assertEquals(//
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_join_char_id jch2, osee_artifact art3, osee_txs txs3, " + //
         "osee_attribute att1, osee_txs txs4, " + //
         "osee_relation_link rel1, osee_txs txs5", //
         output.toString());

      output.delete(0, output.length());

      writer.writePredicates(handlers);

      Assert.assertEquals(
      //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id AND att1.art_id = art3.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs4.gamma_id AND txs4.tx_current = 1 AND txs4.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + // 
         "(rel1.a_art_id = art3.art_id OR rel1.b_art_id = art3.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs5.gamma_id AND txs5.tx_current = 1 AND txs5.branch_id = ?\n" + //
         " AND \n" + //
         "txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id = txs2.transaction_id AND txs1.branch_id = txs2.branch_id\n" + //
         " AND \n" + //
         "txs2.gamma_id = txs3.gamma_id AND txs2.transaction_id = txs3.transaction_id AND txs2.branch_id = txs3.branch_id\n" + //
         " AND \n" + //
         "txs3.gamma_id = txs4.gamma_id AND txs3.transaction_id = txs4.transaction_id AND txs3.branch_id = txs4.branch_id\n" + //
         " AND \n" + //
         "txs4.gamma_id = txs5.gamma_id AND txs4.transaction_id = txs5.transaction_id AND txs4.branch_id = txs5.branch_id" //
         , output.toString());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(13, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(branchId, parameters.get(1));

      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(branchId, parameters.get(3));

      Assert.assertEquals(joins.get(2).getQueryId(), parameters.get(4));
      Assert.assertEquals(branchId, parameters.get(5));

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(6));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(7));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(8));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), parameters.get(9));
      Assert.assertEquals(branchId, parameters.get(10));

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue(), parameters.get(11));
      Assert.assertEquals(branchId, parameters.get(12));
   }

   @Test
   public void testBuildNoBranch() throws OseeCoreException {
      IOseeDatabaseService service = null;
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      StringBuilder output = new StringBuilder();
      SqlWriter writer = new SqlWriter(service, -1, context, output);

      List<SqlHandler> handlers = SqlUtility.createHandlers(createCriteria());

      writer.writeTables(handlers);
      Assert.assertEquals(//
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_join_char_id jch2, osee_artifact art3, osee_txs txs3, " + //
         "osee_attribute att1, osee_txs txs4, " + //
         "osee_relation_link rel1, osee_txs txs5", //
         output.toString());

      output.delete(0, output.length());

      writer.writePredicates(handlers);

      Assert.assertEquals(
      //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id AND att1.art_id = art3.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs4.gamma_id AND txs4.tx_current = 1\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + // 
         "(rel1.a_art_id = art3.art_id OR rel1.b_art_id = art3.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs5.gamma_id AND txs5.tx_current = 1\n" + //
         " AND \n" + //
         "txs1.gamma_id = txs2.gamma_id AND txs1.transaction_id = txs2.transaction_id AND txs1.branch_id = txs2.branch_id\n" + //
         " AND \n" + //
         "txs2.gamma_id = txs3.gamma_id AND txs2.transaction_id = txs3.transaction_id AND txs2.branch_id = txs3.branch_id\n" + //
         " AND \n" + //
         "txs3.gamma_id = txs4.gamma_id AND txs3.transaction_id = txs4.transaction_id AND txs3.branch_id = txs4.branch_id\n" + //
         " AND \n" + //
         "txs4.gamma_id = txs5.gamma_id AND txs4.transaction_id = txs5.transaction_id AND txs4.branch_id = txs5.branch_id" //
         , output.toString());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(8, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(1));
      Assert.assertEquals(joins.get(2).getQueryId(), parameters.get(2));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(3));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(5));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), parameters.get(6));
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue(), parameters.get(7));
   }

   private CriteriaSet createCriteria() {
      return SqlUtility.createCriteria(CoreBranches.COMMON, GUIDS, TYPES, REL_TYPE_EXISTS, IDS, ATTR_TYPE_EXITS, HRIDS);
   }
}
