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

import static org.eclipse.osee.framework.skynet.core.artifact.LoadLevel.RELATION;
import static org.eclipse.osee.framework.skynet.core.artifact.LoadLevel.SHALLOW;
import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;

/**
 * @author Ryan Schmitt
 */
public class AttributeLoader {

   static void loadAttributeData(int queryId, Collection<Artifact> artifacts, boolean historical, DeletionFlag allowDeletedArtifacts, LoadLevel loadLevel) throws OseeCoreException {
      if (loadLevel == SHALLOW || loadLevel == RELATION) {
         return;
      }

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         String sql = getSql(allowDeletedArtifacts, loadLevel, historical);
         chStmt.runPreparedQuery(artifacts.size() * 8, sql, queryId);

         Artifact currentArtifact = null;
         AttrData previousAttr = new AttrData();
         List<AttrData> currentAttributes = new ArrayList<AttrData>();

         while (chStmt.next()) {
            AttrData nextAttr = new AttrData(chStmt, historical);

            if (AttrData.isDifferentArtifact(previousAttr, nextAttr)) {
               loadAttributesFor(currentArtifact, currentAttributes, historical);
               currentAttributes.clear();
               currentArtifact = getArtifact(nextAttr, historical);
            }

            currentAttributes.add(nextAttr);
            previousAttr = nextAttr;
         }
         loadAttributesFor(currentArtifact, currentAttributes, historical);
      } finally {
         chStmt.close();
      }
   }

   private static final class AttrData {
      public int artifactId = -1;
      public int branchId = -1;
      public int attrId = -1;
      public int gammaId = -1;
      public int modType = -1;
      public int transactionId = -1;
      public int attrTypeId = -1;
      public String value = "";
      public int stripeId = -1;
      public String uri = "";

      public AttrData() {
      }

      public AttrData(IOseeStatement chStmt, boolean historical) throws OseeDataStoreException {
         artifactId = chStmt.getInt("art_id");
         branchId = chStmt.getInt("branch_id");
         attrId = chStmt.getInt("attr_id");
         gammaId = chStmt.getInt("gamma_id");
         modType = chStmt.getInt("mod_type");

         transactionId = chStmt.getInt("transaction_id");
         attrTypeId = chStmt.getInt("attr_type_id");
         value = chStmt.getString("value");
         if (historical) {
            stripeId = chStmt.getInt("stripe_transaction_id");
         }
         uri = chStmt.getString("uri");
      }

      public static boolean isDifferentArtifact(AttrData previous, AttrData current) {
         return current.branchId != previous.branchId || current.artifactId != previous.artifactId;
      }

      public static boolean multipleVersionsExist(AttrData current, AttrData previous) {
         return current.attrId == previous.attrId && current.branchId == previous.branchId && current.artifactId == previous.artifactId;
      }
   }

   private static Artifact getArtifact(AttrData current, boolean historical) {
      Artifact artifact = null;
      if (historical) {
         artifact = ArtifactCache.getHistorical(current.artifactId, current.stripeId);
      } else {
         artifact = ArtifactCache.getActive(current.artifactId, current.branchId);
      }
      if (artifact == null) {
         OseeLog.log(ArtifactLoader.class, Level.WARNING, String.format(
               "Orphaned attribute for artifact id[%d] branch[%d]", current.artifactId, current.branchId));
      }
      return artifact;
   }

   private static void loadAttributesFor(Artifact artifact, List<AttrData> attributes, boolean historical) throws OseeCoreException {
      if (artifact == null) {
         return; // If the artifact is null, it means the attributes are orphaned.
      }
      List<Integer> transactionNumbers = new ArrayList<Integer>();
      AttrData previous = new AttrData();
      synchronized (artifact) {
         if (!artifact.isAttributesLoaded()) {
            for (AttrData current : attributes) {
               if (AttrData.multipleVersionsExist(current, previous)) {
                  handleMultipleVersions(previous, current, historical);
               } else {
                  loadAttribute(artifact, current, previous);
                  transactionNumbers.add(current.transactionId);
               }
               previous = current;
            }
            setLastAttributePersistTransaction(artifact, transactionNumbers);
            artifact.meetMinimumAttributeCounts(false);
            ArtifactCache.cachePostAttributeLoad(artifact);
         }
      }
   }

   private static void handleMultipleVersions(AttrData previous, AttrData current, boolean historical) {
      // Do not warn about skipping on historical loading, because the most recent
      // transaction is used first due to sorting on the query
      if (!historical) {
         OseeLog.log(
               ArtifactLoader.class,
               Level.WARNING,
               String.format(
                     "multiple attribute version for attribute id [%d] artifact id[%d] branch[%d] previousGammaId[%s] currentGammaId[%s] previousModType[%s] currentModType[%s]",
                     current.attrId, current.artifactId, current.branchId, previous.gammaId, current.gammaId,
                     previous.modType, current.modType));
      }
   }

   private static void loadAttribute(Artifact artifact, AttrData current, AttrData previous) throws OseeCoreException {
      IAttributeType attributeType = AttributeTypeManager.getType(current.attrTypeId);
      String value = current.value;
      if (isEnumOrBoolean(attributeType)) {
         value = Strings.intern(value);
      }
      boolean markDirty = false;
      artifact.internalInitializeAttribute(attributeType, current.attrId, current.gammaId,
            ModificationType.getMod(current.modType), markDirty, value, current.uri);
   }

   private static boolean isEnumOrBoolean(IAttributeType attributeType) throws OseeCoreException {
      boolean isBooleanAttribute = AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType);
      boolean isEnumAttribute = AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType);
      return isBooleanAttribute || isEnumAttribute;
   }

   private static void setLastAttributePersistTransaction(Artifact artifact, List<Integer> transactionNumbers) {
      int maxTransactionId = Integer.MIN_VALUE;
      for (Integer transactionId : transactionNumbers) {
         maxTransactionId = Math.max(maxTransactionId, transactionId);
      }
      artifact.setTransactionId(maxTransactionId);
   }

   private static String getSql(DeletionFlag allowDeletedArtifacts, LoadLevel loadLevel, boolean historical) throws OseeCoreException {
      OseeSql sqlKey;
      if (historical) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ATTRIBUTES;
      } else if (loadLevel == LoadLevel.ALL_CURRENT) {
         sqlKey = OseeSql.LOAD_ALL_CURRENT_ATTRIBUTES;
      } else if (allowDeletedArtifacts == INCLUDE_DELETED) {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ATTRIBUTES;
      }

      return ClientSessionManager.getSql(sqlKey);
   }
}