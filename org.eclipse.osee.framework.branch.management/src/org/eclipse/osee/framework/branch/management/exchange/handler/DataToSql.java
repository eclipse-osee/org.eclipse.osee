/*
 * Created on Aug 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;
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

   public static Object[] toDataArray(Connection connection, MetaData metadata, Translator translator, Map<String, String> fieldMap) throws Exception {
      int notNullCount = 0;
      Object[] data = new Object[metadata.getColumnSize()];
      int index = 0;
      for (String columnName : metadata.getColumnNames()) {
         String dataValue = fieldMap.get(columnName);
         if (Strings.isValid(dataValue)) {
            Class<?> clazz = metadata.toClass(columnName);
            Object object = stringToObject(clazz, columnName, dataValue);
            if (object != null && translator.isTranslatable(columnName)) {
               object = translator.translate(connection, columnName, object);
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
