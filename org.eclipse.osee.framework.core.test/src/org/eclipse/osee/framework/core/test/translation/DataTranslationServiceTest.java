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
package org.eclipse.osee.framework.core.test.translation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DataTranslationService}
 * 
 * @author Roberto E. Escobar
 */
public class DataTranslationServiceTest {

   @Test
   public void testAddRemoveTranslator() throws OseeCoreException {
      IDataTranslationService service = new DataTranslationService();

      ITranslator<String> tx1 = new DataTranslatorAdapter<String>();
      ITranslator<Integer> tx2 = new DataTranslatorAdapter<Integer>();
      ITranslator<Object> tx3 = new DataTranslatorAdapter<Object>();

      Assert.assertTrue(service.getSupportedClasses().isEmpty());

      Assert.assertTrue(service.addTranslator(tx1, String.class));
      Assert.assertTrue(service.addTranslator(tx2, Integer.class));
      Assert.assertFalse(service.addTranslator(tx2, Integer.class)); // Add again

      Assert.assertTrue(service.addTranslator(tx3, Object.class, Integer.class));
      Assert.assertEquals(3, service.getSupportedClasses().size());

      Assert.assertEquals(tx1, service.getTranslator(String.class));
      Assert.assertEquals(tx2, service.getTranslator(Integer.class));
      Assert.assertEquals(tx3, service.getTranslator(Object.class, Integer.class));

      try {
         service.getTranslator(Integer.class, Object.class);
         Assert.fail("Should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }

      Assert.assertTrue(service.removeTranslator(Integer.class));
      Assert.assertEquals(2, service.getSupportedClasses().size());

      Assert.assertFalse(service.removeTranslator(Integer.class));

      Assert.assertTrue(service.removeTranslator(String.class));
      Assert.assertEquals(1, service.getSupportedClasses().size());

      Assert.assertFalse(service.removeTranslator(Integer.class, Object.class));
      Assert.assertEquals(1, service.getSupportedClasses().size());

      Assert.assertTrue(service.removeTranslator(Object.class, Integer.class));
      Assert.assertTrue(service.getSupportedClasses().isEmpty());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testConvert() throws OseeCoreException {
      DataTranslationService service = new DataTranslationService();
      service.addTranslator(new TestObjectTranslator(), TestObject.class);

      TestObject value = new TestObject("hello", 1, 1.0);
      PropertyStore propertyStore = service.convert(value);
      TestObject actual = service.convert(propertyStore, TestObject.class);

      Assert.assertEquals(value.one, actual.one);
      Assert.assertEquals(value.two, actual.two);
      Assert.assertEquals(value.three, actual.three);
   }

   @Test
   public void testConvertStreams() throws OseeCoreException {
      DataTranslationService service = new DataTranslationService();
      service.addTranslator(new TestObjectTranslator(), TestObject.class);

      TestObject expected = new TestObject("streamTest", 45, 1.0);
      TestObject actual = null;
      InputStream stream = null;
      try {
         stream = service.convertToStream(expected);
         actual = service.convert(stream, TestObject.class);

      } finally {
         Lib.close(stream);
      }
      Assert.assertNotNull(actual);
      Assert.assertNotSame(expected, actual);
      Assert.assertEquals(expected.one, actual.one);
      Assert.assertEquals(expected.two, actual.two);
      Assert.assertEquals(expected.three, actual.three);
   }

   @Test
   public void testNullConverts() throws Exception {
      DataTranslationService service = new DataTranslationService();

      assertEmpty(service.convert(null));
      assertEmpty(service.convertToStream(null));
      Assert.assertNull(service.convert((PropertyStore) null, Object.class));
   }

   private void assertEmpty(InputStream inputStream) throws Exception {
      PropertyStore toCheck = new PropertyStore();
      toCheck.load(inputStream);
      assertEmpty(toCheck);
   }

   private void assertEmpty(PropertyStore toCheck) throws Exception {
      Assert.assertTrue(toCheck.arrayKeySet().isEmpty());
      Assert.assertTrue(toCheck.keySet().isEmpty());
      Assert.assertTrue(toCheck.innerStoresKeySet().isEmpty());
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullClazz() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert(new ByteArrayInputStream(new byte[0]), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullClazz2() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert(new PropertyStore(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullInputStream() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert((InputStream) null, Object.class);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullGetTranslator() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.getTranslator(null);
   }

   private class TestObject {
      String one;
      Integer two;
      Double three;

      public TestObject(String one, Integer two, Double three) {
         super();
         this.one = one;
         this.two = two;
         this.three = three;
      }

   }

   private class DataTranslatorAdapter<T> implements ITranslator<T> {
      @Override
      public T convert(PropertyStore propertyStore) {
         return null;
      }

      @Override
      public PropertyStore convert(T object) {
         return null;
      }
   }

   private class TestObjectTranslator implements ITranslator<TestObject> {
      @Override
      public TestObject convert(PropertyStore propertyStore) {
         return new TestObject(propertyStore.get("one"), propertyStore.getInt("two"), propertyStore.getDouble("three"));
      }

      @Override
      public PropertyStore convert(TestObject object) {
         PropertyStore propertyStore = new PropertyStore();
         propertyStore.put("one", object.one);
         propertyStore.put("two", object.two);
         propertyStore.put("three", object.three);
         return propertyStore;
      }
   }

}
