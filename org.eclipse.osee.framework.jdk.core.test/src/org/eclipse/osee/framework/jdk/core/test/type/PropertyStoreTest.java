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
package org.eclipse.osee.framework.jdk.core.test.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Map.Entry;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStoreTest extends TestCase {

   @org.junit.Test
   public void testCreateWithProperties() {
      Properties properties = (Properties) System.getProperties().clone();
      MockPropertyStore store1 = createPropertyStore(properties);
      checkPropertiesEqual(properties, store1.getItems());
      Assert.assertTrue(!properties.equals(store1.getArrays()));
   }

   @org.junit.Test
   public void testCreateWithId() {
      String id = "123456";
      MockPropertyStore store1 = createPropertyStore(id);
      Assert.assertEquals(id, store1.getId());
   }

   @org.junit.Test
   public void testSaveAndLoad() throws Exception {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      MockPropertyStore store1 = createPropertyStore();
      store1.put("key1", true);
      store1.put("key2", "aKey");
      store1.put("key3", 0.1112);
      store1.put("key4", 12);
      store1.put("key5", 12.3f);
      store1.put("key6", 543L);
      store1.put("key7", new String[] {"entry1", "entry2", "entry3"});
      store1.put("key8", new String[] {"entry4", "entry5", "entry6"});

      store1.save(outputStream);

      MockPropertyStore store2 = createPropertyStore();
      store2.load(new ByteArrayInputStream(outputStream.toByteArray()));

      checkEquals(store1, store2);
   }

   private void checkEquals(MockPropertyStore expected, MockPropertyStore actual) {
      Assert.assertEquals(expected.getId(), actual.getId());
      checkPropertiesEqual(expected.getItems(), actual.getItems());
      checkPropertiesEqual(expected.getArrays(), actual.getArrays());
   }

   private void checkPropertiesEqual(Properties expected, Properties actual) {
      Assert.assertEquals(expected.size(), actual.size());
      for (Entry<Object, Object> expectedEntry : expected.entrySet()) {
         Object expectedValue = expectedEntry.getValue();
         Object actualValue = actual.get(expectedEntry.getKey());
         if (expectedValue instanceof String[]) {
            String[] expArray = (String[]) expectedValue;
            String[] actualArray = (String[]) actualValue;
            Assert.assertEquals(expArray.length, actualArray.length);
            for (int index = 0; index < expArray.length; index++) {
               Assert.assertEquals(expArray[index], actualArray[index]);
            }
         } else {
            Assert.assertEquals(expectedValue, actualValue);
         }
      }
   }

   private MockPropertyStore createPropertyStore() {
      MockPropertyStore store = new MockPropertyStore();
      Assert.assertEquals("", store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      return store;
   }

   private MockPropertyStore createPropertyStore(String id) {
      MockPropertyStore store = new MockPropertyStore(id);
      Assert.assertEquals(id, store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      return store;
   }

   private MockPropertyStore createPropertyStore(Properties properties) {
      MockPropertyStore store = new MockPropertyStore(properties);
      Assert.assertEquals(properties, store.getItems());
      Assert.assertEquals(String.valueOf(properties.hashCode()), store.getId());
      Assert.assertNotNull(store.getItems());
      Assert.assertNotNull(store.getArrays());
      return store;
   }

   private final class MockPropertyStore extends PropertyStore {

      private static final long serialVersionUID = 750608597542081776L;

      MockPropertyStore() {
         super();
      }

      MockPropertyStore(String id) {
         super(id);
      }

      MockPropertyStore(Properties properties) {
         super(properties);
      }

      @Override
      public void setId(String name) {
         super.setId(name);
      }

      @Override
      public Properties getItems() {
         return super.getItems();
      }

      @Override
      public Properties getArrays() {
         return super.getArrays();
      }
   }
}
