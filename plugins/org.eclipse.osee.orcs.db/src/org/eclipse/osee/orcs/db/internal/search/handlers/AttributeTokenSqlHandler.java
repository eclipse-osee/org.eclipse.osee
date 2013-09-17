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

import static org.eclipse.osee.orcs.db.internal.sql.SqlUtil.newSimpleWithClause;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.AliasEntry;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlUtil;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTokenSqlHandler extends SqlHandler<CriteriaAttributeKeywords> implements HasTagProcessor {

   private static final AliasEntry GAMMA_WITH = SqlUtil.newAlias("gamma", "gamma");
   private static final AliasEntry ATTRIBUTE_WITH = SqlUtil.newAlias("attribute", "att");

   private CriteriaAttributeKeywords criteria;

   private String artAlias;
   private String attrAlias;
   private String txsAlias;

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
   public void addWithTables(AbstractSqlWriter writer) throws OseeCoreException {
      String gammaAlias = writer.getNextAlias(GAMMA_WITH);
      StringBuilder gammaSb = new StringBuilder();

      Collection<String> values = criteria.getValues();
      int valueCount = values.size();
      int valueIdx = 0;
      for (String value : values) {
         List<Long> tags = new ArrayList<Long>();
         tokenize(value, tags);
         int tagsSize = tags.size();
         gammaSb.append("(");
         for (int tagIdx = 0; tagIdx < tagsSize; tagIdx++) {
            Long tag = tags.get(tagIdx);
            gammaSb.append("SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?");
            writer.addParameter(tag);
            if (tagIdx + 1 < tagsSize) {
               gammaSb.append("\n INTERSECT \n");
            }
         }
         gammaSb.append(") ");
         if (valueIdx + 1 < valueCount) {
            gammaSb.append("\n UNION ALL \n");
         }
         valueIdx++;
      }
      writer.addWithClause(newSimpleWithClause(gammaAlias, gammaSb.toString()));

      attrAlias = writer.getNextAlias(ATTRIBUTE_WITH);
      Collection<? extends IAttributeType> types = criteria.getTypes();
      StringBuilder attrSb = new StringBuilder();
      attrSb.append("SELECT art_id FROM osee_attribute att, osee_txs txs, ");
      String jIdAlias = null;
      if (!criteria.isIncludeAllTypes() && types.size() > 1) {
         jIdAlias = writer.getNextAlias(TableEnum.ID_JOIN_TABLE);
         attrSb.append("osee_join_id ");
         attrSb.append(jIdAlias);
         attrSb.append(", ");
      }
      attrSb.append(gammaAlias);
      attrSb.append(" WHERE att.gamma_id = ");
      attrSb.append(gammaAlias);
      attrSb.append(".gamma_id AND att.gamma_id = txs.gamma_id AND ");
      attrSb.append(writer.getTxBranchFilter("txs"));
      if (!criteria.isIncludeAllTypes()) {
         attrSb.append(" AND att.attr_type_id = ");
         if (types.size() == 1) {
            attrSb.append("?");
            int localId = toLocalId(criteria.getTypes().iterator().next());
            writer.addParameter(localId);
         } else {
            Set<Integer> typeIds = new HashSet<Integer>();
            for (IAttributeType type : types) {
               typeIds.add(toLocalId(type));
            }
            AbstractJoinQuery joinQuery = writer.writeIdJoin(typeIds);

            attrSb.append(jIdAlias);
            attrSb.append(".id AND ");
            attrSb.append(jIdAlias);
            attrSb.append(".query_id = ?");
            writer.addParameter(joinQuery.getQueryId());
         }
      }
      writer.addWithClause(newSimpleWithClause(attrAlias, attrSb.toString()));
      writer.addTable(attrAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      List<String> txs = writer.getAliases(TableEnum.TXS_TABLE);

      if (aliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      if (txs.isEmpty()) {
         txsAlias = writer.addTable(TableEnum.TXS_TABLE);
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      if (!Strings.isValid(artAlias)) {
         artAlias = writer.getAliases(TableEnum.ARTIFACT_TABLE).iterator().next();
      }
      if (!Strings.isValid(txsAlias)) {
         txsAlias = writer.getAliases(TableEnum.TXS_TABLE).iterator().next();
      }

      writer.write("%s.art_id = %s.art_id", artAlias, attrAlias);
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter(txsAlias));
      writer.writeAndLn();
      writer.write("%s.gamma_id = %s.gamma_id", txsAlias, artAlias);
      return true;
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
