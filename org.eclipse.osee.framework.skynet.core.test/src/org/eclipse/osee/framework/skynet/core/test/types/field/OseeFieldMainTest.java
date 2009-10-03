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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.test.types.MockShallowArtifact;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTestDataAccessor;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTypesUtil;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.types.IOseeField;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.field.AliasesField;
import org.eclipse.osee.framework.skynet.core.types.field.ArtifactSuperTypeField;
import org.eclipse.osee.framework.skynet.core.types.field.ArtifactTypeAttributesField;
import org.eclipse.osee.framework.skynet.core.types.field.AssociatedArtifactField;
import org.eclipse.osee.framework.skynet.core.types.field.ChangeUtil;
import org.eclipse.osee.framework.skynet.core.types.field.EnumEntryField;
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
      Assert.assertFalse(String.format("%s expected[%s] actual[%s]", message, expected, actual),
            ChangeUtil.isDifferent(expected, actual));
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
      data.add(createBranchFieldTest(factory));
      data.add(createAssociatedArtifactTest(factory));
      data.add(createEnumEntryFieldTest(factory));
      data.add(createSuperArtifactTypeFieldTest(factory));
      data.add(createArtifactTypeAttributesFieldTest(factory));
      return data;
   }

   @SuppressWarnings("unchecked")
   private static Object[] createBranchFieldTest(IOseeTypeFactory factory) throws OseeCoreException {
      BranchCache branchCache = new BranchCache(factory, new OseeTestDataAccessor<Branch>());
      Branch branch =
            OseeTypesUtil.createBranch(branchCache, factory, null, "Test Branch", BranchType.WORKING,
                  BranchState.MODIFIED, false);
      branch.setAliases("Alias 1", "Alias2");

      Collection<String> emptyList = Collections.emptyList();
      return new Object[] {//
      new OseeFieldTest<Collection<String>>(
            //
            new AliasesField(branchCache, branch), //
            Arrays.asList("alias 1", "alias2"),
            false,//
            new TestData<Collection<String>>(false, Arrays.asList("alias2", "alias 1"), Arrays.asList("alias 1",
                  "alias2"), false),//
            new TestData<Collection<String>>(false, Arrays.asList("alias 1"), Arrays.asList("alias 1"), true),
            new TestData<Collection<String>>(true, emptyList, emptyList, true)//
      )};
   }

   @SuppressWarnings("unchecked")
   private static Object[] createAssociatedArtifactTest(IOseeTypeFactory factory) throws OseeCoreException {
      BranchCache branchCache = new BranchCache(factory, new OseeTestDataAccessor<Branch>());
      Branch branch =
            OseeTypesUtil.createBranch(branchCache, factory, null, "Test Branch", BranchType.WORKING,
                  BranchState.MODIFIED, false);

      IArtifact defaultAssociatedArtifact = null;
      branchCache.setDefaultAssociatedArtifact(defaultAssociatedArtifact);

      IArtifact artifact1 = new MockShallowArtifact(branchCache, 100);
      IArtifact artifact2 = new MockShallowArtifact(branchCache, 200);
      return new Object[] {new OseeFieldTest<IArtifact>( //
            new AssociatedArtifactField(branchCache, branch), //
            null, false, //
            new TestData<IArtifact>(false, artifact1, artifact1, true), //
            new TestData<IArtifact>(false, artifact2, artifact2, true), //
            new TestData<IArtifact>(true, null, null, true), //
            new TestData<IArtifact>(true, artifact1, artifact1, true), //
            new TestData<IArtifact>(true, artifact1, artifact1, false)//
      )};
   }

   @SuppressWarnings("unchecked")
   private static Object[] createEnumEntryFieldTest(IOseeTypeFactory factory) throws OseeCoreException {
      OseeEnumTypeCache enumTypeCache = new OseeEnumTypeCache(factory, new OseeTestDataAccessor<OseeEnumType>());
      OseeEnumType oseeEnumType = OseeTypesUtil.createEnumType(enumTypeCache, factory, null, "Enum Data 1");
      OseeEnumEntry entry1 = factory.createEnumEntry(enumTypeCache, null, "Entry 1", 1);
      OseeEnumEntry entry2 = factory.createEnumEntry(enumTypeCache, null, "Entry 2", 2);
      OseeEnumEntry entry3 = factory.createEnumEntry(enumTypeCache, null, "Entry 3", 3);

      oseeEnumType.setEntries(Arrays.asList(entry1, entry2, entry3));
      oseeEnumType.clearDirty();

      List<OseeEnumEntry> emptyEnumList = Collections.emptyList();

      return new Object[] { //
      new OseeFieldTest<List<OseeEnumEntry>>(
            new EnumEntryField(//
                  enumTypeCache, oseeEnumType),//
            Arrays.asList(entry1, entry2, entry3),
            false, //
            new TestData<List<OseeEnumEntry>>(false, Arrays.asList(entry1, entry2, entry3), Arrays.asList(entry1,
                  entry2, entry3), false), //
            new TestData<List<OseeEnumEntry>>(false, Arrays.asList(entry1, entry2, entry2, entry3), Arrays.asList(
                  entry1, entry2, entry3), false), //
            new TestData<List<OseeEnumEntry>>(false, Arrays.asList(entry1, entry3), Arrays.asList(entry1, entry3), true),//
            new TestData<List<OseeEnumEntry>>(true, emptyEnumList, emptyEnumList, true), //
            new TestData<List<OseeEnumEntry>>(true, Arrays.asList(entry1, entry2, entry3), Arrays.asList(entry1,
                  entry2, entry3), true) //
      )};
   }

   @SuppressWarnings("unchecked")
   private static Object[] createSuperArtifactTypeFieldTest(IOseeTypeFactory factory) throws OseeCoreException {
      ArtifactTypeCache artTypeCache = new ArtifactTypeCache(factory, new OseeTestDataAccessor<ArtifactType>());
      ArtifactType artifactType = factory.createArtifactType(artTypeCache, null, false, "Test Artifact Type");

      ArtifactType art1 = factory.createArtifactType(artTypeCache, null, false, "Art 1");
      ArtifactType art2 = factory.createArtifactType(artTypeCache, null, false, "Art 2");
      ArtifactType art3 = factory.createArtifactType(artTypeCache, null, false, "Art 3");

      List<ArtifactType> emptyArtTypeList = Collections.emptyList();
      return new Object[] {new OseeFieldTest<Collection<ArtifactType>>( //
            new ArtifactSuperTypeField(artTypeCache, artifactType), //
            emptyArtTypeList, false, //
            new TestData<Collection<ArtifactType>>(false, Arrays.asList(art1, art2, art3), Arrays.asList(art1, art2,
                  art3), true), //
            new TestData<Collection<ArtifactType>>(true, Arrays.asList(art2, art3, art1), Arrays.asList(art1, art2,
                  art3), false), //
            new TestData<Collection<ArtifactType>>(false, Arrays.asList(art1, art2, art3, art1), Arrays.asList(art1,
                  art2, art3), false), //
            new TestData<Collection<ArtifactType>>(false, emptyArtTypeList, Arrays.asList(art1, art2, art3), false,
                  OseeInvalidInheritanceException.class), //
            new TestData<Collection<ArtifactType>>(false, Arrays.asList(art1), Arrays.asList(art1), true) //
      )};
   }

   @SuppressWarnings("unchecked")
   private static Object[] createArtifactTypeAttributesFieldTest(IOseeTypeFactory factory) throws OseeCoreException {
      ArtifactTypeCache artTypeCache = new ArtifactTypeCache(factory, new OseeTestDataAccessor<ArtifactType>());
      ArtifactType artifactType = factory.createArtifactType(artTypeCache, null, false, "Test Super Artifact Type");

      AttributeTypeCache attrCache = new AttributeTypeCache(factory, new OseeTestDataAccessor<AttributeType>());
      AttributeType attr1 = OseeTypesUtil.createAttributeType(attrCache, factory, null, "Attribute Type 1");
      AttributeType attr2 = OseeTypesUtil.createAttributeType(attrCache, factory, null, "Attribute Type 2");
      AttributeType attr3 = OseeTypesUtil.createAttributeType(attrCache, factory, null, "Attribute Type 3");

      BranchCache branchCache = new BranchCache(factory, new OseeTestDataAccessor<Branch>());
      Branch br1 =
            OseeTypesUtil.createBranch(branchCache, factory, null, "Dummy Branch", BranchType.SYSTEM_ROOT,
                  BranchState.CREATED, false);
      Branch br2 =
            OseeTypesUtil.createBranch(branchCache, factory, null, "Dummy Branch2", BranchType.WORKING,
                  BranchState.MODIFIED, false);

      artifactType.setAttributeTypeValidity(Arrays.asList(attr1, attr2, attr3), br1);
      artifactType.clearDirty();
      Map<Branch, Collection<AttributeType>> emptyMap = Collections.emptyMap();

      Map<Branch, Collection<AttributeType>> twoEntry = new HashMap<Branch, Collection<AttributeType>>();
      twoEntry.putAll(map(br2, attr3));
      twoEntry.putAll(map(br1, attr2));

      return new Object[] {new OseeFieldTest<Map<Branch, Collection<AttributeType>>>(
            new ArtifactTypeAttributesField(artTypeCache, artifactType),//
            map(br1, attr1, attr2, attr3),
            false, //
            new TestData<Map<Branch, Collection<AttributeType>>>(false, map(br1, attr3, attr1, attr2), map(br1, attr1,
                  attr2, attr3), false), //
            new TestData<Map<Branch, Collection<AttributeType>>>(false, map(br1, attr3, attr1, attr2, attr1), map(br1,
                  attr1, attr2, attr3), false), //
            new TestData<Map<Branch, Collection<AttributeType>>>(false, map(br1, attr1), map(br1, attr1), true), //
            new TestData<Map<Branch, Collection<AttributeType>>>(true, map(br1), emptyMap, true), //
            new TestData<Map<Branch, Collection<AttributeType>>>(true, map(br2, attr3), map(br2, attr3), true),
            new TestData<Map<Branch, Collection<AttributeType>>>(true, map(br1, attr2), twoEntry, true)//
      )};//
   }

   private static Map<Branch, Collection<AttributeType>> map(Branch branch, AttributeType... attrs) {
      Map<Branch, Collection<AttributeType>> map = new LinkedHashMap<Branch, Collection<AttributeType>>();
      map.put(branch, Arrays.asList(attrs));
      return map;
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
