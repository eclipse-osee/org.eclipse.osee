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
import org.eclipse.osee.framework.core.services.ITranslatorId;
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

   private enum TxId implements ITranslatorId {
      STRING_TX, INTEGER_TX, OBJECT_TX, DUMMY_TX;

      @Override
      public String getKey() {
         return name();
      }

   }

   @Test
   public void testAddRemoveTranslator() throws OseeCoreException {
      IDataTranslationService service = new DataTranslationService();

      ITranslator<String> tx1 = new DataTranslatorAdapter<String>();
      ITranslator<Integer> tx2 = new DataTranslatorAdapter<Integer>();
      ITranslator<Object> tx3 = new DataTranslatorAdapter<Object>();

      Assert.assertTrue(service.getSupportedClasses().isEmpty());

      Assert.assertTrue(service.addTranslator(tx1, TxId.STRING_TX));
      Assert.assertTrue(service.addTranslator(tx2, TxId.INTEGER_TX));
      Assert.assertFalse(service.addTranslator(tx2, TxId.INTEGER_TX)); // Add again

      Assert.assertTrue(service.addTranslator(tx3, TxId.OBJECT_TX));
      Assert.assertEquals(3, service.getSupportedClasses().size());

      Assert.assertEquals(tx1, service.getTranslator(TxId.STRING_TX));
      Assert.assertEquals(tx2, service.getTranslator(TxId.INTEGER_TX));
      Assert.assertEquals(tx3, service.getTranslator(TxId.OBJECT_TX));

      try {
         service.getTranslator(TxId.DUMMY_TX);
         Assert.fail("Should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }

      Assert.assertTrue(service.removeTranslator(TxId.INTEGER_TX));
      Assert.assertEquals(2, service.getSupportedClasses().size());

      Assert.assertFalse(service.removeTranslator(TxId.INTEGER_TX));

      Assert.assertTrue(service.removeTranslator(TxId.STRING_TX));
      Assert.assertEquals(1, service.getSupportedClasses().size());

      Assert.assertFalse(service.removeTranslator(TxId.DUMMY_TX));
      Assert.assertEquals(1, service.getSupportedClasses().size());

      Assert.assertTrue(service.removeTranslator(TxId.OBJECT_TX));
      Assert.assertTrue(service.getSupportedClasses().isEmpty());
   }

   @Test
   public void testConvert() throws OseeCoreException {
      DataTranslationService service = new DataTranslationService();
      service.addTranslator(new TestObjectTranslator(), TxId.OBJECT_TX);

      TestObject value = new TestObject("hello", 1, 1.0);
      PropertyStore propertyStore = service.convert(value, TxId.OBJECT_TX);
      Assert.assertFalse(propertyStore.isEmpty());
      TestObject actual = service.convert(propertyStore, TxId.OBJECT_TX);

      Assert.assertEquals(value.one, actual.one);
      Assert.assertEquals(value.two, actual.two);
      Assert.assertEquals(value.three, actual.three);
   }

   @Test
   public void testConvertStreams() throws OseeCoreException {
      DataTranslationService service = new DataTranslationService();
      service.addTranslator(new TestObjectTranslator(), TxId.OBJECT_TX);

      TestObject expected = new TestObject("streamTest", 45, 1.0);
      TestObject actual = null;
      InputStream stream = null;
      try {
         stream = service.convertToStream(expected, TxId.OBJECT_TX);
         actual = service.convert(stream, TxId.OBJECT_TX);

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

      assertEmpty(service.convert((Object) null, TxId.OBJECT_TX));
      assertEmpty(service.convertToStream(null, TxId.OBJECT_TX));
      Assert.assertNull(service.convert((PropertyStore) null, TxId.OBJECT_TX));
   }

   private void assertEmpty(InputStream inputStream) throws Exception {
      PropertyStore toCheck = new PropertyStore();
      Assert.assertTrue(toCheck.isEmpty());
      toCheck.load(inputStream);
      assertEmpty(toCheck);
   }

   private void assertEmpty(PropertyStore toCheck) throws Exception {
      Assert.assertTrue(toCheck.arrayKeySet().isEmpty());
      Assert.assertTrue(toCheck.keySet().isEmpty());
      Assert.assertTrue(toCheck.innerStoresKeySet().isEmpty());
      Assert.assertTrue(toCheck.isEmpty());
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullClazz() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert(new ByteArrayInputStream(new byte[0]), (ITranslatorId) null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullClazz2() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert(new PropertyStore(), (ITranslatorId) null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullInputStream() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.convert((InputStream) null, TxId.OBJECT_TX);
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullGetTranslator() throws Exception {
      DataTranslationService service = new DataTranslationService();
      service.getTranslator((ITranslatorId) null);
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
