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
package org.eclipse.osee.framework.skynet.core.attribute;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_ATTRIBUTES_TABLE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;

/**
 * Caches the mapping of valid attribute types to artifact subtypes for which they are valid
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Ryan D. Brooks
 */
public class AttributeTypeValidityCache {

   private static final LocalAliasTable VAL_ATTR_ALIAS_1 = new LocalAliasTable(VALID_ATTRIBUTES_TABLE, "t1");
   private static final LocalAliasTable VAL_ATTR_ALIAS_2 = new LocalAliasTable(VALID_ATTRIBUTES_TABLE, "t2");

   private static final String attributeValiditySql = "SELECT " + VAL_ATTR_ALIAS_1.columns("art_type_id",
         "attr_type_id") + " FROM " + VAL_ATTR_ALIAS_1 + "," + TRANSACTIONS_TABLE

   + " WHERE " + VAL_ATTR_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + VAL_ATTR_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + VAL_ATTR_ALIAS_2.column("attr_type_id") + "=" + VAL_ATTR_ALIAS_1.column("attr_type_id") + " AND " + VAL_ATTR_ALIAS_2.column("art_type_id") + "=" + VAL_ATTR_ALIAS_1.column("art_type_id") + " AND " + VAL_ATTR_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=" + "?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?)";

   private final DoubleKeyHashMap<TransactionId, ArtifactSubtypeDescriptor, List<DynamicAttributeDescriptor>> validityMap;

   public AttributeTypeValidityCache() {
      super();
      validityMap = new DoubleKeyHashMap<TransactionId, ArtifactSubtypeDescriptor, List<DynamicAttributeDescriptor>>();
   }

   public Collection<DynamicAttributeDescriptor> getValidAttributeDescriptors(ArtifactSubtypeDescriptor artifactType) throws SQLException {
      TransactionId transactionId = artifactType.getTransactionId();
      ensurePopulated(transactionId);

      Collection<DynamicAttributeDescriptor> validDescriptors = validityMap.get(transactionId, artifactType);
      if (validDescriptors == null) {
         validDescriptors = new ArrayList<DynamicAttributeDescriptor>();
      }

      return validDescriptors;
   }

   private synchronized void ensurePopulated(TransactionId transactionId) throws SQLException {
      if (validityMap.getSubHash(transactionId) == null) {
         populateCache(transactionId);
      }
   }

   private void populateCache(TransactionId transactionId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(attributeValiditySql, SQL3DataType.INTEGER,
               transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER, transactionId.getTransactionNumber());

         ResultSet rSet = chStmt.getRset();
         ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();
         HashCollection<ArtifactSubtypeDescriptor, DynamicAttributeDescriptor> map = new HashCollection<ArtifactSubtypeDescriptor, DynamicAttributeDescriptor>(
               100);

         while (rSet.next()) {
            try {
               ArtifactSubtypeDescriptor artifactType = configurationManager.getArtifactSubtypeDescriptor(
                     rSet.getInt("art_type_id"), transactionId);
               DynamicAttributeDescriptor attributeType = configurationManager.getDynamicAttributeType(
                     rSet.getInt("attr_type_id"), transactionId);

               map.put(artifactType, attributeType);
            } catch (IllegalArgumentException ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }

         for (ArtifactSubtypeDescriptor artifactType : map.keySet()) {
            Collection<DynamicAttributeDescriptor> attributeTypes = map.getValues(artifactType);
            List<DynamicAttributeDescriptor> typeList = new ArrayList<DynamicAttributeDescriptor>(attributeTypes.size());
            ;
            typeList.addAll(attributeTypes);
            Collections.sort(typeList);
            validityMap.put(transactionId, artifactType, typeList);
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public Collection<ArtifactSubtypeDescriptor> getArtifactSubtypeDescriptorsForAttribute(DynamicAttributeDescriptor requestedAttributeType) throws SQLException {
      TransactionId transactionId = requestedAttributeType.getTransactionId();
      ensurePopulated(transactionId);
      Collection<ArtifactSubtypeDescriptor> artifactTypes = new ArrayList<ArtifactSubtypeDescriptor>();
      Map<ArtifactSubtypeDescriptor, List<DynamicAttributeDescriptor>> map = validityMap.getSubHash(transactionId);

      for (Entry<ArtifactSubtypeDescriptor, List<DynamicAttributeDescriptor>> entry : map.entrySet()) {
         ArtifactSubtypeDescriptor artifactType = entry.getKey();
         for (DynamicAttributeDescriptor attributeType : entry.getValue()) {
            if (requestedAttributeType.equals(attributeType)) {
               artifactTypes.add(artifactType);
               break;
            }
         }
      }

      return artifactTypes;
   }

   /**
    * @param attributeType
    * @param artifactType
    * @throws SQLException
    */
   public void add(DynamicAttributeDescriptor attributeType, ArtifactSubtypeDescriptor artifactType) throws SQLException {
      TransactionId transactionId = artifactType.getTransactionId();
      ensurePopulated(transactionId);

      List<DynamicAttributeDescriptor> typeList = validityMap.get(transactionId, artifactType);
      if (typeList == null) {
         typeList = new ArrayList<DynamicAttributeDescriptor>();
         validityMap.put(transactionId, artifactType, typeList);
      }
      typeList.add(attributeType);
      Collections.sort(typeList);
   }
}