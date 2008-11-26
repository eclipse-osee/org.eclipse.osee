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

import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * This class is responsible for persisting an attribute for a particular artifact. Upon completion the attribute will
 * be marked as not dirty.
 * 
 * @author Roberto E. Escobar
 */
public class AttributeToTransactionOperation {
   private static final String GET_EXISTING_ATTRIBUTE_IDS =
         "SELECT att1.attr_id FROM osee_attribute att1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE att1.attr_type_id = ? AND att1.art_id = ? AND att1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id <> ?";

   private final Artifact artifact;
   private final SkynetTransaction transaction;

   public AttributeToTransactionOperation(final Artifact artifact, final SkynetTransaction transaction) {
      this.artifact = artifact;
      this.transaction = transaction;
   }

   public void execute() throws OseeCoreException {
      for (Attribute<?> attribute : artifact.internalGetAttributes()) {
         if (attribute != null && attribute.isDirty()) {
            persistAttribute(artifact, attribute, transaction);
         }
      }
   }

   private void persistAttribute(Artifact artifact, Attribute<?> attribute, SkynetTransaction transaction) throws OseeCoreException {
      DAOToSQL daoToSql = new DAOToSQL();
      ModificationType modificationType;

      if (attribute.isInDb()) {
         if (attribute.isDeleted()) {
            if (artifact.isDeleted()) {
               modificationType = ModificationType.ARTIFACT_DELETED;
            } else {
               modificationType = ModificationType.DELETED;
            }
         } else {
            modificationType = ModificationType.CHANGE;
         }
      } else {
         if (attribute.isDeleted()) {
            if (artifact.isDeleted()) {
               modificationType = ModificationType.ARTIFACT_DELETED;
            } else {
               modificationType = ModificationType.DELETED;
            }
         } else {
            modificationType = ModificationType.NEW;
            attribute.getAttributeDataProvider().persist();
            daoToSql.setData(attribute.getAttributeDataProvider().getData());
         }
         attribute.internalSetAttributeId(getNewAttributeId(attribute));
      }

      //      if (attribute.isDeleted()) {
      //         if (!attribute.isInDb()) {
      //            return;
      //         } else {
      //            if (artifact.isDeleted()) {
      //               modificationType = ModificationType.ARTIFACT_DELETED;
      //            } else {
      //               modificationType = ModificationType.DELETED;
      //            }
      //         }
      //      } else {
      //         if (attribute.isInDb()) {
      //            modificationType = ModificationType.CHANGE;
      //         } else {
      //            createNewAttributeMemo(attribute);
      //            modificationType = ModificationType.NEW;
      //         }
      //         attribute.getAttributeDataProvider().persist();
      //         daoToSql.setData(attribute.getAttributeDataProvider().getData());
      //      }
      transaction.addAttribute(attribute, daoToSql.getValue(), daoToSql.getUri(), modificationType);
   }

   private int getNewAttributeId(Attribute<?> attribute) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      AttributeType attributeType = attribute.getAttributeType();
      int attrId = -1;
      // reuse an existing attribute id when there should only be a max of one and it has already been created on another branch 
      if (attributeType.getMaxOccurrences() == 1) {
         try {
            chStmt.runPreparedQuery(GET_EXISTING_ATTRIBUTE_IDS, attributeType.getAttrTypeId(), artifact.getArtId(),
                  artifact.getBranch().getBranchId());

            if (chStmt.next()) {
               attrId = chStmt.getInt("attr_id");
            }
         } finally {
            chStmt.close();
         }
      }
      if (attrId < 1) {
         attrId = SequenceManager.getNextAttributeId();
      }
      return attrId;
   }

   public static void meetMinimumAttributeCounts(Artifact artifact, boolean isNewArtifact) throws OseeCoreException {
      for (AttributeType attributeType : artifact.getAttributeTypes()) {
         int missingCount = attributeType.getMinOccurrences() - artifact.getAttributeCount(attributeType.getName());
         for (int i = 0; i < missingCount; i++) {
            Attribute<?> attribute = artifact.createAttribute(attributeType, true);
            if (!isNewArtifact) {
               attribute.setNotDirty();
               OseeLog.log(SkynetActivator.class, Level.FINER, String.format(
                     "artId [%d] - an attribute of type %s was created", artifact.getArtId(), attributeType.toString()));
            }
         }
      }
   }

   private final class DAOToSQL {
      private String uri;
      private String value;

      public DAOToSQL(Object... data) {
         if (data != null) {
            setData(data);
         } else {
            uri = null;
            value = null;
         }
      }

      public void setData(Object... data) {
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

   public static Attribute<?> initializeAttribute(Artifact artifact, int atttributeTypeId, int attributeId, int gammaId, Object... data) throws OseeDataStoreException {
      try {
         AttributeType attributeType = AttributeTypeManager.getType(atttributeTypeId);
         attributeType = AttributeTypeManager.getType(attributeType.getName());

         Attribute<?> attribute = artifact.createAttribute(attributeType, false);
         attribute.getAttributeDataProvider().loadData(data);
         attribute.internalSetAttributeId(attributeId);
         attribute.internalSetGammaId(gammaId);
         return attribute;
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}