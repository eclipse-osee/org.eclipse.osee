/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class PropertyStoreTestUtil {

   private PropertyStoreTestUtil() {
      // Utility Class
   }

   public static void checkArrays(String[] expArray, String[] actualArray) {
      Assert.assertEquals(expArray.length, actualArray.length);
      for (int index = 0; index < expArray.length; index++) {
         Assert.assertEquals(expArray[index], actualArray[index]);
      }
   }

   public static PropertyStore createPropertyStore() {
      PropertyStore store = new PropertyStore();
      Assert.assertEquals("", store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      Assert.assertTrue(store.isEmpty());
      return store;
   }

   public static PropertyStore createPropertyStore(String id) {
      PropertyStore store = new PropertyStore(id);
      Assert.assertEquals(id, store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      return store;
   }

   public static PropertyStore createPropertyStore(Map<String, Object> properties) {
      PropertyStore store = new PropertyStore(properties);
      Assert.assertEquals(properties, store.getItems());
      Assert.assertEquals(String.valueOf(properties.hashCode()), store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      return store;
   }

   public static void checkEquals(PropertyStore expected, PropertyStore actual) {
      Assert.assertEquals(expected.getId(), actual.getId());
      checkPropertiesEqual(expected.getItems(), actual.getItems());
      checkPropertiesEqual(expected.getArrays(), actual.getArrays());
      checkPropertiesEqual(expected.getPropertyStores(), actual.getPropertyStores());
   }

   public static void checkPropertiesEqual(Map<String, Object> expected, Map<String, Object> actual) {
      Assert.assertEquals(expected.size(), actual.size());
      for (Entry<String, Object> expectedEntry : expected.entrySet()) {
         Object expectedValue = expectedEntry.getValue();
         Object actualValue = actual.get(expectedEntry.getKey());
         if (expectedValue instanceof String[]) {
            String[] expArray = (String[]) expectedValue;
            String[] actualArray = (String[]) actualValue;
            checkArrays(expArray, actualArray);
         } else {
            Assert.assertEquals(expectedValue, actualValue);
         }
      }
   }

   public static Map<String, Object> convertPropertiesToMap(Properties props) {
      Map<String, Object> result = new TreeMap<>();
      for (Entry<Object, Object> entry : props.entrySet()) {
         result.put((String) entry.getKey(), entry.getValue());
      }
      return result;
   }

}
