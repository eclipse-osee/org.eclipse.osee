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
package org.eclipse.osee.orcs.core.internal.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.attributeContainsPattern;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.attributeStringEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.attributeValueEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.deletionFlagEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.excludeDeleted;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.includeDeleted;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.isDirty;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.isNotDirty;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.nodeIdOnSideEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.nodeIdsEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import com.google.common.base.Predicate;
import java.util.Date;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.data.HasDeleteState;
import org.eclipse.osee.orcs.data.Modifiable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link OrcsPredicates}
 * 
 * @author Roberto E. Escobar
 */
public class OrcsPredicatesTest {

   // @formatter:off
   @Mock private Modifiable dirty;
   @Mock private Modifiable notDirty;
   
   @Mock private HasDeleteState deleted;
   @Mock private HasDeleteState notDeleted;
   
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute1;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute2;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute3;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute4;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute5;
   
   @Mock private HasLocalId localId1;
   @Mock private HasLocalId localId2;
   
   @Mock private Relation relation1;
   // @formatter:on

   private Date date;

   @Before
   public void init()  {
      MockitoAnnotations.initMocks(this);

      when(dirty.isDirty()).thenReturn(true);
      when(notDirty.isDirty()).thenReturn(false);

      when(deleted.getModificationType()).thenReturn(ModificationType.ARTIFACT_DELETED);
      when(deleted.isDeleted()).thenReturn(true);

      when(notDeleted.getModificationType()).thenReturn(ModificationType.NEW);
      when(notDeleted.isDeleted()).thenReturn(false);

      date = new Date();

      when(attribute1.getValue()).thenReturn(45789L);
      when(attribute2.getValue()).thenReturn(true);
      when(attribute3.getValue()).thenReturn(date);
      when(attribute4.getValue()).thenReturn("Hello");
      when(attribute5.getValue()).thenReturn(true);

      when(localId1.getLocalId()).thenReturn(11);
      when(localId2.getLocalId()).thenReturn(22);

      when(relation1.getLocalIdForSide(RelationSide.SIDE_A)).thenReturn(11);
      when(relation1.getLocalIdForSide(RelationSide.SIDE_B)).thenReturn(22);
   }

   @Test
   public void testAcceptDirties() {
      assertFalse(isDirty().apply(notDirty));
      assertTrue(isDirty().apply(dirty));
   }

   @Test
   public void testAcceptNoneDirties() {
      assertTrue(isNotDirty().apply(notDirty));
      assertFalse(isNotDirty().apply(dirty));
   }

   @Test
   public void testIncludeDeleted() {
      assertTrue(includeDeleted().apply(deleted));
      assertTrue(includeDeleted().apply(notDeleted));
   }

   @Test
   public void testExcludeDeleted() {
      assertFalse(excludeDeleted().apply(deleted));
      assertTrue(excludeDeleted().apply(notDeleted));
   }

   @Test
   public void testDeletionFlag() {
      when(attribute1.getModificationType()).thenReturn(ModificationType.ARTIFACT_DELETED);
      when(attribute1.isDeleted()).thenReturn(true);
      when(attribute2.getModificationType()).thenReturn(ModificationType.NEW);
      when(attribute2.isDeleted()).thenReturn(false);

      assertFalse(deletionFlagEquals(EXCLUDE_DELETED).apply(attribute1));
      assertTrue(deletionFlagEquals(EXCLUDE_DELETED).apply(attribute2));

      assertTrue(deletionFlagEquals(INCLUDE_DELETED).apply(attribute1));
      assertTrue(deletionFlagEquals(INCLUDE_DELETED).apply(attribute2));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAttributeStringEquals()  {
      assertTrue(attributeStringEquals("45789").apply(attribute1));
      assertTrue(attributeStringEquals("true").apply(attribute2));
      assertTrue(attributeStringEquals(date.toString()).apply(attribute3));

      assertFalse(attributeStringEquals("Helo").apply(attribute1));
      assertFalse(attributeStringEquals("Hello").apply(attribute2));

      Date date2 = new Date(123123111231L);

      assertFalse(attributeStringEquals(date2.toString()).apply(attribute3));

      assertFalse(attributeStringEquals("true").apply(attribute1));
      assertFalse(attributeStringEquals("false").apply(attribute1));
      assertFalse(attributeStringEquals("false").apply(attribute2));

      when(attribute1.getValue()).thenReturn((String) null);

      assertTrue(attributeStringEquals(null).apply(attribute1));
      assertFalse(attributeStringEquals("null").apply(attribute1));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAttributeValueEquals() {
      assertTrue(attributeValueEquals("Hello").apply(attribute4));
      assertTrue(attributeValueEquals(true).apply(attribute5));

      assertFalse(attributeValueEquals("Helo").apply(attribute4));
      assertFalse(attributeValueEquals("Hello").apply(attribute5));

      assertFalse(attributeValueEquals(true).apply(attribute4));
      assertFalse(attributeValueEquals(false).apply(attribute4));
      assertFalse(attributeValueEquals(false).apply(attribute5));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAttributeContainsPattern()  {
      when(attribute1.getValue()).thenReturn("123-456-7890", "00-000-0000", "000-000-0000");

      Predicate<Attribute<CharSequence>> telAttribute = attributeContainsPattern("[0-9]{3}[-][0-9]{3}[-][0-9]{4}");

      assertTrue(telAttribute.apply(attribute1));
      assertFalse(telAttribute.apply(attribute1));
      assertTrue(telAttribute.apply(attribute1));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAttributeContainsPattern2()  {
      Pattern pattern = Pattern.compile("[0-9]{3}[-][0-9]{3}[-][0-9]{4}");
      when(attribute1.getValue()).thenReturn("123-456-7890", "00-000-0000", "000-000-0000");

      Predicate<Attribute<CharSequence>> telAttribute = attributeContainsPattern(pattern);

      assertTrue(telAttribute.apply(attribute1));
      assertFalse(telAttribute.apply(attribute1));
      assertTrue(telAttribute.apply(attribute1));
   }

   @Test
   public void testNodeIdOnSideEquals() {
      assertTrue(nodeIdOnSideEquals(localId1, RelationSide.SIDE_A).apply(relation1));
      assertTrue(nodeIdOnSideEquals(localId2, RelationSide.SIDE_B).apply(relation1));
      assertFalse(nodeIdOnSideEquals(localId2, RelationSide.SIDE_A).apply(relation1));
      assertFalse(nodeIdOnSideEquals(localId1, RelationSide.SIDE_B).apply(relation1));
   }

   @Test
   public void testNodeIdEquals() {
      assertTrue(nodeIdsEquals(localId1, localId2).apply(relation1));
      assertFalse(nodeIdsEquals(localId2, localId1).apply(relation1));
   }
}
