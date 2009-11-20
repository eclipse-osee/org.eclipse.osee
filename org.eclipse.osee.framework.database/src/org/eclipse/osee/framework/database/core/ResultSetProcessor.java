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
package org.eclipse.osee.framework.database.core;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.VariantData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetProcessor {
   public static IVariantData parse(IOseeStatement chStmt) throws OseeDataStoreException, OseeWrappedException {
      IVariantData toReturn = new VariantData();
      int numberOfColumns = chStmt.getColumnCount() + 1;
      for (int index = 1; index < numberOfColumns; index++) {
         int columnIndex = index;
         int type = chStmt.getColumnType(columnIndex);
         String name = chStmt.getColumnName(columnIndex);
         // Store name - all upper case
         String upperCasedName = name.toUpperCase();
         switch (type) {
            case Types.CLOB:
            case Types.BINARY:
               InputStream inputStream = chStmt.getAsciiStream(name);
               toReturn.put(upperCasedName, streamToByteArray(inputStream));
               break;
            case Types.BLOB:
               InputStream blobStream = chStmt.getBinaryStream(name);
               toReturn.put(upperCasedName, streamToByteArray(blobStream));
               break;
            case Types.TIMESTAMP:
               Timestamp timeStamp = chStmt.getTimestamp(name);
               if (timeStamp != null) {
                  toReturn.put(upperCasedName, timeStamp.getTime());
               }
               break;
            case Types.DATE:
               Date date = chStmt.getDate(name);
               if (date != null) {
                  toReturn.put(upperCasedName, date.getTime());
               }
               break;
            default:
               try {
                  String value = chStmt.getString(name);
                  if (Strings.isValid(value) != false) {
                     value = value.trim();
                  }
                  toReturn.put(upperCasedName, chStmt.getString(name));
               } catch (OseeDataStoreException ex) {
                  String typeName = chStmt.getColumnTypeName(columnIndex);
                  throw new OseeDataStoreException(getErrorMessage(name, typeName), ex);
               }
               break;
         }
      }
      return toReturn;
   }

   private static String getErrorMessage(String name, String typeName) {
      return String.format("Unable to convert [%s] of raw type [%s] to string.", name, typeName);
   }

   private static byte[] streamToByteArray(InputStream inputStream) throws OseeWrappedException {
      byte[] toReturn = new byte[0];
      if (inputStream != null) {
         try {
            toReturn = Lib.inputStreamToBytes(inputStream);
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
      return toReturn;
   }
}
