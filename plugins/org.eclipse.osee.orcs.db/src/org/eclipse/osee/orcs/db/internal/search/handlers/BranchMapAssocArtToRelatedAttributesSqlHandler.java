/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMapAssocArtToRelatedAttributes;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class BranchMapAssocArtToRelatedAttributesSqlHandler extends SqlHandler<CriteriaMapAssocArtToRelatedAttributes> {

   private CriteriaMapAssocArtToRelatedAttributes criteria;
   private String branchAlias;
   private String relatedBranchAlias;
   private final List<String> artifactAlias = new LinkedList<String>();
   private final List<String> artifactTxAlias = new LinkedList<String>();
   private final List<String> attributeAlias = new LinkedList<String>();
   private final List<String> attributeTxAlias = new LinkedList<String>();

   @Override
   public void setData(CriteriaMapAssocArtToRelatedAttributes criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write(relatedBranchAlias + ".branch_id=" + criteria.getRelatedBranch().getId());
      writer.writeAnd();
      //first and last are special cases
      Optional<String> firstArtifactAlias = artifactAlias.stream().findFirst();
      Optional<String> firstArtifactTxAlias = artifactTxAlias.stream().findFirst();
      Optional<String> firstAttrAlias = attributeAlias.stream().findFirst();
      Optional<String> firstAttrTxAlias = attributeTxAlias.stream().findFirst();
      if (firstArtifactTxAlias.isPresent() && firstArtifactAlias.isPresent() && firstAttrAlias.isPresent() && firstAttrTxAlias.isPresent()) {

         writer.writeEqualsAnd(firstArtifactTxAlias.get(), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(firstArtifactTxAlias.get() + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(firstArtifactTxAlias.get(), "gamma_id", firstArtifactAlias.get(), "gamma_id");
         writer.write(
            firstArtifactAlias.get() + ".art_type_id=" + criteria.getArtAttrPairs().get(0).getFirst().getIdString());
         writer.writeAnd();
         writer.writeEqualsAnd(firstAttrTxAlias.get(), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(firstAttrTxAlias.get() + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(firstAttrTxAlias.get(), "gamma_id", firstAttrAlias.get(), "gamma_id");
         writer.writeEqualsAnd(firstAttrAlias.get(), "art_id", firstArtifactAlias.get(), "art_id");
         writer.write(
            firstAttrAlias.get() + ".attr_type_id=" + criteria.getArtAttrPairs().get(0).getSecond().getIdString());
         writer.writeAnd();
         writer.write(firstAttrAlias.get() + ".value='" + criteria.getRelatedValue() + "'");
         writer.writeAnd();
      }
      String lookupVal = firstAttrAlias.get() + ".value";
      for (int i = 1; i < artifactAlias.size() - 1; i++) {
         writer.writeEqualsAnd(artifactTxAlias.get(i), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(artifactTxAlias.get(i) + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(artifactTxAlias.get(i), "gamma_id", artifactAlias.get(i), "gamma_id");
         writer.write(
            artifactAlias.get(i) + ".art_type_id=" + criteria.getArtAttrPairs().get(i).getFirst().getIdString());
         writer.writeAnd();
         writer.writeEqualsAnd(attributeTxAlias.get(i), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(attributeTxAlias.get(i) + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(attributeTxAlias.get(i), "gamma_id", attributeAlias.get(i), "gamma_id");
         writer.writeEquals(attributeAlias.get(i), "art_id", artifactAlias.get(i), "art_id");
         writer.write(
            attributeAlias.get(i) + ".attr_type_id=" + criteria.getArtAttrPairs().get(i).getSecond().getIdString());
         writer.writeAnd();
         writer.write(attributeAlias.get(i) + lookupVal);
         lookupVal = attributeAlias.get(i) + ".value";
      }

      Optional<String> lastArtifactAlias = artifactAlias.stream().reduce((previous, current) -> current);
      Optional<String> lastArtifactTxAlias = artifactTxAlias.stream().reduce((previous, current) -> current);
      Optional<String> lastAttrAlias = attributeAlias.stream().reduce((previous, current) -> current);
      Optional<String> lastAttrTxAlias = attributeTxAlias.stream().reduce((previous, current) -> current);

      if (lastArtifactTxAlias.isPresent() && lastArtifactAlias.isPresent() && lastAttrAlias.isPresent() && lastAttrTxAlias.isPresent()) {
         writer.writeAnd();
         writer.writeEqualsAnd(lastArtifactTxAlias.get(), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(lastArtifactTxAlias.get() + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(lastArtifactTxAlias.get(), "gamma_id", lastArtifactAlias.get(), "gamma_id");
         writer.write(lastArtifactAlias.get() + ".art_type_id=" + criteria.getArtAttrPairs().get(
            criteria.getArtAttrPairs().size() - 1).getFirst().getIdString());
         writer.writeAnd();
         writer.writeEqualsAnd(lastAttrTxAlias.get(), "branch_id", relatedBranchAlias, "branch_id");
         writer.write(lastAttrTxAlias.get() + ".tx_current=1");
         writer.writeAnd();
         writer.writeEqualsAnd(lastAttrTxAlias.get(), "gamma_id", lastAttrAlias.get(), "gamma_id");
         writer.writeEqualsAnd(lastAttrAlias.get(), "art_id", lastArtifactAlias.get(), "art_id");
         writer.write(lastAttrAlias.get() + ".attr_type_id=" + criteria.getArtAttrPairs().get(
            criteria.getArtAttrPairs().size() - 1).getSecond().getIdString());
         writer.writeAnd();
         //link the previous art id to the current attribute value
         writer.write(lastAttrAlias.get() + ".value= " + artifactAlias.get(
            artifactAlias.size() - 2) + ".art_id" + writer.getJdbcClient().getDbType().getStringConversion());
         writer.writeAnd();
         //this links the attribute search to the branch search
         writer.writeEquals(branchAlias, "associated_art_id", lastArtifactAlias.get(), "art_id");
      }

   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      branchAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      relatedBranchAlias = writer.addTable(OseeDb.BRANCH_TABLE);
      for (int i = 0; i < criteria.getArtAttrPairs().size(); i++) {
         artifactAlias.add(writer.addTable(OseeDb.ARTIFACT_TABLE));
         attributeAlias.add(writer.addTable(OseeDb.ATTRIBUTE_TABLE));
         artifactTxAlias.add(writer.addTable(OseeDb.TXS_TABLE, ObjectType.ARTIFACT));
         attributeTxAlias.add(writer.addTable(OseeDb.TXS_TABLE, ObjectType.ATTRIBUTE));
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_MAP_ASSOC_ART_TO_REL_ATTR.ordinal();
   }

}
