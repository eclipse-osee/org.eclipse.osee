/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.AttributeChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Jeff C. Phillips
 */
public class AttributeChangeAcquirer extends ChangeAcquirer {

   public AttributeChangeAcquirer(BranchId sourceBranch, TransactionToken transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      Map<Integer, ChangeBuilder> attributesWasValueCache = new HashMap<>();
      Map<Integer, ModificationType> artModTypes = new HashMap<>();
      Set<Integer> modifiedArtifacts = new HashSet<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      boolean hasBranch = getSourceBranch() != null;
      long time = System.currentTimeMillis();
      try {
         if (getMonitor() != null) {
            getMonitor().subTask("Gathering Attribute Changes");
         }
         TransactionToken fromTransactionId;
         TransactionToken toTransaction;
         boolean hasSpecificArtifact = getSpecificArtifact() != null;

         for (ChangeBuilder changeBuilder : getChangeBuilders()) {// cache in map for performance look ups
            artModTypes.put(changeBuilder.getArtId(), changeBuilder.getModType());
         }
         //Changes per a branch
         if (hasBranch) {
            fromTransactionId = getSourceBaseTransaction();
            toTransaction = TransactionManager.getHeadTransaction(getSourceBranch());
            chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_BRANCH_ATTRIBUTE_IS), getSourceBranch(),
               fromTransactionId);

         } //Changes per transaction number
         else {
            toTransaction = getTransaction();
            if (hasSpecificArtifact) {
               chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_TX_ATTRIBUTE_IS_FOR_SPECIFIC_ARTIFACT),
                  toTransaction.getBranch(), toTransaction.getId(), getSpecificArtifact().getArtId());
               fromTransactionId = getTransaction();
            } else {
               chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_TX_ATTRIBUTE_IS), toTransaction.getBranch(),
                  toTransaction.getId());
               fromTransactionId = TransactionManager.getPriorTransaction(toTransaction);
            }
         }
         loadIsValues(getSourceBranch(), getArtIds(), getChangeBuilders(), getNewAndDeletedArtifactIds(), getMonitor(),
            attributesWasValueCache, artModTypes, modifiedArtifacts, chStmt, hasBranch, time, fromTransactionId,
            toTransaction, hasSpecificArtifact);
         loadAttributeWasValues(getSourceBranch(), getTransaction(), getArtIds(), getMonitor(), attributesWasValueCache,
            hasBranch);
      } finally {
         chStmt.close();
      }
      return getChangeBuilders();
   }

   private void loadIsValues(BranchId sourceBranch, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds, IProgressMonitor monitor, Map<Integer, ChangeBuilder> attributesWasValueCache, Map<Integer, ModificationType> artModTypes, Set<Integer> modifiedArtifacts, JdbcStatement chStmt, boolean hasBranch, long time, TransactionToken fromTransactionId, TransactionToken toTransactionId, boolean hasSpecificArtifact) throws OseeCoreException {
      ModificationType artModType;
      AttributeChangeBuilder attributeChangeBuilder;

      try {
         TransactionDelta txDelta = new TransactionDelta(fromTransactionId, toTransactionId);

         while (chStmt.next()) {
            int attrId = chStmt.getInt("attr_id");
            int artId = chStmt.getInt("art_id");
            int sourceGamma = chStmt.getInt("gamma_id");
            long attrTypeId = chStmt.getLong("attr_type_id");
            ArtifactTypeId artifactType = ArtifactTypeId.valueOf(chStmt.getLong("art_type_id"));
            String isValue = chStmt.getString("is_value");
            ModificationType modificationType = ModificationType.getMod(chStmt.getInt("mod_type"));

            if (artModTypes.containsKey(artId)) {
               artModType = artModTypes.get(artId);
            } else {
               artModType = ModificationType.MODIFIED;
            }

            //This will be false iff the artifact was new and then deleted
            if (!newAndDeletedArtifactIds.contains(artId)) {
               // Want to add an artifact changed item once if any attribute was modified && artifact was not
               // NEW or DELETED and these changes are not for a specific artifact
               if (artModType == ModificationType.MODIFIED && !modifiedArtifacts.contains(artId)) {

                  ArtifactChangeBuilder artifactChangeBuilder = new ArtifactChangeBuilder(sourceBranch, artifactType,
                     -1, artId, txDelta, ModificationType.MODIFIED, !hasBranch);

                  changeBuilders.add(artifactChangeBuilder);
                  modifiedArtifacts.add(artId);
               }

               //ModTypes will be temporarily set to new and then revised for based on the existence of a was value
               if (modificationType == ModificationType.MODIFIED && artModType != ModificationType.INTRODUCED) {
                  modificationType = ModificationType.NEW;
               }
               AttributeType attributeType = AttributeTypeManager.getTypeByGuid(attrTypeId);
               attributeChangeBuilder = new AttributeChangeBuilder(sourceBranch, artifactType, sourceGamma, artId,
                  txDelta, modificationType, !hasBranch, isValue, "", attrId, attributeType, artModType);

               changeBuilders.add(attributeChangeBuilder);
               attributesWasValueCache.put(attrId, attributeChangeBuilder);
               artIds.add(artId);
            }
         }

         if (getMonitor() != null) {
            monitor.worked(13);
            monitor.subTask("Gathering Was values");
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadAttributeWasValues(BranchId sourceBranch, TransactionToken transactionId, Set<Integer> artIds, IProgressMonitor monitor, Map<Integer, ChangeBuilder> attributesWasValueCache, boolean hasBranch) throws OseeCoreException, OseeDataStoreException {
      if (!artIds.isEmpty()) {
         Id sqlParamter; // Will either be a branch uuid or transaction id
         BranchId wasValueBranch;
         String sql;

         if (hasBranch) {
            wasValueBranch = sourceBranch;
            sql = ServiceUtil.getSql(OseeSql.CHANGE_BRANCH_ATTRIBUTE_WAS);
            sqlParamter = wasValueBranch;
         } else {
            wasValueBranch = transactionId.getBranch();
            sql = ServiceUtil.getSql(OseeSql.CHANGE_TX_ATTRIBUTE_WAS);
            sqlParamter = transactionId;
         }

         Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery();
         try {
            for (int artId : artIds) {
               joinQuery.add(wasValueBranch, ArtifactId.valueOf(artId), TransactionId.SENTINEL,
                  wasValueBranch.getViewId());
            }
            joinQuery.store();

            JdbcStatement chStmt = ConnectionHandler.getStatement();
            try {
               chStmt.runPreparedQuery(sql, sqlParamter, joinQuery.getQueryId());
               int previousAttrId = -1;
               while (chStmt.next()) {
                  int attrId = chStmt.getInt("attr_id");

                  if (previousAttrId != attrId) {
                     String wasValue = chStmt.getString("was_value");
                     if (attributesWasValueCache.containsKey(
                        attrId) && attributesWasValueCache.get(attrId) instanceof AttributeChangeBuilder) {
                        AttributeChangeBuilder changeBuilder =
                           (AttributeChangeBuilder) attributesWasValueCache.get(attrId);

                        if (changeBuilder.getArtModType() != ModificationType.NEW) {
                           if (changeBuilder.getModType() != ModificationType.DELETED && changeBuilder.getModType() != ModificationType.ARTIFACT_DELETED) {
                              changeBuilder.setModType(ModificationType.MODIFIED);
                           }
                           changeBuilder.setWasValue(wasValue);
                        }
                     }
                     previousAttrId = attrId;
                  }
               }
            } finally {
               chStmt.close();
            }
         } finally {
            joinQuery.delete();
         }
         if (getMonitor() != null) {
            monitor.worked(12);
         }
      }
   }
}
