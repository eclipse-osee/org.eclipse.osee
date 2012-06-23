/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

public class AttributeSqlProvider extends AbstractSqlProvider implements SqlProvider<AttributeData> {

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private final DAOToSQL daoToSql = new DAOToSQL();

   public AttributeSqlProvider(IdFactory idFactory) {
      super(idFactory);
   }

   @Override
   public void getInsertData(InsertDataCollector collector, AttributeData data) throws OseeCoreException {
      long gammaId = getGammaId(data);
      DataProxy dataProxy = data.getDataProxy();

      collector.addBinaryStore(new BinaryStoreTx(gammaId, dataProxy));
      daoToSql.setData(dataProxy.getData());

      collector.addInsertToBatch(3, INSERT_ATTRIBUTE, data.getArtifactId(), data.getLocalId(), data.getTypeUuid(),
         daoToSql.getValue(), gammaId, daoToSql.getUri());

      collector.addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES, data.getLocalId(),
         data.getModType());
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

}
