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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
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

   private static final String GET_EXISTING_ATTRIBUTE_IDS =
         "SELECT DISTINCT atr1.attr_id FROM osee_define_attribute atr1, osee_define_attribute_type aty1 WHERE atr1.art_id = ? AND atr1.attr_type_id = ?";

   private final Artifact artifact;
   private final SkynetTransaction transaction;

   public AttributeToTransactionOperation(final Artifact artifact, final SkynetTransaction transaction) {
      this.artifact = artifact;
      this.transaction = transaction;
   }

   public void execute() throws OseeCoreException, SQLException {
      for (Attribute<?> attribute : artifact.internalGetAttributes()) {
         if (attribute.isDirty()) {
            if (attribute.isDeleted()) {
               deleteAttribute(attribute, transaction, artifact);
            } else {
               addAttributeData(artifact, attribute, transaction);
            }
         }
      }
   }

   private void addAttributeData(Artifact artifact, Attribute<?> attribute, SkynetTransaction transaction) throws OseeCoreException, SQLException {
      if (artifact.isVersionControlled()) {
         versionControlled(artifact, attribute, transaction);
      } else {
         nonVersionControlled(artifact, attribute, transaction);
      }
   }

   //TODO
   private void versionControlled(Artifact artifact, Attribute<?> attribute, SkynetTransaction transaction) throws OseeCoreException, SQLException {
      ModType modType = null;
      ModificationType attrModType = null;
      if (attribute.isInDatastore()) {
         attribute.setGammaId(SkynetDatabase.getNextGammaId());

         modType = ModType.Changed;
         attrModType = ModificationType.CHANGE;
      } else {
         createNewAttributeMemo(attribute);
         attrModType = ModificationType.NEW;
         modType = ModType.Added;
      }
      attribute.getAttributeDataProvider().persist();
      DAOToSQL daoToSql = new DAOToSQL(attribute.getAttributeDataProvider().getData());
      transaction.addTransactionDataItem(createAttributeTxData(artifact, attribute, daoToSql, transaction, attrModType));
      transaction.addLocalEvent(new TransactionArtifactModifiedEvent(artifact, modType, artifact));
   }

   private void nonVersionControlled(Artifact artifact, Attribute<?> attribute, SkynetTransaction transaction) throws OseeCoreException, SQLException {
      IAttributeDataProvider dataProvider = attribute.getAttributeDataProvider();
      if (!attribute.isInDatastore()) {
         createNewAttributeMemo(attribute);
         dataProvider.persist();
         DAOToSQL daoToSql = new DAOToSQL(dataProvider.getData());
         transaction.addTransactionDataItem(createAttributeTxData(artifact, attribute, daoToSql, transaction,
               ModificationType.NEW));
      } else {
         dataProvider.persist();
         DAOToSQL daoToSql = new DAOToSQL(dataProvider.getData());
         transaction.addToBatch(UPDATE_TRANSACTION_TABLE, SQL3DataType.VARCHAR, transaction.getComment(),
               SQL3DataType.TIMESTAMP, GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.INTEGER,
               attribute.getGammaId(), SQL3DataType.INTEGER, artifact.getBranch().getBranchId());

         transaction.addToBatch(UPDATE_ATTRIBUTE, SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER,
               attribute.getAttrId(), SQL3DataType.INTEGER, attribute.getAttributeType().getAttrTypeId(),
               SQL3DataType.INTEGER, attribute.getGammaId(), SQL3DataType.VARCHAR, daoToSql.getValue(),
               SQL3DataType.VARCHAR, daoToSql.getUri());
      }
   }

   private AttributeTransactionData createAttributeTxData(Artifact artifact, Attribute<?> attribute, DAOToSQL dao, SkynetTransaction transaction, ModificationType attrModType) throws OseeCoreException {
      return new AttributeTransactionData(artifact.getArtId(), attribute.getAttrId(),
            attribute.getAttributeType().getAttrTypeId(), dao.getValue(), attribute.getGammaId(),
            transaction.getTransactionNumber(), dao.getUri(), attrModType, transaction.getBranch());
   }

   private void createNewAttributeMemo(Attribute<?> attribute) throws SQLException {
      if (attribute == null) return;
      ConnectionHandlerStatement connectionHandlerStatement = null;
      AttributeType attributeType = attribute.getAttributeType();
      int attrId = -1;
      //TODO: Only handles a max Occurrence of 1
      if (attributeType.getMaxOccurrences() == 1) {
         try {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(GET_EXISTING_ATTRIBUTE_IDS, SQL3DataType.INTEGER,
                        artifact.getArtId(), SQL3DataType.INTEGER, attributeType.getAttrTypeId());

            ResultSet resultSet = connectionHandlerStatement.getRset();

            if (resultSet.next()) {
               attrId = (resultSet.getInt("attr_id"));
            }
         } finally {
            DbUtil.close(connectionHandlerStatement);
         }
      }
      int gammaId = SkynetDatabase.getNextGammaId();
      if (attrId < 1) {
         attrId = Query.getNextSeqVal(SkynetDatabase.ATTR_ID_SEQ);
      }
      attribute.setIds(attrId, gammaId);
   }

   /**
    * Remove an attribute from the database that is represented by a particular persistence memo that the persistence
    * layer marked it with. The persistence memo is used for this since it is the identifying information the
    * persistence layer needs, allowing the attribute to be destroyed and released back to the system.
    * 
    * @throws SQLException
    */
   private void deleteAttribute(Attribute<?> attribute, SkynetTransaction transaction, Artifact artifact) throws SQLException {
      if (!attribute.isInDatastore()) return;

      int gammaId = SkynetDatabase.getNextGammaId();
      transaction.addTransactionDataItem(new AttributeTransactionData(artifact.getArtId(), attribute.getAttrId(),
            attribute.getAttributeType().getAttrTypeId(), null, gammaId, transaction.getTransactionNumber(), null,
            ModificationType.DELETED, transaction.getBranch()));

      transaction.addLocalEvent(new CacheArtifactModifiedEvent(artifact, ModType.Changed, this));
   }

   public static void meetMinimumAttributeCounts(Artifact artifact, boolean isNewArtifact) throws OseeDataStoreException {
      try {
         for (AttributeType attributeType : artifact.getAttributeTypes()) {
            int missingCount = attributeType.getMinOccurrences() - artifact.getAttributeCount(attributeType.getName());
            for (int i = 0; i < missingCount; i++) {
               Attribute<?> attribute = artifact.createAttribute(attributeType, true);
               if (!isNewArtifact) {
                  attribute.setNotDirty();
                  OseeLog.log(SkynetActivator.class, Level.FINER, String.format("%s [%d]- %s was created",
                        artifact.toString(), artifact.getArtId(), attributeType.toString()));
               }
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex.getMessage(), ex);
      }
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

   public static void initializeAttribute(Artifact artifact, int atttributeTypeId, String value, String uri, int attributeId, int gamma_id) {
      try {
         AttributeType attributeType = AttributeTypeManager.getType(atttributeTypeId);
         attributeType = AttributeTypeManager.getTypeWithWordContentCheck(artifact, attributeType.getName());

         Attribute<?> attribute = artifact.createAttribute(attributeType, false);
         attribute.getAttributeDataProvider().loadData(value, uri);
         attribute.setIds(attributeId, gamma_id);
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }
}