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

import static org.eclipse.osee.framework.database.sql.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.EQUAL;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.database.sql.LocalAliasTable;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;

/**
 * @author Robert A. Fisher
 */
public class InRelationSearch implements ISearchPrimitive {
   private static final LocalAliasTable LINK_ALIAS_1 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "rel_1");
   private static final LocalAliasTable LINK_ALIAS_2 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "rel_2");
   private static final LocalAliasTable LINK_TYPE_ALIAS_1 =
         new LocalAliasTable("OSEE_RELATION_LINK_TYPE", "rel_type_1");
   private static final String relationTables = LINK_TYPE_ALIAS_1 + "," + LINK_ALIAS_1 + ", " + TRANSACTIONS_TABLE;
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
      this.sideA = firstSide.isSideA();
      this.otherArtifactsCriteria = otherArtifacts;

      int count = 0;
      typeNames[count++] = firstSide.getName();
      for (IRelationEnumeration side : sides) {
         if (side.isSideA() != firstSide.isSideA()) {
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

   public String getArtIdColName() {
      return sideA ? "a_art_id" : "b_art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      StringBuffer sql = new StringBuffer();

      boolean first = true;

      sql.append(LINK_TYPE_ALIAS_1.column("rel_link_type_id") + EQUAL + LINK_ALIAS_1.column("rel_link_type_id"));

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

         sql.append(LINK_TYPE_ALIAS_1.column("type_name") + "=?");
         dataList.add(typeName);
      }
      if (typeNames.length > 1) {
         sql.append(")");
      }

      if (otherArtifactsCriteria != null) {
         sql.append(" AND " + LINK_ALIAS_1.column((!sideA ? "a_art_id" : "b_art_id")) + " IN (" + ArtifactPersistenceManager.getSelectArtIdSql(
               otherArtifactsCriteria, dataList, branch) + ")");
      }

      sql.append(" AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT max(osee_tx_details.transaction_id) FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)" + " AND " + TRANSACTIONS_TABLE.column("mod_type") + "<>?");

      dataList.add(branch.getBranchId());
      dataList.add(ModificationType.DELETED.getValue());

      return sql.toString();
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return relationTables;
   }

   @Override
   public String toString() {
      return "In Relation: " + typeNames + " from";
      //"side " + ((sideA)?"A":"B");
   }

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