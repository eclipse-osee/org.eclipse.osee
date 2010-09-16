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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.VariantData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetProcessor {
   public static IVariantData parse(IOseeStatement chStmt) throws OseeCoreException {
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
               String value = chStmt.getString(name);
               if (Strings.isValid(value) != false) {
                  value = value.trim();
               }
               toReturn.put(upperCasedName, chStmt.getString(name));
               break;
         }
      }
      return toReturn;
   }

   private static byte[] streamToByteArray(InputStream inputStream) throws OseeCoreException {
      byte[] toReturn = new byte[0];
      if (inputStream != null) {
         try {
            toReturn = Lib.inputStreamToBytes(inputStream);
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return toReturn;
   }
}
