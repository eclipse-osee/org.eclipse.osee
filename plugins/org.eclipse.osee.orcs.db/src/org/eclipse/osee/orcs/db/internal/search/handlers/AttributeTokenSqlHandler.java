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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
      Collection<AttributeTypeId> types = criteria.getTypes();
      AbstractJoinQuery joinQuery = null;
      if (!criteria.isIncludeAllTypes() && types.size() > 1) {
         Set<AttributeTypeId> typeIds = new HashSet<>();
         for (AttributeTypeId type : types) {
            typeIds.add(type);
         }
         joinQuery = writer.writeJoin(typeIds);
      }

      String gammaAlias = writer.startCommonTableExpression("gamma");
      writeGammaWith(writer, joinQuery);

      attrAlias = writer.startCommonTableExpression("att");
      writeAttrWith(writer, joinQuery, gammaAlias);
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
            writer.write("    SELECT gamma_id FROM osee_attribute att");
            if (joinQuery != null) {
               writer.write(", ");
               jIdAlias = writer.writeTable(TableEnum.ID_JOIN_TABLE);
            }
            writer.write(" WHERE ");
            if (Strings.isValid(value)) {
               writer.writeEqualsParameter("value", value);
            } else {
               writer.write("value is null or value = ''");
            }

            if (!criteria.isIncludeAllTypes()) {
               writer.write(" AND ");
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
               writer.write("    SELECT gamma_id FROM osee_search_tags WHERE ");
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
      writer.write("   SELECT DISTINCT art_id FROM osee_attribute att, osee_txs txs, ");

      String jIdAlias = null;
      if (joinQuery != null) {
         jIdAlias = writer.writeTable(TableEnum.ID_JOIN_TABLE);
         writer.write(", ");
      }

      writer.write(gammaAlias);
      writer.write("\n WHERE \n");
      writer.write("   att.gamma_id = ");
      writer.write(gammaAlias);
      writer.write(".gamma_id");
      if (!criteria.isIncludeAllTypes()) {
         writer.write(" AND ");
         if (joinQuery == null) {
            writer.writeEqualsParameter("attr_type_id", criteria.getTypes().iterator().next());
         } else {
            writer.writeEqualsAnd("att", "attr_type_id", jIdAlias, "id");
            writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
         }
      }
      writer.write("\n AND \n");
      writer.write("   att.gamma_id = txs.gamma_id");
      writer.write(" AND ");
      writer.writeTxBranchFilter("txs", true);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(attrAlias);
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      writer.getMainTableAlias(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(artAlias, attrAlias, "art_id");
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