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
package org.eclipse.osee.orcs.core.internal.relation;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Design_Design;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DirtyState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link Relation}
 *
 * @author Roberto E. Escobar
 */
public class RelationTest {

   // @formatter:off
   @Mock private RelationTypes relationTypes;
   @Mock RelationData data;
   // @formatter:on

   private Relation relation;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      relation = new Relation(data);
      when(data.getType()).thenReturn(Design_Design);
   }

   @Test
   public void testDelete() {
      assertFalse(relation.isDirty());
      relation.delete();

      verify(data).setModType(ModificationType.DELETED);
      when(data.isDirty()).thenReturn(true);
      assertTrue(relation.isDirty());
   }

   @Test
   public void testGetSetRationale() {
      assertFalse(relation.isDirty());
      Mockito.when(data.getRationale()).thenReturn("rationale");

      String rationale = relation.getRationale();

      verify(data).getRationale();
      assertEquals("rationale", rationale);
      assertFalse(relation.isDirty());

      relation.setRationale("new rationale");
      verify(data).setRationale("new rationale");
      verify(data).setModType(ModificationType.MODIFIED);
      when(data.isDirty()).thenReturn(true);
      assertTrue(relation.isDirty());
   }

   @Test
   public void testGetModificationType() {
      relation.getModificationType();
      verify(data).getModType();
   }

   @Test
   public void testGetOrcsData() {
      assertEquals(data, relation.getOrcsData());
   }

   @Test
   public void testDirty() {
      when(data.calculateDirtyState(true)).thenReturn(DirtyState.OTHER_CHANGES);
      when(data.isDirty()).thenReturn(true);
      relation.setDirty();
      assertTrue(relation.isDirty());
      verify(data, times(1)).calculateDirtyState(true);

      when(data.calculateDirtyState(false)).thenReturn(DirtyState.CLEAN);
      when(data.isDirty()).thenReturn(false);
      relation.clearDirty();
      assertFalse(relation.isDirty());
      verify(data, times(1)).calculateDirtyState(false);
   }

   @Test
   public void testGetLocalIdForSide() {
      ArtifactId artifactId33 = ArtifactId.valueOf(33);
      ArtifactId artifactId45 = ArtifactId.valueOf(45);

      when(data.getArtIdOn(RelationSide.SIDE_A)).thenReturn(artifactId45);
      when(data.getArtIdOn(RelationSide.SIDE_B)).thenReturn(artifactId33);

      assertEquals(artifactId45, relation.getIdForSide(RelationSide.SIDE_A));
      assertEquals(artifactId33, relation.getIdForSide(RelationSide.SIDE_B));
   }

   @Test
   public void testIsDeleteD() {
      when(data.getModType()).thenReturn(ModificationType.ARTIFACT_DELETED);
      assertTrue(relation.isDeleted());

      when(data.getModType()).thenReturn(ModificationType.DELETED);
      assertTrue(relation.isDeleted());

      when(data.getModType()).thenReturn(ModificationType.MODIFIED);
      assertFalse(relation.isDeleted());
   }

   @Test
   public void testIsOfType() {
      assertTrue(relation.isOfType(Design_Design));
      assertFalse(relation.isOfType(CoreRelationTypes.DEFAULT_HIERARCHY));
   }

}
