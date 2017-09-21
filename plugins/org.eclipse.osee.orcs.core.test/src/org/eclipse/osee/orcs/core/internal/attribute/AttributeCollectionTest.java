/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeCollection}
 *
 * @author Roberto E. Escobar
 */
public class AttributeCollectionTest {

   // @formatter:off
   @Mock private AttributeExceptionFactory exceptionFactory;
   @Mock private Attribute<Object> dirtyAttr;
   @Mock private Attribute<Object> cleanAttr;
   @Mock private Attribute<Object> deletedAttr;
   // @formatter:on

   private AttributeCollection attributeCollection;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      attributeCollection = new AttributeCollection(exceptionFactory);

      attributeCollection.add(CoreAttributeTypes.Country, dirtyAttr);
      attributeCollection.add(CoreAttributeTypes.Active, cleanAttr);
      attributeCollection.add(CoreAttributeTypes.Annotation, deletedAttr);

      when(dirtyAttr.isDirty()).thenReturn(true);
      when(deletedAttr.isDeleted()).thenReturn(true);

      when(dirtyAttr.getModificationType()).thenReturn(ModificationType.MODIFIED);
      when(cleanAttr.getModificationType()).thenReturn(ModificationType.NEW);
      when(deletedAttr.getModificationType()).thenReturn(ModificationType.ARTIFACT_DELETED);
   }

   @Test
   public void testGetAttributesDirty() throws OseeCoreException {
      assertEquals(1, attributeCollection.getDirties().size());
      assertEquals(dirtyAttr, attributeCollection.getDirties().iterator().next());
   }

   @Test
   public void testHasAttributesDirty() {
      boolean actual1 = attributeCollection.hasDirty();
      assertTrue(actual1);

      attributeCollection.remove(CoreAttributeTypes.Country, dirtyAttr);

      boolean actual2 = attributeCollection.hasDirty();
      assertFalse(actual2);
   }

   @Test
   public void testGetAllAttributes() {
      Collection<Attribute<?>> attributes = attributeCollection.getAll();
      assertEquals(3, attributes.size());

      assertTrue(attributes.contains(dirtyAttr));
      assertTrue(attributes.contains(cleanAttr));
      assertTrue(attributes.contains(deletedAttr));
   }

   @Test
   public void testGetExistingTypes() throws OseeCoreException {
      AttributeType typeA = mock(AttributeType.class);
      AttributeType typeB = mock(AttributeType.class);
      AttributeType typeC = mock(AttributeType.class);

      when(dirtyAttr.getAttributeType()).thenReturn(typeA);
      when(dirtyAttr.isDeleted()).thenReturn(true);

      when(cleanAttr.getAttributeType()).thenReturn(typeB);
      when(cleanAttr.isDeleted()).thenReturn(true);

      when(deletedAttr.getAttributeType()).thenReturn(typeC);
      when(deletedAttr.isDeleted()).thenReturn(false);

      Collection<? extends AttributeTypeId> types = attributeCollection.getExistingTypes(DeletionFlag.INCLUDE_DELETED);

      assertEquals(3, types.size());

      assertTrue(types.contains(typeA));
      assertTrue(types.contains(typeB));
      assertTrue(types.contains(typeC));

      Collection<? extends AttributeTypeId> types2 = attributeCollection.getExistingTypes(DeletionFlag.EXCLUDE_DELETED);
      assertEquals(1, types2.size());

      assertFalse(types2.contains(typeA));
      assertFalse(types2.contains(typeB));
      assertTrue(types2.contains(typeC));
   }

   @Test
   public void testGetListDeletionFlag() throws OseeCoreException {
      List<Attribute<?>> list1 = attributeCollection.getList(DeletionFlag.INCLUDE_DELETED);

      assertEquals(3, list1.size());
      assertTrue(list1.contains(dirtyAttr));
      assertTrue(list1.contains(cleanAttr));
      assertTrue(list1.contains(deletedAttr));

      List<Attribute<?>> list2 = attributeCollection.getList(DeletionFlag.EXCLUDE_DELETED);
      assertEquals(2, list2.size());

      assertTrue(list2.contains(dirtyAttr));
      assertTrue(list2.contains(cleanAttr));
      assertFalse(list2.contains(deletedAttr));
   }

   @Test
   public void testGetSetDeletionFlag() throws OseeCoreException {
      ResultSet<Attribute<?>> set1 = attributeCollection.getResultSet(DeletionFlag.INCLUDE_DELETED);

      assertEquals(3, set1.size());
      checkContains(set1, dirtyAttr, true);
      checkContains(set1, cleanAttr, true);
      checkContains(set1, deletedAttr, true);

      ResultSet<Attribute<?>> set2 = attributeCollection.getResultSet(DeletionFlag.EXCLUDE_DELETED);
      assertEquals(2, set2.size());

      checkContains(set2, dirtyAttr, true);
      checkContains(set2, cleanAttr, true);
      checkContains(set2, deletedAttr, false);
   }

   @Test
   public void testGetListTypeAndDelete() throws OseeCoreException {
      List<Attribute<Object>> list1 =
         attributeCollection.getList(CoreAttributeTypes.Annotation, DeletionFlag.INCLUDE_DELETED);
      assertEquals(1, list1.size());
      assertTrue(list1.contains(deletedAttr));

      List<Attribute<Object>> list2 =
         attributeCollection.getList(CoreAttributeTypes.Annotation, DeletionFlag.EXCLUDE_DELETED);
      assertEquals(0, list2.size());
   }

   @Test
   public void testGetSetTypeAndDelete() throws OseeCoreException {
      ResultSet<Attribute<Object>> set1 =
         attributeCollection.getResultSet(CoreAttributeTypes.Annotation, DeletionFlag.INCLUDE_DELETED);

      assertEquals(1, set1.size());
      checkContains(set1, deletedAttr, true);

      ResultSet<Attribute<Object>> set2 =
         attributeCollection.getResultSet(CoreAttributeTypes.Annotation, DeletionFlag.EXCLUDE_DELETED);
      assertEquals(0, set2.size());
   }

   @Test
   public void testGetAttributeSetFromString() throws OseeCoreException {
      when(cleanAttr.getValue()).thenReturn(true);

      ResultSet<Attribute<Object>> set =
         attributeCollection.getAttributeSetFromString(CoreAttributeTypes.Active, DeletionFlag.EXCLUDE_DELETED, "true");
      Assert.assertEquals(cleanAttr, set.getExactlyOne());
   }

   @Test
   public void testGetAttributeSetFromValue() throws OseeCoreException {
      when(cleanAttr.getValue()).thenReturn(true);

      ResultSet<Attribute<Boolean>> set =
         attributeCollection.getAttributeSetFromValue(CoreAttributeTypes.Active, DeletionFlag.EXCLUDE_DELETED, true);
      Assert.assertEquals(cleanAttr, set.getExactlyOne());
   }

   private void checkContains(Iterable<? extends Attribute<?>> items, Attribute<Object> toFind, boolean findExpected) {
      boolean actual = Iterables.contains(items, toFind);
      assertEquals(findExpected, actual);
   }
}
