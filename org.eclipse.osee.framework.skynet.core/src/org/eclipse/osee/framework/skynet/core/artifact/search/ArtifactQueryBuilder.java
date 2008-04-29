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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryBuilder {
   private final HashMap<String, NextAlias> nextAlias = new HashMap<String, NextAlias>();
   private final StringBuilder sql = new StringBuilder(1000);
   private final List<Object> dataList = new ArrayList<Object>();
   private AbstractArtifactSearchCriteria[] criteria;
   private final Branch branch;
   private int artId;
   private String guid;
   private String hrid;
   private ArtifactSubtypeDescriptor artifactType;
   private final boolean allowDeleted;

   /**
    * @param artId
    * @param branch
    * @param allowDeleted set whether deleted artifacts should be included in the resulting artifact list
    */
   public ArtifactQueryBuilder(int artId, Branch branch, boolean allowDeleted) {
      this(artId, null, null, branch, allowDeleted);
   }

   public ArtifactQueryBuilder(String guidOrHrid, Branch branch) {
      this(guidOrHrid, branch, false);
   }

   public ArtifactQueryBuilder(String guidOrHrid, Branch branch, boolean allowDeleted) {
      this(0, ensureValid(guidOrHrid), null, branch, allowDeleted);
   }

   private static String ensureValid(String str) {
      if (str == null) {
         throw new IllegalArgumentException("Id can not be null");
      }
      return str;
   }

   public ArtifactQueryBuilder(Branch branch, AbstractArtifactSearchCriteria... criteria) {
      this(0, null, null, branch, false, criteria);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch) {
      this(0, null, artifactType, branch, false);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, AbstractArtifactSearchCriteria... criteria) {
      this(0, null, artifactType, branch, false, criteria);
   }

   private ArtifactQueryBuilder(int artId, String guidOrHrid, ArtifactSubtypeDescriptor artifactType, Branch branch, boolean allowDeleted, AbstractArtifactSearchCriteria... criteria) {
      this.artifactType = artifactType;
      this.branch = branch;
      this.criteria = criteria;
      this.artId = artId;
      this.allowDeleted = allowDeleted;
      if (guidOrHrid != null) {
         if (GUID.isValid(guidOrHrid)) {
            guid = guidOrHrid;
         } else {
            hrid = guidOrHrid;
         }
      }

      nextAlias.put("osee_define_txs", new NextAlias("txs"));
      nextAlias.put("osee_define_tx_details", new NextAlias("txd"));
      nextAlias.put("osee_define_artifact", new NextAlias("art"));
      nextAlias.put("osee_define_artifact_version", new NextAlias("arv"));
      nextAlias.put("osee_define_attribute", new NextAlias("att"));
      nextAlias.put("osee_define_rel_link", new NextAlias("rel"));
   }

   public void addTxTablesSql() {
      appendAliasedTables("osee_define_txs", "osee_define_tx_details");
   }

   public String appendAliasedTable(String table, boolean comma) {
      String alias = getNextAlias(table);
      if (comma) {
         sql.append(',');
      }
      sql.append(table);
      sql.append(' ');
      sql.append(alias);
      return alias;
   }

   public String appendAliasedTable(String table) {
      return appendAliasedTable(table, true);
   }

   private void appendAliasedTables(String... tables) {
      for (String table : tables) {
         appendAliasedTable(table, true);
      }
   }

   public String getNextAlias(String table) {
      return nextAlias.get(table).getNextAlias();
   }

   private class NextAlias {
      String aliasPrefix;
      int aliasSuffix;

      public NextAlias(String aliasPrefix) {
         this.aliasPrefix = aliasPrefix;
         this.aliasSuffix = 1;
      }

      public String getNextAlias() {
         return aliasPrefix + aliasSuffix++;
      }
   }

   public String getArtifactsSql() throws SQLException {
      sql.append("SELECT art1.*, arv1.gamma_id FROM ");
      appendAliasedTable("osee_define_artifact", false);
      appendAliasedTable("osee_define_artifact_version");
      addTxTablesSql();

      if (criteria.length > 0) {
         for (AbstractArtifactSearchCriteria x : criteria) {
            x.addToTableSql(this);
         }
      }
      sql.append(" WHERE ");
      if (guid != null) {
         sql.append("art1.guid=? AND ");
         addParameter(SQL3DataType.VARCHAR, guid);
      }
      if (artId != 0) {
         sql.append("art1.art_id=? AND ");
         addParameter(SQL3DataType.INTEGER, artId);
      }
      if (artifactType != null) {
         sql.append("art1.art_type_id=? AND ");
         addParameter(SQL3DataType.INTEGER, artifactType.getArtTypeId());
      }
      if (hrid != null) {
         sql.append("art1.human_readable_id=? AND ");
         addParameter(SQL3DataType.VARCHAR, hrid);
      }

      if (criteria.length > 0) {
         criteria[0].addToWhereSql(this);
         for (int i = 1; i < criteria.length; i++) {
            AbstractArtifactSearchCriteria leftCriteria = criteria[i - 1];
            AbstractArtifactSearchCriteria rightCriteria = criteria[i];
            leftCriteria.addJoinArtId(this, true);
            sql.append("=");
            rightCriteria.addJoinArtId(this, false);
            sql.append(" AND ");
            rightCriteria.addToWhereSql(this);
            sql.append(" AND ");
         }
         criteria[criteria.length - 1].addJoinArtId(this, false);
         sql.append("=art1.art_id AND ");
      }

      sql.append("art1.art_id=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND ");
      sql.append("txs1.tx_current=1 AND ");
      if (!allowDeleted) {
         sql.append("txs1.tx_type<>");
         sql.append(TransactionType.Deleted.getId());
         sql.append(" AND ");
      }
      sql.append("txs1.transaction_id=txd1.transaction_id AND txd1.branch_id=?");
      addParameter(SQL3DataType.INTEGER, branch.getBranchId());

      return sql.toString();
   }

   public void append(String sqlSnippet) {
      sql.append(sqlSnippet);
   }

   public void addParameter(SQL3DataType sqlType, Object data) {
      dataList.add(sqlType);
      dataList.add(data);
   }

   public void addCurrentTxSql(String txsAlias, String txdAlias) {
      addCurrentTxSql(txsAlias, txdAlias, branch);
   }

   public void addCurrentTxSql(String txsAlias, String txdAlias, Branch branch) {
      sql.append(txsAlias);
      sql.append(".tx_current=1 AND ");
      sql.append(txsAlias);
      sql.append(".tx_type<>");
      sql.append(TransactionType.Deleted.getId());
      sql.append(" AND ");
      sql.append(txsAlias);
      sql.append(".transaction_id=");
      sql.append(txdAlias);
      sql.append(".transaction_id AND ");
      sql.append(txdAlias);
      sql.append(".branch_id=? AND ");
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
   }

   public Collection<Artifact> getArtifacts() throws SQLException {
      return ArtifactPersistenceManager.getInstance().getArtifacts(getArtifactsSql(), dataList,
            TransactionIdManager.getInstance().getEditableTransactionId(branch), null);
   }
}