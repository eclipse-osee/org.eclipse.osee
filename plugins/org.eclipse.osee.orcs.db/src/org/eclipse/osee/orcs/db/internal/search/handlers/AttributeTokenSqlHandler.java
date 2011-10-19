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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.TableEnum;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlWriter;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTokenSqlHandler extends SqlHandler {

   private CriteriaAttributeKeyword criteria;

   private String attrAlias;
   private String txsAlias1;
   private String jIdAlias;
   private List<Long> codedTags;
   private List<String> tagAliases;

   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(Criteria criteria) {
      this.criteria = (CriteriaAttributeKeyword) criteria;
   }

   @Override
   public void addTables(SqlWriter writer) throws OseeCoreException {
      StringOperator operator = criteria.getStringOp();
      if (requiresTokenizing(operator)) {
         codedTags = new ArrayList<Long>();
         tokenize(criteria.getValues(), codedTags);

         tagAliases = new ArrayList<String>();
         for (int index = 0; index < codedTags.size(); index++) {
            tagAliases.add(writer.writeTable(TableEnum.SEARCH_TAGS_TABLE));
         }
      }

      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.writeTable(TableEnum.ID_JOIN_TABLE);
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      List<String> txs = writer.getAliases(TableEnum.TXS_TABLE);

      attrAlias = writer.writeTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias1 = writer.writeTable(TableEnum.TXS_TABLE);

      if (aliases.isEmpty()) {
         writer.writeTable(TableEnum.ARTIFACT_TABLE);
      }
      if (txs.isEmpty()) {
         writer.writeTable(TableEnum.TXS_TABLE);
      }
   }

   @Override
   public void addPredicates(SqlWriter writer) throws OseeCoreException {
      Collection<? extends IAttributeType> types = criteria.getTypes();
      if (types.size() > 1) {
         Set<Integer> typeIds = new HashSet<Integer>();
         for (IAttributeType type : types) {
            typeIds.add(toLocalId(type));
         }
         joinQuery = writer.writeIdJoin(typeIds);
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());

         List<String> aliases = writer.getAliases(TableEnum.ATTRIBUTE_TABLE);
         if (!aliases.isEmpty()) {
            writer.write(" AND ");
            int aSize = aliases.size();
            for (int index = 0; index < aSize; index++) {
               String alias = aliases.get(index);

               writer.write(alias);
               writer.write(".attr_type_id = ");
               writer.write(jIdAlias);
               writer.write(".id");
               if (index + 1 < aSize) {
                  writer.write(" AND ");
               }
            }
         }
      } else {
         IAttributeType type = types.iterator().next();
         int localId = toLocalId(type);
         List<String> aliases = writer.getAliases(TableEnum.ATTRIBUTE_TABLE);
         if (!aliases.isEmpty()) {
            int aSize = aliases.size();
            for (int index = 0; index < aSize; index++) {
               String alias = aliases.get(index);

               writer.write(alias);
               writer.write(".attr_type_id = ?");
               writer.addParameter(localId);

               if (index + 1 < aSize) {
                  writer.write(" AND ");
               }
            }
         }
      }

      StringOperator operator = criteria.getStringOp();
      if (requiresTokenizing(operator)) {

         int size = tagAliases.size();

         for (int index = 0; index < size; index++) {
            String tagAlias = tagAliases.get(index);
            Long tag = codedTags.get(index);

            writer.write(tagAlias);
            writer.write(".coded_tag_id = ?");
            writer.addParameter(tag);

            if (index + 1 < size) {
               writer.write(" AND ");
            }
         }

         for (int index = 1; index < size; index++) {
            String tagAlias1 = tagAliases.get(index - 1);
            String tagAlias2 = tagAliases.get(index);

            writer.write(" AND ");
            writer.write(tagAlias1);
            writer.write(".gamma_id = ? AND");
            writer.write(tagAlias2);
            writer.write(".gamma_id = ?");
         }
         String lastAlias = tagAliases.get(size - 1);
         writer.write(" AND ");
         writer.write(lastAlias);
         writer.write(".gamma_id = ");
         writer.write(attrAlias);
         writer.write(".gamma_id");
      } else {
         // case CONTAINS:
         // case NOT_EQUALS:
         // case EQUALS:
         //
         //
         //
         //         CaseType caseType = criteria.getMatch();
         //         caseType.isCaseSensitive();
         throw new UnsupportedOperationException();
      }
      writer.write(" AND ");
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias1);
      writer.write(".gamma_id AND ");
      writer.writeTxBranchFilter(txsAlias1);
   }

   @Override
   public int getPriority() {
      return CriteriaPriority.ATTRIBUTE_TOKEN.ordinal();
   }

   private void tokenize(Collection<String> values, final Collection<Long> codedTags) {
      TagCollector collector = new TagCollector() {
         @Override
         public void addTag(String word, Long codedTag) {
            codedTags.add(codedTag);
         }
      };

      TagProcessor processor = getTagProcessor();
      for (String value : values) {
         processor.collectFromString(value, collector);
      }
   }

   private boolean requiresTokenizing(StringOperator op) {
      return StringOperator.TOKENIZED_ANY_ORDER == op || StringOperator.TOKENIZED_MATCH_ORDER == op;
   }
}
