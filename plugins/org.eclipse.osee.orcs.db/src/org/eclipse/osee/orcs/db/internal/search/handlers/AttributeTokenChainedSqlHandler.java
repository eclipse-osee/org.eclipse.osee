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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.ds.OptionsUtil;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeKeywordsChained;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeKeywordsChained.AttributeConstraint;

/**
 * Builds a chain of CTEs where each subsequent att CTE narrows the art_id set by joining to the previous one. Gamma
 * CTEs (for tokenized searches) are emitted first, then att CTEs reference them. The final att CTE contains the
 * fully-narrowed art_id set. This replaces the pattern where multiple AttributeTokenSqlHandler instances each produce
 * independent CTEs that are all joined in the final artWith query. Instead, the chaining happens within the CTEs
 * themselves, resulting in a more efficient query plan.
 */
public class AttributeTokenChainedSqlHandler extends SqlHandler<CriteriaAttributeKeywordsChained> implements HasTagProcessor {

   private CriteriaAttributeKeywordsChained criteria;
   private String finalAttrAlias;
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
   public void setData(CriteriaAttributeKeywordsChained criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      if (OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         return;
      }

      List<AttributeConstraint> constraints = criteria.getConstraints();
      List<String> gammaAliases = new ArrayList<>();

      // First pass: emit gamma CTEs for any tokenized constraints
      for (int i = 0; i < constraints.size(); i++) {
         AttributeConstraint constraint = constraints.get(i);
         if (isTokenized(constraint)) {
            String gammaAlias = writer.startCommonTableExpression("gamma");
            writeGammaCte(writer, constraint);
            gammaAliases.add(gammaAlias);
         } else {
            gammaAliases.add(null);
         }
      }

      // Second pass: emit chained att CTEs
      String previousAttAlias = null;
      for (int i = 0; i < constraints.size(); i++) {
         AttributeConstraint constraint = constraints.get(i);
         String attAlias = writer.startCommonTableExpression("att");

         writeAttCte(writer, constraint, gammaAliases.get(i), previousAttAlias);

         previousAttAlias = attAlias;
         finalAttrAlias = attAlias;
      }
   }

   /**
    * Determines if a constraint requires tokenized (tag-based) search vs exact value match.
    */
   private boolean isTokenized(AttributeConstraint constraint) {
      List<QueryOption> opts = Arrays.asList(constraint.getOptions());
      if (opts.contains(QueryOption.CASE__MATCH) && opts.contains(QueryOption.TOKEN_DELIMITER__EXACT) && opts.contains(
         QueryOption.TOKEN_MATCH_ORDER__MATCH)) {
         return false;
      }
      if (Arrays.equals(constraint.getOptions(), QueryOption.EXACT_MATCH_OPTIONS)) {
         return false;
      }
      // If any value produces tags, it's tokenized
      for (String value : constraint.getValues()) {
         List<Long> tags = new ArrayList<>();
         tokenize(value, tags);
         if (!tags.isEmpty()) {
            return true;
         }
      }
      return false;
   }

   /**
    * Writes a gamma CTE that finds gamma_ids matching the tokenized search values.
    */
   private void writeGammaCte(AbstractSqlWriter writer, AttributeConstraint constraint) {
      Collection<String> values = constraint.getValues();
      int valueCount = values.size();
      int valueIdx = 0;

      for (String value : values) {
         List<Long> tags = new ArrayList<>();
         tokenize(value, tags);
         int tagsSize = tags.size();
         writer.write("  ( \n");

         if (tagsSize == 0) {
            writer.write("SELECT gamma_id FROM osee_attribute att");
            writer.write(" WHERE ");
            if (Strings.isValid(value)) {
               writer.writeEqualsParameter("value", value);
            } else {
               writer.write("value is null or value = ''");
            }
            writer.writeAnd();
            writer.writeEqualsParameter("attr_type_id", constraint.getAttributeType());
         } else {
            for (int tagIdx = 0; tagIdx < tagsSize; tagIdx++) {
               Long tag = tags.get(tagIdx);
               writer.write(" SELECT gamma_id FROM osee_search_tags_hash WHERE ");
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

   /**
    * Writes an att CTE. If previousAttAlias is non-null, joins to it to chain the narrowing. If gammaAlias is non-null,
    * joins to the gamma CTE for tokenized lookup. Otherwise does a direct value match.
    */
   private void writeAttCte(AbstractSqlWriter writer, AttributeConstraint constraint, String gammaAlias,
      String previousAttAlias) {
      boolean allowDeleted = OptionsUtil.areDeletedAttributesIncluded(
         writer.getOptions()) || OptionsUtil.areDeletedArtifactsIncluded(writer.getOptions());

      writer.write(" SELECT att.art_id FROM osee_attribute att, osee_txs txs");

      // Add gamma table if tokenized
      if (gammaAlias != null) {
         writer.write(", ");
         writer.write(gammaAlias);
      }

      // Add previous att CTE for chaining
      if (previousAttAlias != null) {
         writer.write(", ");
         writer.write(previousAttAlias);
      }

      writer.write("\n WHERE \n");

      // Chain to previous CTE
      if (previousAttAlias != null) {
         writer.write("   att.art_id = ");
         writer.write(previousAttAlias);
         writer.write(".art_id");
         writer.writeAnd();
      }

      // Join to gamma CTE if tokenized
      if (gammaAlias != null) {
         writer.write("   att.gamma_id = ");
         writer.write(gammaAlias);
         writer.write(".gamma_id");
         writer.writeAnd();
      }

      // Attribute type filter
      writer.writeEqualsParameter("att", "attr_type_id", constraint.getAttributeType());

      // Gamma join to txs
      writer.writeAnd();
      writer.writeEqualsAnd("att", "txs", "gamma_id");

      // Branch/tx filter
      writer.writeTxBranchFilter("txs", allowDeleted);

      // Direct value match (only for non-tokenized constraints)
      if (gammaAlias == null) {
         Collection<String> values = constraint.getValues();
         if (values.size() == 1) {
            writer.write(" and att.value = ?");
            writer.addParameter(values.iterator().next());
         } else {
            writer.write(" and att.value in (");
            boolean first = true;
            for (int i = 0; i < values.size(); i++) {
               if (first) {
                  first = false;
               } else {
                  writer.write(",");
               }
               writer.write("?");
            }
            writer.write(")");
            for (String val : values) {
               writer.addParameter(val);
            }
         }
      }
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (!OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         writer.addTable(finalAttrAlias);
         artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
         writer.getMainTableAlias(OseeDb.TXS_TABLE);
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (!OptionsUtil.getFollowSearchInProgress(writer.getOptions())) {
         writer.writeEquals(artAlias, finalAttrAlias, "art_id");
      } else {
         writer.write("1 = 1");
      }
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
