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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AttributeTransactionData extends BaseTransactionData {
   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private final Attribute<?> attribute;
   private final DAOToSQL daoToSql;

   public AttributeTransactionData(Attribute<?> attribute) {
      super(attribute, attribute.getModificationType());
      this.attribute = attribute;
      this.daoToSql = new DAOToSQL();
   }

   public Attribute<?> getAttribute() {
      return attribute;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES;
   }

   @Override
   protected void addInsertToBatch(InsertDataCollector collector) {
      super.addInsertToBatch(collector);
      if (!attribute.isUseBackingData()) {
         attribute.getAttributeDataProvider().persist(getGammaId());
         daoToSql.setData(attribute.getAttributeDataProvider().getData());
         internalAddInsertToBatch(collector, 3, INSERT_ATTRIBUTE, attribute.getArtifact(), getItemId(),
            attribute.getAttributeType().getId(), daoToSql.getValue(), getGammaId(), daoToSql.getUri());
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) {
      attribute.internalSetGammaId(getGammaId());
      attribute.getArtifact().setTransactionId(transactionId);
   }

   @Override
   protected void internalClearDirtyState() {
      attribute.setNotDirty();
   }

   @Override
   protected void internalOnRollBack() {
      if (!attribute.isUseBackingData() && Strings.isValid(daoToSql.getUri())) {
         try {
            OseeClient client = ServiceUtil.getOseeClient();
            ResourcesEndpoint endpoint = client.getResourcesEndpoint();
            endpoint.deleteResource(asPath(daoToSql.getUri()));
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
   }

   private String asPath(String uri) {
      String toReturn = uri;
      if (Strings.isValid(toReturn)) {
         toReturn = toReturn.replaceAll("://", "/");
      }
      return toReturn;
   }

   @Override
   protected int createGammaId() {
      return attribute.isUseBackingData() ? attribute.getGammaId() : getNextGammaIdFromSequence();
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
   protected void internalAddToEvents(ArtifactEvent artifactEvent) {
      return;
   }

   @Override
   protected ApplicabilityId getApplicabilityId() {
      return attribute.getApplicabilityId();
   }

}