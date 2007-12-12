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
package org.eclipse.osee.framework.skynet.core.relation;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_RELATIONS_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;

/**
 * Caches artifact subtype descriptors.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor
 * @author Robert A. Fisher
 */
public class IRelationLinkDescriptorCache {
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private final HashCollection<TransactionId, IRelationLinkDescriptor> allDescriptors;
   private final DoubleKeyHashMap<Integer, TransactionId, IRelationLinkDescriptor> idToDescriptors;
   private final DoubleKeyHashMap<String, TransactionId, IRelationLinkDescriptor> nameToDescriptors;
   private final Map<TransactionId, Object> populated;

   private static final LocalAliasTable LINK_TYPE_ALIAS_1 = new LocalAliasTable(RELATION_LINK_TYPE_TABLE, "t1");
   private static final LocalAliasTable LINK_TYPE_ALIAS_2 = new LocalAliasTable(RELATION_LINK_TYPE_TABLE, "t2");
   private static final String SELECT_LINK_TYPES =
         "SELECT " + LINK_TYPE_ALIAS_1.columns("type_name", "a_name", "b_name", "ab_phrasing", "ba_phrasing",
               "short_name", "rel_link_type_id") + " FROM " + LINK_TYPE_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + LINK_TYPE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id")
         // TODO the RELATION_LINK_TYPE_TABLE does not have a modification_id to mark deleted types
         //   + " AND " + LINK_TYPE_ALIAS_1.column("modification_id") + "<>?"
         + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + LINK_TYPE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_TYPE_ALIAS_2.column("rel_link_type_id") + "=" + LINK_TYPE_ALIAS_1.column("rel_link_type_id") + " AND " + LINK_TYPE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?)";

   // TODO this does not handle deleted validities
   private static final LocalAliasTable VALIDITY_ALIAS_1 = new LocalAliasTable(VALID_RELATIONS_TABLE, "t1");
   private static final LocalAliasTable VALIDITY_ALIAS_2 = new LocalAliasTable(VALID_RELATIONS_TABLE, "t2");
   private static final String SELECT_LINK_VALIDITY =
         "SELECT /*+ ordered */" + VALIDITY_ALIAS_1.columns("rel_link_type_id", "art_type_id", "side_a_max",
               "side_b_max") + " FROM " + VALIDITY_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + VALIDITY_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id")
         // TODO the VALID_RELATIONS_TABLE does not have a modification_id to mark deleted validity
         // + " AND " + VALIDITY_ALIAS_1.column("modification_id") + "<>?"
         + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + VALIDITY_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + VALIDITY_ALIAS_2.column("art_type_id") + "=" + VALIDITY_ALIAS_1.column("art_type_id") + " AND " + VALIDITY_ALIAS_2.column("rel_link_type_id") + "=" + VALIDITY_ALIAS_1.column("rel_link_type_id") + " AND " + VALIDITY_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=" + "?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?)" + " ORDER BY " + VALIDITY_ALIAS_1.column("rel_link_type_id");

   /**
    * 
    */
   protected IRelationLinkDescriptorCache() {
      this.allDescriptors = new HashCollection<TransactionId, IRelationLinkDescriptor>();
      this.idToDescriptors = new DoubleKeyHashMap<Integer, TransactionId, IRelationLinkDescriptor>();
      this.nameToDescriptors = new DoubleKeyHashMap<String, TransactionId, IRelationLinkDescriptor>();
      this.populated = new HashMap<TransactionId, Object>();
   }

   private void checkPopulated(TransactionId transactionId) {
      if (!populated.containsKey(transactionId)) {
         populated.put(transactionId, null);
         populateCache(transactionId);
      }
   }

   private void populateCache(TransactionId transactionId) {
      try {
         Collection<IRelationLinkDescriptor> savedDescriptors = new LinkedList<IRelationLinkDescriptor>();

         Query.acquireCollection(savedDescriptors, new LinkDescriptorProcessor(transactionId), SELECT_LINK_TYPES,
               SQL3DataType.INTEGER, transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
               transactionId.getTransactionNumber());

         for (IRelationLinkDescriptor descriptor : savedDescriptors) {
            cache(descriptor);
         }

         loadLinkValidities(transactionId);
      } catch (SQLException ex) {
         throw new IllegalStateException(ex);
      }
   }

   private void loadLinkValidities(TransactionId transactionId) {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(2000, SELECT_LINK_VALIDITY, SQL3DataType.INTEGER,
                     transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
                     transactionId.getTransactionNumber());
         ResultSet rset = chStmt.getRset();
         IRelationLinkDescriptor descriptor = null;

         while (rset.next()) {
            if (descriptor == null || descriptor.getPersistenceMemo().getLinkTypeId() != rset.getInt("rel_link_type_id")) {
               descriptor = getDescriptor(rset.getInt("rel_link_type_id"), transactionId);
            }
            descriptor.setLinkSideRestriction(rset.getInt("art_type_id"), new LinkSideRestriction(
                  rset.getInt("side_a_max"), rset.getInt("side_b_max")));
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * @return Returns all of the descriptors.
    */
   public Collection<IRelationLinkDescriptor> getAllDescriptors(Branch branch) {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);
      return getAllDescriptors(transactionId);
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public IRelationLinkDescriptor getDescriptor(String name, Branch branch) {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);
      return getDescriptor(name, transactionId);
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public IRelationLinkDescriptor getDescriptor(int id, Branch branch) {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);
      return getDescriptor(id, transactionId);
   }

   /**
    * @return Returns all of the descriptors.
    */
   public Collection<IRelationLinkDescriptor> getAllDescriptors(TransactionId transactionId) {
      checkPopulated(transactionId);
      Collection<IRelationLinkDescriptor> descriptors = allDescriptors.getValues(transactionId);
      if (descriptors == null) {
         return new ArrayList<IRelationLinkDescriptor>(0);
      } else {
         return new ArrayList<IRelationLinkDescriptor>(descriptors);
      }
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public IRelationLinkDescriptor getDescriptor(String name, TransactionId transactionId) {
      checkPopulated(transactionId);
      return nameToDescriptors.get(name, transactionId);
   }

   /**
    * @return Returns the descriptor with a particular id, null if it does not exist.
    */
   public IRelationLinkDescriptor getDescriptor(int id, TransactionId transactionId) {
      checkPopulated(transactionId);
      return idToDescriptors.get(id, transactionId);
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws IllegalArgumentException if descriptor is null.
    */
   public void cache(IRelationLinkDescriptor descriptor) {
      checkPopulated(descriptor.getTransactionId());
      if (descriptor == null) throw new IllegalArgumentException("The descriptor parameter can not be null");

      allDescriptors.put(descriptor.getTransactionId(), descriptor);
      idToDescriptors.put(descriptor.getPersistenceMemo().getLinkTypeId(), descriptor.getTransactionId(), descriptor);
      nameToDescriptors.put(descriptor.getName(), descriptor.getTransactionId(), descriptor);
   }

   private class LinkDescriptorProcessor implements RsetProcessor<IRelationLinkDescriptor> {

      private TransactionId transactionId;

      /**
       * @param transactionId
       */
      public LinkDescriptorProcessor(TransactionId transactionId) {
         this.transactionId = transactionId;
      }

      public IRelationLinkDescriptor process(ResultSet rset) throws SQLException {
         IRelationLinkDescriptor descriptor =
               new DynamicRelationLinkDescriptor(rset.getString("type_name"), rset.getString("a_name"),
                     rset.getString("b_name"), rset.getString("ab_phrasing"), rset.getString("ba_phrasing"),
                     rset.getString("short_name"), transactionId);

         descriptor.setPersistenceMemo(new LinkDescriptorPersistenceMemo(rset.getInt("rel_link_type_id")));

         return descriptor;
      }

      public boolean validate(IRelationLinkDescriptor item) {
         return item != null;
      }

   }
}
