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
package org.eclipse.osee.framework.core.test.exchange;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BranchCommitDataTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.junit.Test;

/**
 * Test Case for {@link BranchCommitDataTranslator}
 * 
 * @author Megumi Telles
 */
public class BranchCommitDataTranslatorTest {

   @Test
   public void testAddRemoveTranslator() {
      IDataTranslationService service = new DataTranslationService();

   }

   @Test
   public void testConvert() throws OseeCoreException {
      DataTranslationService service = new DataTranslationService();

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

   private class DataTranslatorAdapter<T> implements IDataTranslator<T> {
      @Override
      public T convert(PropertyStore propertyStore) {
         return null;
      }

      @Override
      public PropertyStore convert(T object) {
         return null;
      }
   }

   private class TestObjectTranslator implements IDataTranslator<TestObject> {
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
