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
package org.eclipse.osee.framework.skynet.core.test.types.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTestDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeField;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.field.AliasesField;
import org.eclipse.osee.framework.skynet.core.types.field.ChangeUtil;
import org.eclipse.osee.framework.skynet.core.types.field.OseeField;
import org.eclipse.osee.framework.skynet.core.types.field.UniqueIdField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeFieldMainTest {
   private final OseeFieldTest<?> test;

   public OseeFieldMainTest(OseeFieldTest<?> test) {
      this.test = test;
   }

   @Test
   public void testInitialValues() throws OseeCoreException {
      String message = "InitTest";
      checkObjects(message, test.getInitExpectedValue(), test.getField().get());
      Assert.assertEquals(message, test.isInitExpectedDirty(), test.getField().isDirty());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSettterGetter() throws OseeCoreException {
      int index = 0;
      for (TestData testData : test.getTestDatas()) {
         String message = String.format("Test Data [%s]", ++index);
         if (testData.isClearBeforeRun()) {
            test.getField().clearDirty();
            Assert.assertFalse(message, test.getField().isDirty());
         }

         if (testData.throwsError()) {
            try {
               test.doSetValue(testData);
               Assert.assertNull(message, "This line should never be executed");
            } catch (Exception ex) {
               Class<?> clazz = testData.getError();
               Assert.assertTrue(message, ex.getClass().isAssignableFrom(clazz));
            }
         } else {
            test.doSetValue(testData);
         }

         checkObjects(message, testData.getExpectedValue(), test.getField().get());
         Assert.assertEquals(message, testData.isExpectedDirty(), test.getField().isDirty());
      }
   }

   private void checkObjects(String message, Object expected, Object actual) {
      if (expected instanceof Collection<?> && actual instanceof Collection<?> || expected instanceof Object[] && actual instanceof Object[]) {
         Assert.assertFalse(message, ChangeUtil.isDifferent(expected, actual));
      } else {
         Assert.assertEquals(message, expected, actual);
      }
   }

   @SuppressWarnings("unchecked")
   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {//
      new OseeFieldTest<Object>(new OseeField<Object>(), //
            null, false, //
            new TestData<Object>(true, "one test", "one test", true), //
            new TestData<Object>(false, "two test", "two test", true), //
            new TestData<Object>(true, "three test", "three test", true)//
      )});//

      data.add(new Object[] { //
      new OseeFieldTest<Integer>(new OseeField<Integer>(), //
            null, false, //
            new TestData<Integer>(true, 1, 1, true), //
            new TestData<Integer>(false, 5, 5, true), //
            new TestData<Integer>(true, Integer.MIN_VALUE, Integer.MIN_VALUE, true)//
      )});

      data.add(new Object[] { //
      new OseeFieldTest<Boolean>(new OseeField<Boolean>(), //
            null, false, // 
            new TestData<Boolean>(true, true, true, true), //
            new TestData<Boolean>(false, true, true, true), //
            new TestData<Boolean>(true, false, false, true),//
            new TestData<Boolean>(false, false, false, true)//
      )});

      data.add(new Object[] {new OseeFieldTest<Object>(new OseeField<Object>("string1"), "string1", true)});
      data.add(new Object[] {new OseeFieldTest<Integer>(new OseeField<Integer>(Integer.MIN_VALUE), Integer.MIN_VALUE,
            true)});
      data.add(new Object[] {new OseeFieldTest<Boolean>(new OseeField<Boolean>(true), true, true)});
      data.add(new Object[] {new OseeFieldTest<Boolean>(new OseeField<Boolean>(false), false, true)});

      data.add(new Object[] {//
      new OseeFieldTest<String>(new OseeField<String>("string2"), "string2", true, //
            new TestData<String>(false, "another", "another", true), //
            new TestData<String>(true, "something", "something", true)//
      )});

      data.add(new Object[] { //
      new OseeFieldTest<Integer>(new UniqueIdField(), //
            UniqueIdField.UNPERSISTTED_VALUE, true, //
            new TestData<Integer>(false, UniqueIdField.UNPERSISTTED_VALUE, UniqueIdField.UNPERSISTTED_VALUE, true), //
            new TestData<Integer>(true, UniqueIdField.UNPERSISTTED_VALUE, UniqueIdField.UNPERSISTTED_VALUE, false), //
            new TestData<Integer>(false, 100, 100, true), //
            new TestData<Integer>(false, 200, 100, true, OseeStateException.class),//
            new TestData<Integer>(true, 300, 100, false, OseeStateException.class)//
      )});

      IOseeTypeFactory factory = new OseeTypeFactory();
      BranchCache branchCache = new BranchCache(factory, new OseeTestDataAccessor<Branch>());
      Branch branch =
            factory.createBranch(branchCache, GUID.create(), "Test Branch", BranchType.WORKING, BranchState.MODIFIED,
                  false);
      branch.setAliases("Alias 1", "Alias2");

      Collection<String> emptyList = Collections.emptyList();
      data.add(new Object[] {//
      new OseeFieldTest<Collection<String>>(
            //
            new AliasesField(branchCache, branch), //
            Arrays.asList("alias 1", "alias2"),
            false,//
            new TestData<Collection<String>>(false, Arrays.asList("alias2", "alias 1"), Arrays.asList("alias 1",
                  "alias2"), false),//
            new TestData<Collection<String>>(false, Arrays.asList("alias 1"), Arrays.asList("alias 1"), true),
            new TestData<Collection<String>>(true, emptyList, emptyList, true)//
      )});

      // TODO Add set tests
      //      data.add(new Object[] {new OseeFieldTest<IArtifact>(new AssociatedArtifactField(null, null)});
      //      data.add(new Object[] {new OseeFieldTest<Collection<ArtifactType>>(new ArtifactSuperTypeField(null, null)});
      //      data.add(new Object[] {new OseeFieldTest<Map<Branch, Collection<AttributeType>>>(new ArtifactTypeAttributesField(null, null)});
      //      data.add(new Object[] {new OseeFieldTest<List<OseeEnumEntry>>(new EnumEntryField(null, null)});

      return data;
   }
   private final static class OseeFieldTest<T> {
      private final IOseeField<T> field;
      private final Object initExpectedValue;
      private final boolean initExpectedDirty;

      private final Collection<TestData<T>> testDatas;

      public OseeFieldTest(IOseeField<T> field, Object initExpectedValue, boolean initExpectedDirty, TestData<T>... testDatas) {
         this.field = field;
         this.initExpectedValue = initExpectedValue;
         this.initExpectedDirty = initExpectedDirty;
         this.testDatas = new ArrayList<TestData<T>>();
         if (testDatas != null && testDatas.length > 0) {
            this.testDatas.addAll(Arrays.asList(testDatas));
         }
      }

      public IOseeField<T> getField() {
         return field;
      }

      public Object getInitExpectedValue() {
         return initExpectedValue;
      }

      public boolean isInitExpectedDirty() {
         return initExpectedDirty;
      }

      public Collection<TestData<T>> getTestDatas() {
         return testDatas;
      }

      public void doSetValue(TestData<T> testData) throws OseeCoreException {
         getField().set(testData.getSetValue());
      }

   }

   private final static class TestData<T> {
      private final T setValue;
      private final T expectedValue;
      private final boolean expectedDirty;
      private final boolean clearBeforeRun;
      private final Class<? extends Throwable> error;

      public TestData(boolean clearBeforeRun, T setValue, T expectedValue, boolean expectedDirty, Class<? extends Throwable> error) {
         this.clearBeforeRun = clearBeforeRun;
         this.setValue = setValue;
         this.expectedValue = expectedValue;
         this.expectedDirty = expectedDirty;
         this.error = error;
      }

      public TestData(boolean clearBeforeRun, T setValue, T expectedValue, boolean expectedDirty) {
         this(clearBeforeRun, setValue, expectedValue, expectedDirty, null);
      }

      public boolean isClearBeforeRun() {
         return clearBeforeRun;
      }

      public T getSetValue() {
         return setValue;
      }

      public T getExpectedValue() {
         return expectedValue;
      }

      public boolean isExpectedDirty() {
         return expectedDirty;
      }

      public boolean throwsError() {
         return getError() != null;
      }

      public Class<? extends Throwable> getError() {
         return error;
      }
   }
}
