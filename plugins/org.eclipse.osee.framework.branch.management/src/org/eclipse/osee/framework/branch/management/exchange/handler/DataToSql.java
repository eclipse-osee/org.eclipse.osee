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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.lang.reflect.Method;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.exchange.TranslationManager;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class DataToSql {

   private DataToSql() {
   }

   public static Object[] toDataArray(MetaData metadata, Map<String, Object> dataMap) {
      int notNullCount = 0;
      Object[] data = new Object[metadata.getColumnSize()];
      int index = 0;
      for (String columnName : metadata.getColumnNames()) {
         Object dataValue = dataMap.get(columnName);
         if (dataValue != null) {
            data[index] = dataValue;
            notNullCount++;
         } else {
            data[index] = metadata.toDataType(columnName);
         }
         index++;
      }
      return notNullCount > 0 ? data : null;
   }

   public static Object[] toDataArray(OseeConnection connection, MetaData metadata, TranslationManager translator, Map<String, String> fieldMap) throws OseeDataStoreException {
      int notNullCount = 0;
      Object[] data = new Object[metadata.getColumnSize()];
      int index = 0;
      for (String columnName : metadata.getColumnNames()) {
         String dataValue = fieldMap.get(columnName);
         if (Strings.isValid(dataValue)) {
            Class<?> clazz = metadata.toClass(columnName);
            Object object = stringToObject(clazz, columnName, dataValue);
            if (object != null && translator.isTranslatable(columnName)) {
               object = translator.translate(columnName, object);
            }
            if (object != null) {
               data[index] = object;
               notNullCount++;
            } else {
               data[index] = metadata.toDataType(columnName);
            }
         } else {
            data[index] = metadata.toDataType(columnName);
         }
         index++;
      }
      return notNullCount > 0 ? data : null;
   }

   public static Object stringToObject(Class<?> clazz, String columnName, String value) {
      Object convertedObject = null;
      if (clazz != null) {
         try {
            Method mainMethod = clazz.getMethod("valueOf", new Class[] {String.class});
            convertedObject = mainMethod.invoke(null, value);
         } catch (Exception ex) {
            try {
               Method mainMethod = clazz.getMethod("valueOf", new Class[] {Object.class});
               convertedObject = mainMethod.invoke(null, value);
            } catch (Exception ex1) {
               throw new IllegalStateException(String.format(
                     "Unable to convert from string to object for - attribute [%s] to class [%s]", columnName,
                     clazz.getName()));
            }
         }
      } else {
         convertedObject = value;
      }
      return convertedObject;
   }
}
