/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTokenSqlHandler extends SqlHandler<CriteriaAttributeKeywords> implements HasTagProcessor {
   private CriteriaAttributeKeywords criteria;
   private String attrAlias;
   private String artAlias;

   private TagProcessor tagProcessor;

   @Override
   public void setTagProcessor(TagProcessor tagProcessor) {
      this.tagProcessor = tagProcessor;
   }

   @Override
   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   @Override
   public void setData(CriteriaAttributeKeywords criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      if (!OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         Collection<AttributeTypeId> types = criteria.getTypes();
         AbstractJoinQuery joinQuery = null;
         if (!criteria.isIncludeAllTypes() && types.size() > 1) {
            Set<AttributeTypeId> typeIds = new HashSet<>();
            for (AttributeTypeId type : types) {
               typeIds.add(type);
            }
            joinQuery = writer.writeJoin(typeIds);
         }
         List<QueryOption> asList = Arrays.asList(criteria.getOptions());
         if ((asList.contains(QueryOption.CASE__MATCH) && asList.contains(QueryOption.TOKEN_DELIMITER__EXACT)
            && asList.contains(QueryOption.TOKEN_MATCH_ORDER__MATCH))
            || Arrays.equals(criteria.getOptions(), QueryOption.EXACT_MATCH_OPTIONS)) {
            attrAlias = writer.startCommonTableExpression("att");
            writeAttrWithNoGamma(writer, joinQuery);
         } else {
            String gammaAlias = writer.startCommonTableExpression("gamma");
            writeGammaWith(writer, joinQuery);

            attrAlias = writer.startCommonTableExpression("att");
            writeAttrWith(writer, joinQuery, gammaAlias);
         }
      }
   }

   private void writeGammaWith(AbstractSqlWriter writer, AbstractJoinQuery joinQuery) {
      Collection<String> values = criteria.getValues();
      int valueCount = values.size();
      int valueIdx = 0;
      String jIdAlias = null;
      for (String value : values) {
         List<Long> tags = new ArrayList<>();
         tokenize(value, tags);
         int tagsSize = tags.size();
         writer.write("  ( \n");
         if (tagsSize == 0) {
            writer.write("SELECT gamma_id FROM osee_attribute att");
            if (joinQuery != null) {
               writer.write(", ");
               jIdAlias = writer.writeTable(OseeDb.OSEE_JOIN_ID_TABLE);
            }
            writer.write(" WHERE ");
            if (Strings.isValid(value)) {
               writer.writeEqualsParameter("value", value);
            } else {
               writer.write("value is null or value = ''");
            }

            if (!criteria.isIncludeAllTypes()) {
               writer.writeAnd();
               if (joinQuery == null) {
                  writer.writeEqualsParameter("attr_type_id", criteria.getTypes().iterator().next());
               } else {
                  writer.writeEqualsAnd("att", "attr_type_id", jIdAlias, "id");
                  writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
               }
            }
         } else {
            for (int tagIdx = 0; tagIdx < tagsSize; tagIdx++) {
               Long tag = tags.get(tagIdx);
               writer.write(" SELECT gamma_id FROM osee_search_tags WHERE ");
               writer.writeEqualsParameter("coded_tag_id", tag);
               if (tagIdx + 1 < tagsSize) {
                  writer.write("\n INTERSECT \n");
               }
            }
         }
         writer.write("\n  ) ");
         if (valueIdx + 1 < valueCount) {
            writer.write("\n UNION ALL \n");
         }
         valueIdx++;
      }
   }

   private void writeAttrWith(AbstractSqlWriter writer, AbstractJoinQuery joinQuery, String gammaAlias) {
      writer.write(" SELECT DISTINCT art_id FROM osee_attribute att, osee_txs txs, ");

      String jIdAlias = null;
      if (joinQuery != null) {
         jIdAlias = writer.writeTable(OseeDb.OSEE_JOIN_ID_TABLE);
         writer.write(", ");
      }

      writer.write(gammaAlias);
      writer.write("\n WHERE \n");
      writer.write("   att.gamma_id = ");
      writer.write(gammaAlias);
      writer.write(".gamma_id");
      if (!criteria.isIncludeAllTypes()) {
         writer.writeAnd();
         if (joinQuery == null) {
            writer.writeEqualsParameter("attr_type_id", criteria.getTypes().iterator().next());
         } else {
            writer.writeEqualsAnd("att", "attr_type_id", jIdAlias, "id");
            writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
         }
      }
      writer.writeAnd();
      writer.writeEqualsAnd("att", "txs", "gamma_id");
      writer.writeTxBranchFilter("txs", true);
   }

   private void writeAttrWithNoGamma(AbstractSqlWriter writer, AbstractJoinQuery joinQuery) {
      writer.write(" SELECT DISTINCT art_id FROM osee_attribute att, osee_txs txs ");

      String jIdAlias = null;
      if (joinQuery != null) {
         writer.writeCommaIfNotFirst();
         jIdAlias = writer.writeTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      writer.write("\n WHERE \n");

      if (!criteria.isIncludeAllTypes()) {

         if (joinQuery == null) {
            writer.writeEqualsParameterAnd("attr_type_id", criteria.getTypes().iterator().next());
         } else {
            writer.writeEqualsAnd("att", "attr_type_id", jIdAlias, "id");
            writer.writeEqualsParameterAnd(jIdAlias, "query_id", joinQuery.getQueryId());
         }
      }
      writer.writeEqualsAnd("att", "txs", "gamma_id");
      writer.writeTxBranchFilter("txs", true);
      if (criteria.getValues().size() == 1) {
         writer.write(" and att.value = '" + criteria.getValues().iterator().next() + "' ");
      }
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (!OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         writer.addTable(attrAlias);
         artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
         writer.getMainTableAlias(OseeDb.TXS_TABLE);
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (!OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         writer.writeEquals(artAlias, attrAlias, "art_id");
      } else {
         writer.write("1 = 1");
      }
      ;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TOKENIZED_VALUE.ordinal();
   }

   private void tokenize(String value, final Collection<Long> codedTags) {
      TagCollector collector = new TagCollector() {
         @Override
         public void addTag(String word, Long codedTag) {
            codedTags.add(codedTag);
         }
      };
      getTagProcessor().collectFromString(value, collector);
   }
}