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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.LoadLevel.ARTIFACT_DATA;
import static org.eclipse.osee.framework.core.enums.LoadLevel.RELATION_DATA;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan Schmitt
 */
public class AttributeLoader {

   static void loadAttributeData(int queryId, CompositeKeyHashMap<ArtifactId, Id, Artifact> tempCache, boolean historical, DeletionFlag allowDeletedArtifacts, LoadLevel loadLevel, boolean isArchived) {
      if (loadLevel == ARTIFACT_DATA || loadLevel == RELATION_DATA) {
         return;
      }

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String sql = getSql(allowDeletedArtifacts, loadLevel, historical, isArchived);
         chStmt.runPreparedQuery(tempCache.size() * 8, sql, queryId);

         Artifact currentArtifact = null;
         AttrData previousAttr = new AttrData();
         List<AttrData> currentAttributes = new ArrayList<>();

         while (chStmt.next()) {
            AttrData nextAttr = new AttrData(chStmt, historical);

            if (AttrData.isDifferentArtifact(previousAttr, nextAttr)) {
               loadAttributesFor(currentArtifact, currentAttributes, historical);
               currentAttributes.clear();
               currentArtifact = getArtifact(nextAttr, historical, tempCache);
            }

            currentAttributes.add(nextAttr);
            previousAttr = nextAttr;
         }
         loadAttributesFor(currentArtifact, currentAttributes, historical);
      } finally {
         chStmt.close();
      }
      for (Artifact art : tempCache.values()) {
         synchronized (ArtifactCache.class) {
            ArtifactCache.cache(art);
         }
      }
   }

   private static final class AttrData {
      public ArtifactId artifactId = ArtifactId.SENTINEL;
      public BranchId branch = BranchId.SENTINEL;
      public AttributeId attrId = AttributeId.SENTINEL;
      public GammaId gammaId = GammaId.SENTINEL;
      public ModificationType modType;
      public Long transactionId = -1L;
      public AttributeTypeId attributeType = AttributeTypeId.SENTINEL;
      public Object value = "";
      public TransactionId stripeId = TransactionId.SENTINEL;
      public String uri = "";
      public ApplicabilityId applicabilityId = ApplicabilityId.BASE;

      public AttrData() {
         // do nothing
      }

      public AttrData(JdbcStatement chStmt, boolean historical) {
         artifactId = ArtifactId.valueOf(chStmt.getLong("art_id"));
         branch = BranchId.valueOf(chStmt.getLong("id1"));
         attrId = AttributeId.valueOf(chStmt.getLong("attr_id"));
         gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
         modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

         transactionId = chStmt.getLong("transaction_id");
         attributeType = AttributeTypeManager.getTypeById(chStmt.getLong("attr_type_id"));

         AttributeType typeByGuid = AttributeTypeManager.getType(attributeType);
         String baseAttributeType = typeByGuid.getBaseAttributeTypeId();
         if (baseAttributeType.contains("BooleanAttribute")) {
            value = chStmt.getBoolean("value");
         } else if (baseAttributeType.contains("FloatingPointAttribute")) {
            value = chStmt.getDouble("value");
         } else if (baseAttributeType.contains("IntegerAttribute")) {
            value = chStmt.getInt("value");
         } else if (baseAttributeType.contains("LongAttribute")) {
            value = chStmt.getLong("value");
         } else if (baseAttributeType.contains("ArtifactReferenceAttribute")) {
            value = ArtifactId.valueOf(chStmt.getString("value"));
         } else if (baseAttributeType.contains("BranchReferenceAttribute")) {
            value = BranchId.valueOf(chStmt.getString("value"));
         } else if (baseAttributeType.contains("DateAttribute")) {
            value = new Date(chStmt.getLong("value"));
         } else {
            value = chStmt.getString("value");
            if (baseAttributeType.contains("EnumeratedAttribute")) {
               value = Strings.intern((String) value);
            }
         }

         if (historical) {
            stripeId = TransactionId.valueOf(chStmt.getLong("stripe_transaction_id"));
         }
         uri = chStmt.getString("uri");
         applicabilityId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
      }

      public static boolean isDifferentArtifact(AttrData previous, AttrData current) {
         return current.branch.notEqual(previous.branch) || current.artifactId.notEqual(previous.artifactId);
      }

      public static boolean multipleVersionsExist(AttrData current, AttrData previous) {
         return current.attrId.equals(previous.attrId) && current.branch.equals(
            previous.branch) && current.artifactId.equals(previous.artifactId);
      }
   }

   private static Artifact getArtifact(AttrData current, boolean historical, CompositeKeyHashMap<ArtifactId, Id, Artifact> tempCache) {
      Artifact artifact = null;
      Id key2 = historical ? current.stripeId : current.branch;
      artifact = tempCache.get(current.artifactId, key2);
      if (artifact == null) {
         OseeLog.logf(ArtifactLoader.class, Level.WARNING, "Orphaned attribute id [%s] for artifact id[%s] branch[%s]",
            current.attrId, current.artifactId, current.branch);
      }
      return artifact;
   }

   private static void loadAttributesFor(Artifact artifact, List<AttrData> attributes, boolean historical) {
      if (artifact == null) {
         return; // If the artifact is null, it means the attributes are orphaned.
      }
      Long maxTransaction = Id.SENTINEL;
      AttrData previous = new AttrData();
      synchronized (artifact) {
         if (!artifact.isAttributesLoaded()) {
            for (AttrData current : attributes) {
               if (AttrData.multipleVersionsExist(current, previous)) {
                  handleMultipleVersions(previous, current, historical);
               } else {
                  loadAttribute(artifact, current, previous);
                  if (current.transactionId > maxTransaction) {
                     maxTransaction = current.transactionId;
                  }
               }
               previous = current;
            }
            artifact.setTransactionId(TransactionToken.valueOf(maxTransaction, artifact.getBranch()));
            artifact.meetMinimumAttributeCounts(false);
         }
      }
   }

   private static void handleMultipleVersions(AttrData previous, AttrData current, boolean historical) {
      // Do not warn about skipping on historical loading, because the most recent
      // transaction is used first due to sorting on the query
      if (!historical) {
         OseeLog.logf(ArtifactLoader.class, Level.WARNING,
            "multiple attribute version for attribute id [%s] artifact id[%s] branch[%s] previousGammaId[%s] currentGammaId[%s] previousModType[%s] currentModType[%s]",
            current.attrId, current.artifactId, current.branch, previous.gammaId, current.gammaId, previous.modType,
            current.modType);
      }
   }

   private static void loadAttribute(Artifact artifact, AttrData current, AttrData previous) {
      boolean markDirty = false;
      artifact.internalInitializeAttribute(current.attributeType, current.attrId, current.gammaId, current.modType,
         current.applicabilityId, markDirty, current.value, current.uri);
   }

   private static String getSql(DeletionFlag allowDeletedArtifacts, LoadLevel loadLevel, boolean historical, boolean isArchived) {
      OseeSql sqlKey;
      if (historical && isArchived) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ARCHIVED_ATTRIBUTES;
      } else if (historical) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ATTRIBUTES;
      } else if (isArchived && allowDeletedArtifacts == INCLUDE_DELETED) {
         sqlKey = OseeSql.LOAD_CURRENT_ARCHIVED_ATTRIBUTES_WITH_DELETED;
      } else if (isArchived) {
         sqlKey = OseeSql.LOAD_CURRENT_ARCHIVED_ATTRIBUTES;
      } else if (allowDeletedArtifacts == INCLUDE_DELETED) {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES;
      }

      return ServiceUtil.getSql(sqlKey);
   }
}