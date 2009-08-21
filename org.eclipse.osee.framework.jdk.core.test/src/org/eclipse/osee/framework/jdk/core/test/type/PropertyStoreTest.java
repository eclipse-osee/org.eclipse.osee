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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStoreTest {

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
   public void testEqualsAndHashCode() {
      MockPropertyStore store1 = createPropertyStore("ID");
      store1.put("key1", 12.3f);
      store1.put("key2", 543L);
      store1.put("key3", new String[] {"entry1", "entry2", "entry3"});

      Assert.assertFalse(store1.equals("Test"));

      MockPropertyStore store2 = createPropertyStore("ID");
      store2.put("key1", 12.3f);
      store2.put("key2", 543L);
      store2.put("key3", new String[] {"entry1", "entry2", "entry3"});

      Assert.assertTrue(store1.equals(store2));

      int hash = store1.hashCode();
      Assert.assertEquals(hash, store2.hashCode());
      // Check that it didn't change
      Assert.assertEquals(hash, store2.hashCode());

      store2.put("key2", 542L);
      Assert.assertTrue(!store1.equals(store2));
      Assert.assertTrue(hash != store2.hashCode());

      store2.put("key2", 543L);
      Assert.assertTrue(store1.equals(store2));

      store2.put("key3", new String[] {"entry1", "entry2"});
      Assert.assertTrue(!store1.equals(store2));

      store2.put("key3", new String[] {"entry1", "entry2", "entry4"});
      Assert.assertTrue(!store1.equals(store2));
   }

   @org.junit.Test
   public void testToString() {
      MockPropertyStore store1 = createPropertyStore("ID");
      store1.put("key1", 12.3f);
      store1.put("key2", 543L);
      store1.put("key3", new String[] {"entry1", "entry2", "entry3"});
      store1.put("key4", new String[] {"entry4", "entry5"});

      Assert.assertEquals(
            "Id:[ID] Data:{key2=543, key1=12.3} Arrays:{key3=[entry1, entry2, entry3], key4=[entry4, entry5]}",
            store1.toString());
   }

   @org.junit.Test
   public void testNumberException() {
      MockPropertyStore store1 = createPropertyStore();

      try {
         store1.getDouble("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("No setting found for key: [key1]", ex.getLocalizedMessage());
      }

      try {
         store1.getFloat("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("No setting found for key: [key1]", ex.getLocalizedMessage());
      }
      try {
         store1.getInt("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("No setting found for key: [key1]", ex.getLocalizedMessage());
      }
      try {
         store1.getLong("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("No setting found for key: [key1]", ex.getLocalizedMessage());
      }
      store1.put("key1", "hello");
      try {
         store1.getDouble("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("For input string: \"hello\"", ex.getLocalizedMessage());
      }
      try {
         store1.getFloat("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("For input string: \"hello\"", ex.getLocalizedMessage());
      }
      try {
         store1.getInt("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("For input string: \"hello\"", ex.getLocalizedMessage());
      }
      try {
         store1.getLong("key1");
      } catch (NumberFormatException ex) {
         Assert.assertEquals("For input string: \"hello\"", ex.getLocalizedMessage());
      }
      Assert.assertEquals("hello", store1.get("key1"));
   }

   @org.junit.Test
   public void testSetsAndGets() throws Exception {
      MockPropertyStore store1 = createPropertyStore();
      store1.setId("myId");
      store1.put("key1", true);
      store1.put("key2", "aKey");
      store1.put("key3", 0.1112);
      store1.put("key4", 12);
      store1.put("key5", 12.3f);
      store1.put("key6", 543L);
      store1.put("key6.5", "");
      store1.put("key7", new String[] {"entry1", "entry2", "entry3"});
      store1.put("key8", new String[] {"entry4", "entry5", "entry6"});

      Set<String> set1 = new TreeSet<String>(store1.keySet());
      checkArrays(new String[] {"key1", "key2", "key3", "key4", "key5", "key6", "key6.5"},
            set1.toArray(new String[set1.size()]));
      Set<String> set2 = new TreeSet<String>(store1.arrayKeySet());
      checkArrays(new String[] {"key7", "key8"}, set2.toArray(new String[set2.size()]));

      Assert.assertEquals("myId", store1.getId());
      Assert.assertEquals(true, store1.getBoolean("key1"));
      Assert.assertEquals("aKey", store1.get("key2"));
      Assert.assertEquals(0.1112, store1.getDouble("key3"));
      Assert.assertEquals(12, store1.getInt("key4"));
      Assert.assertEquals(12.3f, store1.getFloat("key5"));
      Assert.assertEquals(543L, store1.getLong("key6"));
      Assert.assertEquals("", store1.get("key6.5"));

      checkArrays(new String[] {"entry1", "entry2", "entry3"}, store1.getArray("key7"));
      checkArrays(new String[] {"entry4", "entry5", "entry6"}, store1.getArray("key8"));
   }

   @org.junit.Test
   public void testNullSetsAndGets() {
      MockPropertyStore store1 = createPropertyStore("HelloId");
      Assert.assertEquals("HelloId", store1.getId());
      store1.setId(null);

      Assert.assertEquals("", store1.getId());

      store1.put("key1", (String) null);
      Assert.assertEquals("", store1.get("key1"));
      store1.put("key2", (String[]) null);
      Assert.assertTrue(store1.getArray("key2").length == 0);
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

   @org.junit.Test
   public void testSaveAndLoadRW() throws Exception {
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

      store1.save(new OutputStreamWriter(outputStream));

      MockPropertyStore store2 = createPropertyStore();
      store2.load(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()), "utf-8"));

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
            checkArrays(expArray, actualArray);
         } else {
            Assert.assertEquals(expectedValue, actualValue);
         }
      }
   }

   private void checkArrays(String[] expArray, String[] actualArray) {
      Assert.assertEquals(expArray.length, actualArray.length);
      for (int index = 0; index < expArray.length; index++) {
         Assert.assertEquals(expArray[index], actualArray[index]);
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
