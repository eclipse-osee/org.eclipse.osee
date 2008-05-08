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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceMemo;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

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

   public List<Artifact> getArtifacts(ISearchConfirmer confirmer) throws SQLException {
      if (true) {
         return new ArrayList<Artifact>(ArtifactPersistenceManager.getInstance().getArtifacts(getArtifactsSql(),
               dataList, TransactionIdManager.getInstance().getEditableTransactionId(branch), null));
      }
      List<Artifact> artifacts = new LinkedList<Artifact>();
      List<Artifact> artifactsToInit = new LinkedList<Artifact>();
      ConnectionHandlerStatement chStmt = null;
      int artifactsCount = 0;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(getArtifactsSql(), dataList.toArray());
         ResultSet rSet = chStmt.getRset();

         while (rSet.next()) {
            artifactsCount++;

            Artifact artifact = ArtifactCache.getArtifact(rSet.getInt("art_id"), branch);
            if (artifact == null) { // if not in cache
               artifact = loadArtifactMetaData(rSet);
               artifactsToInit.add(artifact);
            }
            artifacts.add(artifact);
         }
      } finally {
         DbUtil.close(chStmt);
      }

      if (confirmer == null || confirmer.canProceed(artifactsCount)) {
         loadArtifactsData(artifactsToInit, loadLevel);
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

   private Artifact loadArtifactMetaData(ResultSet rSet) throws SQLException {
      ArtifactSubtypeDescriptor artifactType = ArtifactTypeManager.getType(rSet.getInt("art_type_id"));
      IArtifactFactory factory = artifactType.getFactory();

      Artifact artifact =
            factory.getNewArtifact(rSet.getString("guid"), rSet.getString("human_readable_id"),
                  artifactType.getFactoryKey(), branch, artifactType);
      artifact.setPersistenceMemo(new ArtifactPersistenceMemo(null, rSet.getInt("art_id"), rSet.getInt("gamma_id")));

      if (rSet.getInt("mod_type") == ModificationType.DELETED.getValue()) {
         artifact.setDeleted(rSet.getInt("transaction_id"));
      }
      return artifact;
   }

   /**
    * The in clause is limited to 1000 values at a time
    * 
    * @param artifacts
    * @param sql
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
         sql.append("IN (");
         sql.append(list, 0, list.length() - 1);
         sql.append(')');
      }
   }

   private String getAttributeSQL(Iterator<Artifact> artifacts, List<Object> localDataList) {
      StringBuilder sql = new StringBuilder(10000);
      sql.append("SELECT att1.* from osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.art_id");
      makeArtifactIdList(artifacts, sql, localDataList);
      sql.append(" AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());
      sql.append(" AND txs1.transaction_id txd1.transaction_id AND txd1.branch_id=? ORDER BY att1.art_id,txd1.transaction_id desc");
      return sql.toString();
   }

   /**
    * accepts an array of no more than 1000 artifacts sorted in ascending order
    * 
    * @param artifacts
    * @param transactionId
    * @throws SQLException
    */
   private void loadArtifactsData(List<Artifact> artifacts, ArtifactLoad loadLevel) throws SQLException {
      if (loadLevel == SHALLOW) {
         return;
      } else if (loadLevel == ATTRIBUTE) {
         loadAttributeData(artifacts);
      } else if (loadLevel == FULL) {
         loadAttributeData(artifacts);
      } else if (loadLevel == RELATION) {
      }

      for (Artifact artifact : artifacts) {
         artifact.setNotDirty(); // The artifacts are fresh, so mark them as not dirty
         artifact.onInitializationComplete();
      }
   }

   private void loadAttributeData(List<Artifact> artifacts) throws SQLException {
      Iterator<Artifact> artIdsIter = artifacts.iterator();
      Iterator<Artifact> artifactIterator = artifacts.iterator();
      while (artIdsIter.hasNext()) {

         ConnectionHandlerStatement chStmt = null;
         try {
            // NOTE: the 'ORDER BY att1.art_id,txd1.transaction_id desc' clause works with the DynamicAttributeManager
            // to ignore effectively overwritten attributes with {min,max} of {0,1} with >1 attribute instances.

            List<Object> attributeDataList = new ArrayList<Object>(4);

            String sql = getAttributeSQL(artIdsIter, attributeDataList);

            attributeDataList.add(SQL3DataType.INTEGER);
            attributeDataList.add(branch.getBranchId());
            chStmt = ConnectionHandler.runPreparedQuery(sql, attributeDataList.toArray());

            ResultSet rSet = chStmt.getRset();
            int artId;
            int lastArtId = -1; // Set to -1 to force a trigger on the first run of the loop
            while (rSet.next()) {
               Artifact artifact = null;

               artId = rSet.getInt("art_id");

               // Get a new artifact reference if the ID has changed
               if (artId != lastArtId) {
                  lastArtId = artId;
                  artifact = artifactIterator.next();
               }
               DynamicAttributeManager attributeManager =
                     AttributeTypeManager.getType(rSet.getInt("attr_type_id")).createAttributeManager(artifact, false);
               attributeManager.setupForInitialization(false);

               Attribute<?> attribute = attributeManager.injectFromDb(rSet.getString("value"), rSet.getString("uri"));
               attribute.setPersistenceMemo(new AttributeMemo(rSet.getInt("attr_id"), rSet.getInt("gamma_id")));

               // Finalize the initialization of all the attribute sets
               attributeManager.enforceMinMaxConstraints();
            }
         } finally {
            DbUtil.close(chStmt);
         }

      }
   }
}