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

import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 */
public class AttributeTransactionData extends BaseTransactionData {
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri, modification_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

   private final Attribute<?> attribute;
   private final DAOToSQL daoToSql;

   public AttributeTransactionData(Attribute<?> attribute, ModificationType modificationType) throws OseeDataStoreException {
      super(attribute.getAttrId(), modificationType);
      this.attribute = attribute;
      this.daoToSql = new DAOToSQL();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return OseeSql.Transaction.SELECT_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#addInsertToBatch(org.eclipse.osee.framework.jdk.core.type.HashCollection)
    */
   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (getModificationType() != ModificationType.ARTIFACT_DELETED) {
         if (getModificationType() == ModificationType.CHANGE || getModificationType() == ModificationType.NEW) {
            attribute.getAttributeDataProvider().persist(getGammaId());
            daoToSql.setData(attribute.getAttributeDataProvider().getData());
         }
         internalAddInsertToBatch(transaction, 3, INSERT_ATTRIBUTE, attribute.getArtifact().getArtId(), getItemId(),
               attribute.getAttributeType().getAttrTypeId(), daoToSql.getValue(), getGammaId(), daoToSql.getUri(),
               getModificationType().getValue());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalUpdate()
    */
   @Override
   protected void internalUpdate(TransactionId transactionId) throws OseeCoreException {
      attribute.internalSetGammaId(getGammaId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalClearDirtyState()
    */
   @Override
   protected void internalClearDirtyState() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalOnRollBack()
    */
   @Override
   protected void internalOnRollBack() throws OseeCoreException {
      if (Strings.isValid(daoToSql.getUri())) {
         try {
            HttpProcessor.delete(AttributeURL.getDeleteURL(daoToSql.getUri()));
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#createGammaId()
    */
   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (getModificationType() == ModificationType.ARTIFACT_DELETED) {
         newGammaId = attribute.getGammaId();
      } else {
         newGammaId = SequenceManager.getNextGammaId();
      }
      return newGammaId;
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
}