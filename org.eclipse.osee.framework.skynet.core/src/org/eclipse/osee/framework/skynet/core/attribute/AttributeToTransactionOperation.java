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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.data.AttributeTransactionData;

/**
 * This class is responsible for persisting an attribute for a particular artifact. Upon completion the attribute will
 * be marked as not dirty.
 * 
 * @author Roberto E. Escobar
 */
public class AttributeToTransactionOperation {

   private static final String UPDATE_TRANSACTION_TABLE =
         " UPDATE " + TRANSACTION_DETAIL_TABLE + " SET " + TRANSACTION_DETAIL_TABLE.column(TXD_COMMENT) + " = ?, TIME = ? WHERE transaction_id = (SELECT transaction_id FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id = ? AND branch_id = ?)";

   private static final String UPDATE_ATTRIBUTE =
         "UPDATE " + ATTRIBUTE_VERSION_TABLE + " SET value = ?, uri = ? WHERE art_id = ? and attr_id = ? and attr_type_id = ? and gamma_id = ?";

   private final Artifact artifact;
   private final SkynetTransaction transaction;

   public AttributeToTransactionOperation(final Artifact artifact, final SkynetTransaction transaction) {
      this.artifact = artifact;
      this.transaction = transaction;
   }

   public void execute() throws Exception {
      Collection<DynamicAttributeManager> managers = artifact.getAttributeManagers();
      for (DynamicAttributeManager attributeManager : managers) {
         for (Attribute<?> attribute : attributeManager.getAttributes()) {
            if (attribute.isDirty()) {
               IDataAccessObject dao = attributeManager.getDataAccessObjectFor(attribute);
               if (dao != null) {
                  addAttributeData(artifact, attribute, dao, transaction);
               }
            }
         }

         for (Attribute<?> attribute : attributeManager.getDeletedAttributes()) {
            deleteAttribute(attribute, transaction, artifact);
         }
      }
   }

   private void addAttributeData(Artifact artifact, Attribute attribute, IDataAccessObject dao, SkynetTransaction transaction) throws Exception {
      if (artifact.isVersionControlled()) {
         versionControlled(artifact, attribute, dao, transaction);
      } else {
         nonVersionControlled(artifact, attribute, dao, transaction);
      }
   }

   private void versionControlled(Artifact artifact, Attribute attribute, IDataAccessObject dao, SkynetTransaction transaction) throws Exception {
      AttributeMemo memo = null;
      ModType modType = null;
      ModificationType attrModType = null;
      if (attribute.getPersistenceMemo() == null) {
         memo = createNewAttributeMemo(attribute);
         attrModType = ModificationType.NEW;
         modType = ModType.Added;
      } else {
         memo = attribute.getPersistenceMemo();
         memo = new AttributeMemo(memo.getAttrId(), SkynetDatabase.getNextGammaId());
         attribute.setPersistenceMemo(memo);

         modType = ModType.Changed;
         attrModType = ModificationType.CHANGE;
      }
      dao.persist();
      DAOToSQL daoToSql = new DAOToSQL(dao.getData());
      transaction.addTransactionDataItem(createAttributeTxData(artifact, attribute, daoToSql, transaction, attrModType));
      transaction.addLocalEvent(new TransactionArtifactModifiedEvent(artifact, modType, artifact));
   }

   private void nonVersionControlled(Artifact artifact, Attribute attribute, IDataAccessObject dao, SkynetTransaction transaction) throws Exception {
      AttributeMemo memo = null;
      if (attribute.getPersistenceMemo() == null) {
         memo = createNewAttributeMemo(attribute);
         dao.persist();
         DAOToSQL daoToSql = new DAOToSQL(dao.getData());
         transaction.addTransactionDataItem(createAttributeTxData(artifact, attribute, daoToSql, transaction,
               ModificationType.NEW));
      } else {
         memo = attribute.getPersistenceMemo();
         dao.persist();
         DAOToSQL daoToSql = new DAOToSQL(dao.getData());
         transaction.addToBatch(UPDATE_TRANSACTION_TABLE, SQL3DataType.VARCHAR, transaction.getComment(),
               SQL3DataType.TIMESTAMP, GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.INTEGER,
               attribute.getPersistenceMemo().getGammaId(), SQL3DataType.INTEGER, artifact.getBranch().getBranchId());

         transaction.addToBatch(UPDATE_ATTRIBUTE, SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER,
               memo.getAttrId(), SQL3DataType.INTEGER, attribute.getTypeId(), SQL3DataType.INTEGER, memo.getGammaId(),
               SQL3DataType.VARCHAR, daoToSql.getValue(), SQL3DataType.VARCHAR, daoToSql.getUri());
      }
   }

   private AttributeTransactionData createAttributeTxData(Artifact artifact, Attribute attribute, DAOToSQL dao, SkynetTransaction transaction, ModificationType attrModType) throws Exception {
      AttributeMemo memo = attribute.getPersistenceMemo();
      return new AttributeTransactionData(artifact.getArtId(), memo.getAttrId(), attribute.getTypeId(), dao.getValue(),
            memo.getGammaId(), transaction.getTransactionNumber(), dao.getUri(), attrModType, transaction.getBranch());
   }

   private AttributeMemo createNewAttributeMemo(Attribute<?> attribute) throws SQLException {
      int gammaId = SkynetDatabase.getNextGammaId();
      int attrId = Query.getNextSeqVal(null, SkynetDatabase.ATTR_ID_SEQ);

      AttributeMemo memo = new AttributeMemo(attrId, gammaId);
      attribute.setPersistenceMemo(memo);
      return memo;
   }

   /**
    * Remove an attribute from the database that is represented by a particular persistence memo that the persistence
    * layer marked it with. The persistence memo is used for this since it is the identifying information the
    * persistence layer needs, allowing the attribute to be destroyed and released back to the system.
    * 
    * @throws SQLException
    */
   private void deleteAttribute(Attribute<?> attribute, SkynetTransaction transaction, Artifact artifact) throws SQLException {
      // If the memo is null, the attribute was never saved to the database, so we can ignore it
      AttributeMemo memo = attribute.getPersistenceMemo();
      if (memo == null) return;

      int gammaId = SkynetDatabase.getNextGammaId();
      transaction.addTransactionDataItem(new AttributeTransactionData(artifact.getArtId(), memo.getAttrId(),
            attribute.getAttributeType().getAttrTypeId(), null, gammaId, transaction.getTransactionNumber(), null,
            ModificationType.DELETED, transaction.getBranch()));

      transaction.addLocalEvent(new CacheArtifactModifiedEvent(artifact, ModType.Changed, this));
   }

   private final class DAOToSQL {
      private String uri;
      private String value;

      public DAOToSQL(Object... data) {
         this.uri = getItemAt(1, data);
         this.value = getItemAt(0, data);
      }

      private String getItemAt(int index, Object... data) {
         String toReturn = null;
         if (data != null && data.length > index) {
            Object obj = data[index];
            if (obj != null) {
               toReturn = obj.toString();
            }
         }
         return toReturn;
      }

      public String getUri() {
         return uri != null ? uri : "";
      }

      public String getValue() {
         return value != null ? value : "";
      }
   }
}
