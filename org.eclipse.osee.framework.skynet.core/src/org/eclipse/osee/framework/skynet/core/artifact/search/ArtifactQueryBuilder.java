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

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.ATTRIBUTE;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.RELATION;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.SHALLOW;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.relation.IRelationType;
import org.eclipse.osee.framework.skynet.core.relation.LinkPersistenceMemo;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryBuilder {
   private final List<Artifact> artifacts = new LinkedList<Artifact>();
   private final HashMap<String, NextAlias> nextAlias = new HashMap<String, NextAlias>();
   private final StringBuilder sql = new StringBuilder(1000);
   private final List<Object> dataList = new ArrayList<Object>();
   private List<String> guids;
   private List<String> hrids;
   private String guidOrHrid;
   private AbstractArtifactSearchCriteria[] criteria;
   private final Branch branch;
   private int artifactId;
   private boolean searchIsNeeded;
   private Collection<Integer> artifactIds;
   private ArtifactSubtypeDescriptor artifactType;
   private final boolean allowDeleted;
   private final ArtifactLoad loadLevel;

   /**
    * @param artId
    * @param branch
    * @param allowDeleted set whether deleted artifacts should be included in the resulting artifact list
    */
   public ArtifactQueryBuilder(int artId, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel) {
      this(null, artId, null, null, null, branch, allowDeleted, loadLevel);
   }

   /**
    * search for artifacts with the given ids
    * 
    * @param artifactIds list of artifact ids
    * @param branch
    * @param allowDeleted set whether deleted artifacts should be included in the resulting artifact list
    */
   public ArtifactQueryBuilder(Collection<Integer> artifactIds, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel) {
      this(artifactIds, 0, null, null, null, branch, allowDeleted, loadLevel);
   }

   public ArtifactQueryBuilder(List<String> guidOrHrids, Branch branch, ArtifactLoad loadLevel) {
      this(null, 0, guidOrHrids, null, null, branch, false, loadLevel);
   }

   public ArtifactQueryBuilder(String guidOrHrid, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel) {
      this(null, 0, null, ensureValid(guidOrHrid), null, branch, allowDeleted, loadLevel);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, ArtifactLoad loadLevel) {
      this(null, 0, null, null, artifactType, branch, false, loadLevel);
   }

   public ArtifactQueryBuilder(Branch branch, ArtifactLoad loadLevel) {
      this(null, 0, null, null, null, branch, false, loadLevel);
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

   public ArtifactQueryBuilder(Branch branch, ArtifactLoad loadLevel, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, null, branch, false, loadLevel, criteria);
   }

   public ArtifactQueryBuilder(Branch branch, ArtifactLoad loadLevel, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, null, branch, false, loadLevel, toArray(criteria));
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, ArtifactLoad loadLevel, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, artifactType, branch, false, loadLevel, criteria);
   }

   public ArtifactQueryBuilder(ArtifactSubtypeDescriptor artifactType, Branch branch, ArtifactLoad loadLevel, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, artifactType, branch, false, loadLevel, toArray(criteria));
   }

   private ArtifactQueryBuilder(Collection<Integer> artifactIds, int artifactId, List<String> guidOrHrids, String guidOrHrid, ArtifactSubtypeDescriptor artifactType, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel, AbstractArtifactSearchCriteria... criteria) {
      this.artifactType = artifactType;
      this.branch = branch;
      this.criteria = criteria;
      this.loadLevel = loadLevel;
      this.allowDeleted = allowDeleted;
      this.guidOrHrid = guidOrHrid;
      this.artifactId = artifactId;
      this.searchIsNeeded = true;

      if (artifactIds != null && !artifactIds.isEmpty()) {
         // remove from search list any that are already in the cache
         Iterator<Integer> iterator = artifactIds.iterator();
         while (iterator.hasNext()) {
            Artifact artifact = ArtifactCache.get(iterator.next(), branch);
            if (artifact != null) {
               artifacts.add(artifact);
               iterator.remove();
            }
         }
         if (artifactIds.size() == 0) {
            searchIsNeeded = false;
         } else if (artifactIds.size() == 1) {
            this.artifactId = artifactIds.iterator().next();
         } else {
            this.artifactIds = artifactIds;
         }
      }

      if (this.artifactId != 0) {
         Artifact artifact = ArtifactCache.get(this.artifactId, branch);
         if (artifact != null) {
            artifacts.add(artifact);
            searchIsNeeded = false;
         }
      }

      if (artifactIds != null && !artifactIds.isEmpty()) {
         // remove from search list any that are already in the cache
         Iterator<Integer> iterator = artifactIds.iterator();
         while (iterator.hasNext()) {
            Artifact artifact = ArtifactCache.get(iterator.next(), branch);
            if (artifact != null) {
               artifacts.add(artifact);
               iterator.remove();
            }
         }
         if (artifactIds.size() == 0) {
            searchIsNeeded = false;
         } else if (artifactIds.size() == 1) {
            this.artifactId = artifactIds.iterator().next();
         } else {
            this.artifactIds = artifactIds;
         }
      }

      if (guidOrHrids != null && !guidOrHrids.isEmpty()) {
         // remove from search list any that are already in the cache
         Iterator<String> iterator = guidOrHrids.iterator();
         while (iterator.hasNext()) {
            Artifact artifact = ArtifactCache.get(iterator.next(), branch);
            if (artifact != null) {
               artifacts.add(artifact);
               iterator.remove();
            }
         }
         if (guidOrHrids.size() == 0) {
            searchIsNeeded = false;
         } else if (guidOrHrids.size() == 1) {
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

      if (this.guidOrHrid != null) {
         Artifact artifact = ArtifactCache.get(this.guidOrHrid, branch);
         if (artifact != null) {
            artifacts.add(artifact);
            searchIsNeeded = false;
         }
      }

      if (searchIsNeeded) {
         nextAlias.put("osee_define_txs", new NextAlias("txs"));
         nextAlias.put("osee_define_tx_details", new NextAlias("txd"));
         nextAlias.put("osee_define_artifact", new NextAlias("art"));
         nextAlias.put("osee_define_artifact_version", new NextAlias("arv"));
         nextAlias.put("osee_define_attribute", new NextAlias("att"));
         nextAlias.put("osee_define_rel_link", new NextAlias("rel"));
      }
   }

   private String getArtifactsSql() throws SQLException {
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
         sql.append("art1.guid IN ('" + Collections.toString("','", guids) + "') AND ");
      }
      if (hrids != null && hrids.size() > 0) {
         sql.append("art1.human_readable_id IN ('" + Collections.toString("','", hrids) + "') AND ");
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
      sql.append(" AND txd1.branch_id=?");
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
      sql.append(".transaction_id=");
      sql.append(txdAlias);
      sql.append(".transaction_id AND ");
      sql.append(txdAlias);
      sql.append(".branch_id=? AND ");
      addParameter(SQL3DataType.INTEGER, branch.getBranchId());
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

   public List<Artifact> getArtifacts(ISearchConfirmer confirmer) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      int artifactsCount = artifacts.size();

      if (searchIsNeeded) {
         try {
            chStmt = ConnectionHandler.runPreparedQuery(getArtifactsSql(), dataList.toArray());
            ResultSet rSet = chStmt.getRset();

            while (rSet.next()) {
               artifactsCount++;
               artifacts.add(ArtifactPersistenceManager.loadArtifactMetaData(rSet, branch, true));
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      if (confirmer == null || confirmer.canProceed(artifactsCount)) {
         loadArtifactsData(loadLevel);
      } else {
         artifacts.clear();
      }

      return artifacts;
   }

   public Artifact getArtifact() throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = getArtifacts(null);

      if (artifacts.size() == 0) {
         throw new ArtifactDoesNotExist(getSoleExceptionMessage(artifacts.size()));
      }
      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist(getSoleExceptionMessage(artifacts.size()));
      }
      return artifacts.iterator().next();
   }

   private String getSoleExceptionMessage(int artifactCount) {
      StringBuilder message = new StringBuilder(250);
      if (artifactCount == 0) {
         message.append("No artifact found");
      } else {
         message.append(artifactCount);
         message.append(" artifacts found");
      }
      if (artifactType != null) {
         message.append(" with type \"");
         message.append(artifactType.getName());
         message.append("\"");
      }
      if (artifactId != 0) {
         message.append(" with id \"");
         message.append(artifactId);
         message.append("\"");
      }
      if (guidOrHrid != null) {
         message.append(" with id \"");
         message.append(guidOrHrid);
         message.append("\"");
      }
      if (criteria.length > 0) {
         message.append(" with criteria \"");
         message.append(Arrays.deepToString(criteria));
         message.append("\"");
      }
      message.append(" on branch \"");
      message.append(branch);
      message.append("\"");
      return message.toString();
   }

   /**
    * The in clause is limited to 1000 values at a time
    * 
    * @param artifacts
    * @param sql
    * @param localDataList
    */
   private void makeArtifactIdList(Iterator<Artifact> artifacts, StringBuilder sql, List<Object> localDataList) {
      StringBuilder list = new StringBuilder(8000);
      int count = 0;
      int artId = 0;
      while (artifacts.hasNext() && count++ < 1000) {
         artId = artifacts.next().getArtId();
         list.append(artId);
         list.append(',');
      }

      if (count == 1) {
         sql.append("=?");
         localDataList.add(SQL3DataType.INTEGER);
         localDataList.add(artId);
      } else {
         sql.append(" IN (");
         sql.append(list, 0, list.length() - 1);
         sql.append(')');
      }
   }

   private String getAttributeSQL(Iterator<Artifact> artifacts, List<Object> attributeDataList) {
      StringBuilder sql = new StringBuilder(10000);
      sql.append("SELECT att1.* from osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.art_id");

      makeArtifactIdList(artifacts, sql, attributeDataList);

      sql.append(" AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());
      sql.append(" AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=?");

      attributeDataList.add(SQL3DataType.INTEGER);
      attributeDataList.add(branch.getBranchId());

      return sql.toString();
   }

   private String getRelationSQL(Iterator<Artifact> artifacts, List<Object> relationDataList) {
      StringBuilder sql = new StringBuilder(10000);
      sql.append("SELECT rel1.*, txs1.* FROM osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE (rel1.a_art_id");

      makeArtifactIdList(artifacts, sql, relationDataList);
      sql.append(" OR rel1.b_art_id");
      makeArtifactIdList(artifacts, sql, relationDataList);

      sql.append(") AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());
      sql.append(" AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ?");

      relationDataList.add(SQL3DataType.INTEGER);
      relationDataList.add(branch.getBranchId());

      return sql.toString();
   }

   /**
    * accepts an array of no more than 1000 artifacts sorted in ascending order
    * 
    * @param artifacts
    * @param transactionId
    * @throws SQLException
    */
   private void loadArtifactsData(ArtifactLoad loadLevel) throws SQLException {
      if (artifacts.size() == 0) {
         return;
      }
      if (loadLevel == SHALLOW) {
         return;
      } else if (loadLevel == ATTRIBUTE) {
         loadAttributesData();
      } else if (loadLevel == FULL) {
         loadAttributesData();
         loadRelationData();
      } else if (loadLevel == RELATION) {
         loadRelationData();
      }

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
      }
   }

   private void loadRelationData() throws SQLException {
      List<Artifact> artifactsNeedingRelations = new ArrayList<Artifact>(artifacts.size());
      List<RelationLink> relations = new ArrayList<RelationLink>(artifacts.size() * 4);
      Set<Integer> artifactIdsToLoad = new HashSet<Integer>(relations.size() + 1);

      for (Artifact artifact : artifacts) {
         if (!artifact.isLinkManagerLoaded()) {
            artifactsNeedingRelations.add(artifact);
         }
      }

      Iterator<Artifact> artIdsIter = artifactsNeedingRelations.iterator();
      while (artIdsIter.hasNext()) {

         ConnectionHandlerStatement chStmt = null;
         try {
            List<Object> relationDataList = new ArrayList<Object>(6);
            String sql = getRelationSQL(artIdsIter, relationDataList);
            chStmt = ConnectionHandler.runPreparedQuery(sql, relationDataList.toArray());

            ResultSet rSet = chStmt.getRset();
            while (rSet.next()) {
               int relationId = rSet.getInt("rel_link_id");
               int aArtId = rSet.getInt("a_art_id");
               int bArtId = rSet.getInt("b_art_id");

               Artifact artA = ArtifactCache.get(aArtId, branch);
               Artifact artB = ArtifactCache.get(bArtId, branch);

               RelationLink link = null;
               if (artA != null) {
                  link = artA.getLinkManager().getRelation(relationId);
               }
               if (link == null && artB != null) {
                  link = artB.getLinkManager().getRelation(relationId);
               }

               if (link == null) {
                  artifactIdsToLoad.add(aArtId);
                  artifactIdsToLoad.add(bArtId);

                  int aOrderValue = rSet.getInt("a_order_value");
                  int bOrderValue = rSet.getInt("b_order_value");
                  int gammaId = rSet.getInt("gamma_id");
                  String rationale = rSet.getString("rationale");
                  if (rationale == null) rationale = "";
                  IRelationType relationType = RelationTypeManager.getType(rSet.getInt("rel_link_type_id"));

                  link =
                        new RelationLink(aArtId, bArtId, relationType, new LinkPersistenceMemo(relationId, gammaId),
                              rationale, aOrderValue, bOrderValue, false);
                  relations.add(link);
               }
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      for (RelationLink relation : relations) {
         relation.loadArtifacts(branch);
      }

      for (Artifact artifact : artifactsNeedingRelations) {
         artifact.setLinksLoaded();
      }
   }

   private void loadAttributesData() throws SQLException {
      List<Artifact> artifactsNeedingAttributes = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (!artifact.isAttributesLoaded()) {
            artifactsNeedingAttributes.add(artifact);
         }
      }

      Iterator<Artifact> artIdsIter = artifactsNeedingAttributes.iterator();
      while (artIdsIter.hasNext()) {

         ConnectionHandlerStatement chStmt = null;
         try {
            List<Object> attributeDataList = new ArrayList<Object>(4);
            String sql = getAttributeSQL(artIdsIter, attributeDataList);
            chStmt = ConnectionHandler.runPreparedQuery(sql, attributeDataList.toArray());

            ResultSet rSet = chStmt.getRset();
            while (rSet.next()) {
               Artifact artifact = ArtifactCache.get(rSet.getInt("art_id"), branch);
               if (artifact == null) {
                  int i = 5;
               }
               AttributeToTransactionOperation.initializeAttribute(artifact, rSet.getInt("attr_type_id"),
                     rSet.getString("value"), rSet.getString("uri"), rSet.getInt("attr_id"), rSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      for (Artifact artifact : artifactsNeedingAttributes) {
         AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact);
      }
   }
}