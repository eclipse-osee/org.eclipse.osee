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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AttributeTransactionData extends BaseTransactionData {
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private final Attribute<?> attribute;
   private final DAOToSQL daoToSql;

   public AttributeTransactionData(Attribute<?> attribute) throws OseeDataStoreException {
      super(attribute.getAttrId(), attribute.getModificationType());
      this.attribute = attribute;
      this.daoToSql = new DAOToSQL();
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES;
   }

   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (!useExistingBackingData()) {
         attribute.getAttributeDataProvider().persist(getGammaId());
         daoToSql.setData(attribute.getAttributeDataProvider().getData());
         internalAddInsertToBatch(transaction, 3, INSERT_ATTRIBUTE, attribute.getArtifact().getArtId(), getItemId(),
               attribute.getAttributeType().getId(), daoToSql.getValue(), getGammaId(), daoToSql.getUri());
      }
   }

   @Override
   protected void internalUpdate(TransactionId transactionId) throws OseeCoreException {
      attribute.internalSetGammaId(getGammaId());

      if (attribute.isOfType(StaticIdManager.STATIC_ID_ATTRIBUTE)) {
         ArtifactCache.cacheByStaticId((String) attribute.getValue(), attribute.getArtifact());
      }
   }

   @Override
   protected void internalClearDirtyState() {
      attribute.setNotDirty();
   }

   @Override
   protected void internalOnRollBack() throws OseeCoreException {
      if (!useExistingBackingData() && Strings.isValid(daoToSql.getUri())) {
         try {
            HttpProcessor.delete(AttributeURL.getDeleteURL(daoToSql.getUri()));
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (useExistingBackingData()) {
         newGammaId = attribute.getGammaId();
      } else {
         newGammaId = SequenceManager.getNextGammaId();
      }
      return newGammaId;
   }

   private static final class DAOToSQL {
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

   @Override
   protected void internalAddToEvents(Collection<ArtifactTransactionModifiedEvent> events) throws OseeCoreException {
      // Do Nothing - handled by artifact transaction data
   }
}