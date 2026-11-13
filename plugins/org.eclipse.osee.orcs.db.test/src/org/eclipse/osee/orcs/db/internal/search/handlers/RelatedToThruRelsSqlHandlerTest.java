/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.QueryData;
import org.eclipse.osee.orcs.search.ds.Options;
import org.eclipse.osee.orcs.search.ds.OptionsUtil;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelatedToThroughRels;
import org.junit.Assert;
import org.junit.Test;

public class RelatedToThruRelsSqlHandlerTest {

   @Test
   public void testTwoStepPathJoinsMatchingEndpoints() {
      RelationTypeSide testScriptDefinition =
         CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef;
      RelationTypeSide testScriptResults =
         CoreRelationTypes.TestScriptSetToTestScriptResults_TestScriptResults;
      ArtifactId testScriptSet = ArtifactId.valueOf(9932990L);

      TestSqlWriter writer = writePredicates(testScriptSet, testScriptDefinition, testScriptResults);

      String sql = writer.toString();
      Assert.assertTrue(sql.contains("relExists0.a_art_id = art1.art_id"));
      Assert.assertTrue(sql.contains("relExists1.b_art_id = relExists0.b_art_id"));
      Assert.assertTrue(sql.contains("relExists1.a_art_id = ?"));
      Assert.assertEquals(
         Arrays.asList(testScriptDefinition.getGuid(), testScriptResults.getGuid(), testScriptSet),
         writer.getParameters());
   }

   @Test
   public void testThreeStepPathRetainsRepeatedRelationTypeSide() {
      RelationTypeSide testScriptDefinition =
         CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef;
      RelationTypeSide testScriptResults =
         CoreRelationTypes.TestScriptSetToTestScriptResults_TestScriptResults;
      ArtifactId scriptLog = ArtifactId.valueOf(9932991L);

      TestSqlWriter writer =
         writePredicates(scriptLog, testScriptDefinition, testScriptResults, testScriptDefinition);

      String sql = writer.toString();
      Assert.assertTrue(sql.contains("osee_relation relExists0"));
      Assert.assertTrue(sql.contains("osee_relation relExists1"));
      Assert.assertTrue(sql.contains("osee_relation relExists2"));
      Assert.assertTrue(sql.contains("relExists1.b_art_id = relExists0.b_art_id"));
      Assert.assertTrue(sql.contains("relExists2.a_art_id = relExists1.a_art_id"));
      Assert.assertTrue(sql.contains("relExists2.b_art_id = ?"));
      Assert.assertEquals(Arrays.asList(testScriptDefinition.getGuid(), testScriptResults.getGuid(),
         testScriptDefinition.getGuid(), scriptLog), writer.getParameters());
   }

   @Test(expected = OseeArgumentException.class)
   public void testEmptyRelationPathIsRejected() {
      RelatedToThruRelsSqlHandler handler = new RelatedToThruRelsSqlHandler();

      handler.setData(new CriteriaRelatedToThroughRels(new LinkedList<>(), ArtifactId.valueOf(9932990L)));
   }

   private TestSqlWriter writePredicates(ArtifactId relatedArtifact, RelationTypeSide... relationTypeSides) {
      RelatedToThruRelsSqlHandler handler = new RelatedToThruRelsSqlHandler();
      handler.setData(new CriteriaRelatedToThroughRels(new LinkedList<>(Arrays.asList(relationTypeSides)),
         relatedArtifact));
      TestSqlWriter writer = new TestSqlWriter();
      handler.addPredicates(writer);
      return writer;
   }

   private static final class TestSqlWriter extends AbstractSqlWriter {
      private final List<Object> parameters = new ArrayList<>();
      private final Options options = OptionsUtil.createOptions();

      private TestSqlWriter() {
         super(null, null, QueryData.mock());
      }

      @Override
      public Options getOptions() {
         return options;
      }

      @Override
      public String getMainTableAlias(SqlTable table) {
         return "art1";
      }

      @Override
      public void writeTxBranchFilter(String txsAlias) {
         write(txsAlias + ".visible = 1");
      }

      @Override
      public void addParameter(Object parameter) {
         parameters.add(parameter);
      }

      @Override
      protected void writeSelectFields() {
         // do nothing
      }

      @Override
      protected void writeOrderBy(Iterable<SqlHandler<?>> handlers) {
         // do nothing
      }

      @Override
      protected void writeGroupBy(Iterable<SqlHandler<?>> handlers) {
         // do nothing
      }

      private List<Object> getParameters() {
         return parameters;
      }
   }
}
