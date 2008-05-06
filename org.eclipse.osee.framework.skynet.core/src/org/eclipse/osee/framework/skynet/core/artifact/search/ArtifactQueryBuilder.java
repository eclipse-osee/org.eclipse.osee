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
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryBuilder {
   private final HashMap<String, NextAlias> nextAlias = new HashMap<String, NextAlias>();
   private final StringBuilder sql = new StringBuilder(1000);
   private final List<Object> dataList = new ArrayList<Object>();
   private List<String> guids;
   private List<String> hrids;
   private String guidOrHrid;
   private AbstractArtifactSearchCriteria[] criteria;
   private final Branch branch;
   private int artifactId;
   private Collection<Integer> artifactIds;
   private ArtifactSubtypeDescriptor artifactType;
   private final boolean allowDeleted;

   /**
    * @param artId
    * @param branch
    * @param allowDeleted set whether deleted artifacts should be included in the resulting artifact list
    */
   public ArtifactQueryBuilder(int artId, Branch branch, boolean allowDeleted) {
      this(null, artId, null, null, null, branch, allowDeleted);
   }

   /**
    * search for artifacts with the given ids
    * 
    * @param artifactIds list of artifact ids
    * @param branch
    * @param allowDeleted set whether deleted artifacts should be included in the resulting artifact list
    */
   public ArtifactQueryBuilder(Collection<Integer> artifactIds, Branch branch, boolean allowDeleted) {
      this(artifactIds, 0, null, null, null, branch, allowDeleted);
   }

   public ArtifactQueryBuilder(List<String> guidOrHrids, Branch branch) {
      this(null, 0, guidOrHrids, null, null, branch, false);
   }

   public ArtifactQueryBuilder(String guidOrHrid, Branch branch, boolean allowDeleted) {
      this(null, 0, null, ensureValid(guidOrHrid), null, branch, allowDeleted);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch) {
      this(null, 0, null, null, artifactType, branch, false);
   }

   private static String ensureValid(String id) {
      if (id == null) {
         throw new IllegalArgumentException("The id can not be null.");
      }
      return id;
   }

   private static AbstractArtifactSearchCriteria[] toArray(List<AbstractArtifactSearchCriteria> criteria) {
      return criteria.toArray(new AbstractArtifactSearchCriteria[criteria.size()]);
   }

   public ArtifactQueryBuilder(AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, null, null, false, criteria);
   }

   public ArtifactQueryBuilder(Branch branch, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, null, branch, false, criteria);
   }

   public ArtifactQueryBuilder(Branch branch, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, null, branch, false, toArray(criteria));
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, artifactType, branch, false, criteria);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, artifactType, branch, false, toArray(criteria));
   }

   private ArtifactQueryBuilder(Collection<Integer> artifactIds, int artifactId, List<String> guidOrHrids, String guidOrHrid, ArtifactSubtypeDescriptor artifactType, Branch branch, boolean allowDeleted, AbstractArtifactSearchCriteria... criteria) {
      this.artifactType = artifactType;
      this.branch = branch;
      this.criteria = criteria;

      this.allowDeleted = allowDeleted;
      this.guidOrHrid = guidOrHrid;
      this.artifactId = artifactId;

      if (artifactIds != null) {
         if (artifactIds.size() == 1) {
            this.artifactId = artifactIds.iterator().next();
         } else {
            this.artifactIds = artifactIds;
         }
      }

      if (guidOrHrids != null) {
         if (guidOrHrids.size() == 1) {
            this.guidOrHrid = guidOrHrids.get(0);
         } else {
            for (String id : guidOrHrids) {
               hrids = new ArrayList<String>();
               guids = new ArrayList<String>();
               if (GUID.isValid(id)) {
                  guids.add(id);
               } else {
                  hrids.add(id);
               }
            }
         }
      }

      nextAlias.put("osee_define_txs", new NextAlias("txs"));
      nextAlias.put("osee_define_tx_details", new NextAlias("txd"));
      nextAlias.put("osee_define_artifact", new NextAlias("art"));
      nextAlias.put("osee_define_artifact_version", new NextAlias("arv"));
      nextAlias.put("osee_define_attribute", new NextAlias("att"));
      nextAlias.put("osee_define_rel_link", new NextAlias("rel"));
   }

   public String getArtifactsSql() throws SQLException {
      sql.append("SELECT art1.*, txs1.* FROM ");
      appendAliasedTable("osee_define_artifact", false);
      appendAliasedTable("osee_define_artifact_version");
      addTxTablesSql();
      sql.append("\n");

      if (criteria.length > 0) {
         for (AbstractArtifactSearchCriteria x : criteria) {
            x.addToTableSql(this);
         }
      }
      sql.append(" WHERE ");

      if (artifactId != 0) {
         sql.append("art1.art_id=? AND ");
         addParameter(SQL3DataType.INTEGER, artifactId);
      }

      if (artifactIds != null) {
         sql.append("art1.art_id IN (" + Collections.toString(",", artifactIds) + ") AND ");
      }
      if (artifactType != null) {
         sql.append("art1.art_type_id=? AND ");
         addParameter(SQL3DataType.INTEGER, artifactType.getArtTypeId());
      }

      if (guidOrHrid != null) {
         if (GUID.isValid(guidOrHrid)) {
            sql.append("art1.guid=? AND ");
         } else {
            sql.append("art1.human_readable_id=? AND ");
         }
         addParameter(SQL3DataType.VARCHAR, guidOrHrid);
      }

      if (guids != null && guids.size() > 0) {
         sql.append("art1.guid IN (" + Collections.toString(",", guids) + ") AND ");
      }
      if (hrids != null && hrids.size() > 0) {
         sql.append("art1.human_readable_id IN (" + Collections.toString(",", hrids) + ") AND ");
      }

      sql.append("\n");
      if (criteria.length > 0) {
         criteria[0].addToWhereSql(this);
         sql.append("\n");
         for (int i = 1; i < criteria.length; i++) {
            AbstractArtifactSearchCriteria leftCriteria = criteria[i - 1];
            AbstractArtifactSearchCriteria rightCriteria = criteria[i];
            leftCriteria.addJoinArtId(this, false);
            sql.append("=");
            rightCriteria.addJoinArtId(this, true);
            sql.append(" AND ");
            rightCriteria.addToWhereSql(this);
            sql.append("\n");
         }
         criteria[criteria.length - 1].addJoinArtId(this, false);
         sql.append("=art1.art_id AND ");
      }

      sql.append("art1.art_id=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND ");
      if (allowDeleted) {
         sql.append("(");
      }
      sql.append("txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());

      if (allowDeleted) {
         sql.append(" OR txs1.mod_type=");
         sql.append(ModificationType.DELETED.getValue());
         sql.append(")");
      }

      sql.append(" AND txs1.transaction_id=txd1.transaction_id");
      if (branch != null) {
         sql.append(" AND txd1.branch_id=?");
         addParameter(SQL3DataType.INTEGER, branch.getBranchId());
      }

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
      sql.append(".transaction_id=");
      sql.append(txdAlias);
      sql.append(".transaction_id AND ");
      sql.append(txdAlias);
      sql.append(".branch_id=? AND ");
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
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

   public Collection<Artifact> getArtifacts() throws SQLException {
      return ArtifactPersistenceManager.getInstance().getArtifacts(getArtifactsSql(), dataList,
            TransactionIdManager.getInstance().getEditableTransactionId(branch), null);
   }
}