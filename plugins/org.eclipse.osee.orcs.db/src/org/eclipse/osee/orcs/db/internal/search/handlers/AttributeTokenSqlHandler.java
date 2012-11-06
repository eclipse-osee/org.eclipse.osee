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
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.HasDataPostProcessorFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTokenSqlHandler extends SqlHandler<CriteriaAttributeKeyword, QueryOptions> implements HasTagProcessor, HasDataPostProcessorFactory<CriteriaAttributeKeyword> {

   private CriteriaAttributeKeyword criteria;

   private String attrAlias;
   private String txsAlias1;
   private String jIdAlias;
   private List<Long> codedTags;
   private List<String> tagAliases;

   private String artAlias2;
   private String txs2Alias2;

   private AbstractJoinQuery joinQuery;

   private Collection<? extends IAttributeType> types;

   private DataPostProcessorFactory<CriteriaAttributeKeyword> factory;
   private TagProcessor tagProcessor;

   @Override
   public void setDataPostProcessorFactory(DataPostProcessorFactory<CriteriaAttributeKeyword> factory) {
      this.factory = factory;
   }

   @Override
   public DataPostProcessorFactory<CriteriaAttributeKeyword> getDataPostProcessorFactory() {
      return factory;
   }

   @Override
   public void setTagProcessor(TagProcessor tagProcessor) {
      this.tagProcessor = tagProcessor;
   }

   @Override
   public TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   @Override
   public void setData(CriteriaAttributeKeyword criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter<QueryOptions> writer) throws OseeCoreException {
      types = criteria.getTypes();

      if (criteria.getStringOp().isTokenized()) {
         codedTags = new ArrayList<Long>();
         tokenize(criteria.getValue(), codedTags);

         tagAliases = new ArrayList<String>();
         for (int index = 0; index < codedTags.size(); index++) {
            tagAliases.add(writer.addTable(TableEnum.SEARCH_TAGS_TABLE));
         }
      }
      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      if (types.size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      List<String> txs = writer.getAliases(TableEnum.TXS_TABLE);

      txsAlias1 = writer.addTable(TableEnum.TXS_TABLE);

      if (aliases.isEmpty()) {
         artAlias2 = writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      if (txs.isEmpty()) {
         txs2Alias2 = writer.addTable(TableEnum.TXS_TABLE);
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter<QueryOptions> writer) throws OseeCoreException {
      if (types.size() > 1) {
         Set<Integer> typeIds = new HashSet<Integer>();
         for (IAttributeType type : types) {
            typeIds.add(toLocalId(type));
         }
         joinQuery = writer.writeIdJoin(typeIds);
         writer.write(attrAlias);
         writer.write(".attr_type_id = ");
         writer.write(jIdAlias);
         writer.write(".id");
         writer.write(" AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         IAttributeType type = types.iterator().next();
         int localId = toLocalId(type);
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(localId);
      }

      DataPostProcessor<?> processor = getDataPostProcessorFactory().createPostProcessor(criteria, writer.getOptions());
      writer.addPostProcessor(processor);

      if (criteria.getStringOp().isTokenized()) {
         writer.writeAndLn();

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
         if (size > 1) {
            writer.writeAndLn();
            for (int index = 1; index < size; index++) {
               String tagAlias1 = tagAliases.get(index - 1);
               String tagAlias2 = tagAliases.get(index);

               writer.write(tagAlias1);
               writer.write(".gamma_id = ");
               writer.write(tagAlias2);
               writer.write(".gamma_id");
               if (index + 1 < size) {
                  writer.write(" AND ");
               }
            }
         }
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

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);
            writer.write(artAlias);
            writer.write(".art_id = ");
            writer.write(attrAlias);
            writer.write(".art_id");
            if (index + 1 < aSize) {
               writer.write(" AND ");
            }
         }
      }

      writer.writeAndLn();
      writer.write(tagAliases.get(0));
      writer.write(".gamma_id = ");
      writer.write(attrAlias);
      writer.write(".gamma_id AND ");
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias1);
      writer.write(".gamma_id AND ");
      writer.writeTxBranchFilter(txsAlias1);

      if (txs2Alias2 != null && artAlias2 != null) {
         writer.writeAndLn();
         writer.write(artAlias2);
         writer.write(".gamma_id = ");
         writer.write(txs2Alias2);
         writer.write(".gamma_id AND ");
         writer.writeTxBranchFilter(txs2Alias2);
      }
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
