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

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_BASE_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;

/**
 * Caches artifact subtype descriptors.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Robert A. Fisher
 */
public class DynamicAttributeDescriptorCache {
   private static final String SELECT_ATTRIBUTE_TYPES =
         "SELECT " + ATTRIBUTE_BASE_TYPE_TABLE.column("attribute_class") + ", " + ATTRIBUTE_TYPE_TABLE.columns(
               "attr_type_id", "name", "default_value", "validity_xml", "min_occurence", "max_occurence", "tip_text") + " FROM " + ATTRIBUTE_TYPE_TABLE + ", " + ATTRIBUTE_BASE_TYPE_TABLE + "," + TRANSACTIONS_TABLE + ", (SELECT " + TRANSACTION_DETAIL_TABLE.max(
               "transaction_id", "transaction_id") + ", " + ATTRIBUTE_TYPE_TABLE.columns("attr_type_id") + " FROM " + ATTRIBUTE_TYPE_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_TYPE_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=" + "?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " GROUP BY " + ATTRIBUTE_TYPE_TABLE.columns("attr_type_id") + ") T1 "

         + " WHERE " + ATTRIBUTE_BASE_TYPE_TABLE.column("attr_base_type_id") + EQUAL + ATTRIBUTE_TYPE_TABLE.column("attr_base_type_id") + " AND " + ATTRIBUTE_TYPE_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=T1.transaction_id" + " AND " + ATTRIBUTE_TYPE_TABLE.column("attr_type_id") + "=T1.attr_type_id";

   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private final HashCollection<TransactionId, DynamicAttributeDescriptor> allDescriptors;
   private final DoubleKeyHashMap<Integer, TransactionId, DynamicAttributeDescriptor> idToDescriptors;
   private final DoubleKeyHashMap<String, TransactionId, DynamicAttributeDescriptor> nameToDescriptors;
   private final HashSet<TransactionId> populated;

   protected DynamicAttributeDescriptorCache() {
      this.allDescriptors = new HashCollection<TransactionId, DynamicAttributeDescriptor>();
      this.idToDescriptors = new DoubleKeyHashMap<Integer, TransactionId, DynamicAttributeDescriptor>();
      this.nameToDescriptors = new DoubleKeyHashMap<String, TransactionId, DynamicAttributeDescriptor>();
      this.populated = new HashSet<TransactionId>();
   }

   private synchronized void ensurePopulated(TransactionId transactionId) {
      if (!populated.contains(transactionId)) {
         populated.add(transactionId);
         populateCache(transactionId);
      }
   }

   private void populateCache(TransactionId transactionId) {
      try {
         int currentTransactionNumber =
               transactionIdManager.getEditableTransactionId(transactionId.getBranch()).getTransactionNumber();
         // don't populate the cache using a transaction number that is not yet in the DB
         int transactionNumber =
               transactionId.getTransactionNumber() > currentTransactionNumber ? currentTransactionNumber : transactionId.getTransactionNumber();

         Collection<DynamicAttributeDescriptor> savedDescriptors = new LinkedList<DynamicAttributeDescriptor>();
         Query.acquireCollection(savedDescriptors, new DynamicAttributeTypeProcessor(transactionId),
               SELECT_ATTRIBUTE_TYPES, SQL3DataType.INTEGER, transactionId.getBranch().getBranchId(),
               SQL3DataType.INTEGER, transactionNumber);

         for (DynamicAttributeDescriptor descriptor : savedDescriptors) {
            cache(descriptor);
         }
      } catch (SQLException ex) {
         throw new IllegalStateException(ex);
      }
   }

   /**
    * @return Returns all of the descriptors.
    */
   public Collection<DynamicAttributeDescriptor> getAllDescriptors(Branch branch) {
      return getAllDescriptors(transactionIdManager.getEditableTransactionId(branch));
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public DynamicAttributeDescriptor getDescriptor(String name, Branch branch) {
      return getDescriptor(name, transactionIdManager.getEditableTransactionId(branch));
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public DynamicAttributeDescriptor getDescriptor(int attrTypeId, Branch branch) {
      return getDescriptor(attrTypeId, transactionIdManager.getEditableTransactionId(branch));
   }

   /**
    * @return Returns all of the descriptors.
    */
   public Collection<DynamicAttributeDescriptor> getAllDescriptors(TransactionId transactionId) {
      ensurePopulated(transactionId);
      return new ArrayList<DynamicAttributeDescriptor>(allDescriptors.getValues(transactionId));
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public DynamicAttributeDescriptor getDescriptor(String name, TransactionId transactionId) {
      ensurePopulated(transactionId);
      DynamicAttributeDescriptor descriptor = nameToDescriptors.get(name, transactionId);

      if (descriptor == null) {
         throw new IllegalArgumentException(
               "Attribute Descriptor does not exist for attribute type name: \"" + name + "\" for transaction id: " + transactionId);
      }
      return descriptor;
   }

   public boolean hasDescriptor(String name, Branch branch) {
      return hasDescriptor(name, transactionIdManager.getEditableTransactionId(branch));
   }

   public boolean hasDescriptor(String name, TransactionId transactionId) {
      ensurePopulated(transactionId);
      return nameToDescriptors.get(name, transactionId) != null;
   }

   /**
    * @return Returns the descriptor with a particular id, null if it does not exist.
    */
   public DynamicAttributeDescriptor getDescriptor(int attrTypeId, TransactionId transactionId) {
      ensurePopulated(transactionId);
      DynamicAttributeDescriptor descriptor = idToDescriptors.get(attrTypeId, transactionId);
      if (descriptor == null) {
         throw new IllegalArgumentException(
               "Attribute Descriptor does not exist for attribute type id: " + attrTypeId + " and transaction id: " + transactionId);
      }
      return descriptor;
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws IllegalArgumentException if descriptor is null.
    */
   public void cache(DynamicAttributeDescriptor descriptor) {
      ensurePopulated(descriptor.getTransactionId());
      if (descriptor == null) throw new IllegalArgumentException("The descriptor parameter can not be null");

      allDescriptors.put(descriptor.getTransactionId(), descriptor);
      nameToDescriptors.put(descriptor.getName(), descriptor.getTransactionId(), descriptor);
      idToDescriptors.put(descriptor.getAttrTypeId(), descriptor.getTransactionId(), descriptor);
   }
}