/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

public class AttributeInsertProvider extends BaseInsertProvider implements InsertDataProvider<AttributeData> {

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private final DAOToSQL daoToSql = new DAOToSQL();

   @Override
   public void getInsertData(InsertDataCollector collector, Collection<AttributeData> datas) throws OseeCoreException {
      for (AttributeData data : datas) {
         long gammaId = getGammaId(data);
         DataProxy dataProxy = data.getDataProxy();

         dataProxy.persist(gammaId);
         daoToSql.setData(dataProxy.getData());

         collector.addInsertToBatch(3, INSERT_ATTRIBUTE, data.getArtifactId(), data.getLocalId(), data.getTypeUuid(),
            daoToSql.getValue(), gammaId, daoToSql.getUri());

         collector.addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES, data.getLocalId(),
            data.getModType());
      }
   }
   //TX_TODO
   //   @Override
   //   protected void internalOnRollBack() throws OseeCoreException {
   //      if (!useExistingBackingData() && Strings.isValid(daoToSql.getUri())) {
   //         try {
   //            HttpProcessor.delete(AttributeURL.getDeleteURL(daoToSql.getUri()));
   //         } catch (Exception ex) {
   //            OseeExceptions.wrapAndThrow(ex);
   //         }
   //      }
   //   }

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
