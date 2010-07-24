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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;

/**
 * @author Robert A. Fisher
 */
public class InRelationSearch implements ISearchPrimitive {
   private static final String relationTables =
      "osee_relation_link_type rel_type_1,osee_relation_link rel_1, osee_txs txs1";
   private final static String TOKEN = ";";
   private final String[] typeNames;
   private final boolean sideA;
   private final FromArtifactsSearch otherArtifactsCriteria;

   /**
    * @param typeName The type of relation for the artifact to be in.
    * @param sideA The side of the relation the artifact should be on.
    */
   public InRelationSearch(String typeName, boolean sideA) {
      this(typeName, sideA, null);
   }

   /**
    * Search for an artifact on at least one of several different types of relations. All of the
    * <code>RelationSide</code>'s must be for the same side, that is all sideB or all sideA. This restriction is in
    * place to optimize SQL performance.
    * 
    * @param firstSide
    * @param sides
    * @throws IllegalArgumentException if the sides are a mixture of sideA and sideB relation sides.
    */
   public InRelationSearch(IRelationEnumeration firstSide, IRelationEnumeration... sides) {
      this(null, firstSide, sides);
   }

   /**
    * Search for an artifact on at least one of several different types of relations. All of the
    * <code>RelationSide</code>'s must be for the same side, that is all sideB or all sideA. This restriction is in
    * place to optimize SQL performance.
    * 
    * @param otherArtifacts
    * @param firstSide
    * @param sides
    * @throws IllegalArgumentException if the sides are a mixture of sideA and sideB relation sides.
    */
   public InRelationSearch(FromArtifactsSearch otherArtifacts, IRelationEnumeration firstSide, IRelationEnumeration... sides) {
      this.typeNames = new String[sides.length + 1];
      this.sideA = firstSide.getSide().isSideA();
      this.otherArtifactsCriteria = otherArtifacts;

      int count = 0;
      typeNames[count++] = firstSide.getName();
      for (IRelationEnumeration side : sides) {
         if (side != firstSide) {
            throw new IllegalArgumentException("All links must be for the same side.");
         }

         typeNames[count++] = side.getName();
      }
   }

   /**
    * @param typeName The type of relation for the artifact to be in.
    * @param sideA The side of the relation the artifact should be on.
    * @param otherArtifacts The search describing what the related artifacts should be like.
    */
   public InRelationSearch(String typeName, boolean sideA, FromArtifactsSearch otherArtifacts) {
      this(new String[] {typeName}, sideA, otherArtifacts);
   }

   public InRelationSearch(String[] typeNames, boolean sideA) {
      this(typeNames, sideA, null);
   }

   public InRelationSearch(String[] typeNames, boolean sideA, FromArtifactsSearch otherArtifacts) {
      this.typeNames = typeNames;
      this.sideA = sideA;
      this.otherArtifactsCriteria = otherArtifacts;
   }

   @Override
   public String getArtIdColName() {
      return sideA ? "a_art_id" : "b_art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      StringBuffer sql = new StringBuffer();

      boolean first = true;

      sql.append("rel_type_1.rel_link_type_id = rel_1.rel_link_type_id");

      sql.append(" AND ");

      if (typeNames.length > 1) {
         sql.append("(");
      }
      for (String typeName : typeNames) {
         if (!first) {
            sql.append(" OR ");
         } else {
            first = false;
         }

         sql.append("rel_type_1.type_name = ?");
         dataList.add(typeName);
      }
      if (typeNames.length > 1) {
         sql.append(")");
      }

      if (otherArtifactsCriteria != null) {
         sql.append(" AND ");
         sql.append(!sideA ? "rel_1.a_art_id" : "rel_1.b_art_id");
         sql.append(" IN (" + ArtifactPersistenceManager.getSelectArtIdSql(otherArtifactsCriteria, dataList, branch) + ")");
      }
      sql.append(" AND rel_1.gamma_id = txs1.gamma_id AND txs1.transaction_id = (SELECT max(txs1.transaction_id) FROM osee_relation_link rel2, osee_txs txs1 WHERE rel2.rel_link_id = rel_1.rel_link_id AND rel2.gamma_id = txs1.gamma_id AND txs1.branch_id = ? AND txs1.mod_type <>?)");

      dataList.add(branch.getId());
      dataList.add(ModificationType.DELETED.getValue());

      return sql.toString();
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      return relationTables;
   }

   @Override
   public String toString() {
      return "In Relation: " + typeNames + " from";
   }

   @Override
   public String getStorageString() {
      StringBuffer storage = new StringBuffer();

      storage.append(Boolean.toString(sideA));
      for (String typeName : typeNames) {
         storage.append(TOKEN);
         storage.append(typeName);
      }

      return storage.toString();
   }

   public static InRelationSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length < 2) {
         throw new IllegalStateException("Value for " + InRelationSearch.class.getSimpleName() + " not parsable");
      }

      String[] names = new String[values.length - 1];
      for (int x = 0; x < names.length; x++) {
         names[x] = values[x + 1];
      }

      return new InRelationSearch(names, Boolean.parseBoolean(values[0]), null);
   }
}