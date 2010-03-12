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
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.osee.framework.jdk.core.test.type.PropertyStoreTestUtil.MockPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link PropertyStoreWriter}
 * 
 * @author Roberto E. Escobar
 */
public class PropertyStoreWriterTest {
   private MockPropertyStore store;
   private MockPropertyStore nested;
   private PropertyStoreWriter writer;

   @Before
   public void setup() {
      writer = new PropertyStoreWriter();
      store = PropertyStoreTestUtil.createPropertyStore();
      store.setId("myId");
      store.put("key1", true);
      store.put("key2", "aKey");
      store.put("key3", 0.1112);
      store.put("key4", 12);
      store.put("key5", 12.3f);
      store.put("key6", 543L);
      store.put("key6.5", "");
      store.put("key7", new String[] {"entry1", "entry2", "entry3"});
      store.put("key8", new String[] {"entry4", "entry5", "entry6"});
      nested = PropertyStoreTestUtil.createPropertyStore(System.getProperties());
      nested.put("inner array 1", new String[]{"value1", "value2"});
      nested.put("inner store 1", PropertyStoreTestUtil.createPropertyStore(System.getProperties()));
      store.put("key9", nested);
   }

   @Test
   public void testReadWriteStream() throws Exception {
      MockPropertyStore actual = PropertyStoreTestUtil.createPropertyStore();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      writer.save(store, outputStream);
      writer.load(actual, new ByteArrayInputStream(outputStream.toByteArray()));

      PropertyStoreTestUtil.checkEquals(store, actual);
   }

   @Test
   public void testReadWrite() throws Exception {
      MockPropertyStore actual = PropertyStoreTestUtil.createPropertyStore();
      StringWriter stringWriter = new StringWriter();
      writer.save(store, stringWriter);
      writer.load(actual, new StringReader(stringWriter.toString()));
      PropertyStoreTestUtil.checkEquals(store, actual);
   }

   @Test
   public void testLoadingXml() throws Exception {
      String value =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <store id=\"coverage.item\"> <item value=\"68\" key=\"line\" /> <item value=\"\" key=\"testUnits\" /> <item value=\"4\" key=\"executeNum\" /> <item value=\"APc07YhpXgobKwrbROgA\" key=\"guid\" /> <item value=\"Not_Covered\" key=\"methodType\" /> <item value=\"4\" key=\"methodNum\" /> <item value=\"OseeLog.log(Activator.class, Level.SEVERE, ex);\" key=\"text\" /> </store>";
      MockPropertyStore actual = PropertyStoreTestUtil.createPropertyStore();
      writer.load(actual, new StringReader(value));
      MockPropertyStore expected = PropertyStoreTestUtil.createPropertyStore("coverage.item");
      expected.put("line", "68");
      expected.put("testUnits", "");
      expected.put("executeNum", "4");
      expected.put("guid", "APc07YhpXgobKwrbROgA");
      expected.put("methodType", "Not_Covered");
      expected.put("methodNum", "4");
      expected.put("text", "OseeLog.log(Activator.class, Level.SEVERE, ex);");
      PropertyStoreTestUtil.checkEquals(expected, actual);
   }
}
