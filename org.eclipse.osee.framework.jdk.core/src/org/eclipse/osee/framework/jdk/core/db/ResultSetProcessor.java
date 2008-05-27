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

package org.eclipse.osee.framework.jdk.core.db;

import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.VariantData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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
         // Store name - all upper case
         name = name.toUpperCase();
         switch (type) {
            case Types.CLOB:
            case Types.BINARY:
               InputStream inputStream = resultSet.getAsciiStream(columnIndex);
               toReturn.put(name, streamToByteArray(inputStream));
               break;
            case Types.BLOB:
               InputStream blobStream = resultSet.getBinaryStream(columnIndex);
               toReturn.put(name, streamToByteArray(blobStream));
               break;
            case Types.DATE:
               Date date = resultSet.getDate(columnIndex);
               if (date != null) {
                  toReturn.put(name, date.getTime());
               }
               break;
            default:
               try {
                  String value = resultSet.getString(columnIndex);
                  if (Strings.isValid(value) != false) {
                     value = value.trim();
                  }
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

   private byte[] streamToByteArray(InputStream inputStream) throws Exception {
      byte[] toReturn = new byte[0];
      if (inputStream != null) {
         try {
            toReturn = Lib.inputStreamToBytes(inputStream);
         } finally {
            inputStream.close();
         }
      }
      return toReturn;
   }
}
