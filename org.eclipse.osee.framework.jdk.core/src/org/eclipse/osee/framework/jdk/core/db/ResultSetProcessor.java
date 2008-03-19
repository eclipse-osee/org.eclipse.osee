/*
 * Created on Mar 19, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.db;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.VariantData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetProcessor {

   public ResultSetProcessor() {
   }

   public IVariantData parse(ResultSet resultSet) throws Exception {
      IVariantData toReturn = new VariantData();
      ResultSetMetaData meta = resultSet.getMetaData();
      int numberOfColumns = meta.getColumnCount() + 1;
      for (int index = 1; index < numberOfColumns; index++) {
         int columnIndex = index;
         int type = meta.getColumnType(columnIndex);
         String typeName = meta.getColumnTypeName(columnIndex);
         String name = meta.getColumnName(columnIndex);
         switch (type) {
            case Types.CLOB:
               InputStream clobStream = resultSet.getAsciiStream(columnIndex);
               toReturn.put(name, Lib.inputStreamToBytes(clobStream));
               break;
            case Types.BLOB:
               InputStream blobStream = resultSet.getBinaryStream(columnIndex);
               toReturn.put(name, Lib.inputStreamToBytes(blobStream));
               break;
            default:
               try {
                  toReturn.put(name, resultSet.getString(columnIndex));
               } catch (Exception ex) {
                  throw new Exception(getErrorMessage(name, typeName), ex);
               }
               break;
         }
      }
      return toReturn;
   }

   private String getErrorMessage(String name, String typeName) {
      return String.format("Unable to convert [%s] of raw type [%s] to string.", name, typeName);
   }
}
