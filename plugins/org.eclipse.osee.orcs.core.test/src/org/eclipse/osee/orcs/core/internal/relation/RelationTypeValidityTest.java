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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link RelationTypeValidity}
 * 
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class RelationTypeValidityTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private RelationTypes cache;
   @Mock private RelationNode node;
   @Mock private IArtifactType artifactType;   
   @Mock private IArtifactType artifactType2;
   @Mock private IRelationType relationType1;
   @Mock private IRelationType relationType2;
   @Mock private IRelationType relationType3;
   @Mock private IRelationType relationType4;
   @Mock private IRelationTypeSide typeSide1;
   // @formatter:on

   private RelationTypeValidity validity;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      validity = new RelationTypeValidity(cache);

      when(typeSide1.getGuid()).thenReturn(11L);
      when(typeSide1.getSide()).thenReturn(RelationSide.SIDE_B);
      when(typeSide1.getName()).thenReturn("typeSide1");

      when(cache.getByUuid(typeSide1.getGuid())).thenReturn(relationType1);
   }

   @Test
   public void testMaximumRelationAllowedNullArtifactType() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getMaximumRelationsAllowed(null, typeSide1);
   }

   @Test
   public void testMaximumRelationAllowedNullRelationTypeSide() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("relationTypeSide cannot be null");
      validity.getMaximumRelationsAllowed(artifactType, null);
   }

   @Test
   public void testValidRelationTypesNullArtifactType() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getValidRelationTypes(null);
   }

   @Test
   public void testMaximumRelationAllowed1() throws OseeCoreException {
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_B, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.MANY_TO_MANY);

      int actual = validity.getMaximumRelationsAllowed(artifactType, typeSide1);
      assertEquals(Integer.MAX_VALUE, actual);
   }

   @Test
   public void testMaximumRelationAllowed2() throws OseeCoreException {
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_B, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.MANY_TO_ONE);

      int actual = validity.getMaximumRelationsAllowed(artifactType, typeSide1);
      assertEquals(1, actual);
   }

   @Test
   public void testMaximumRelationAllowed3() throws OseeCoreException {
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_A, artifactType)).thenReturn(true);

      int actual = validity.getMaximumRelationsAllowed(artifactType, typeSide1);
      assertEquals(0, actual);
   }

   @Test
   public void testValidRelationTypes() throws OseeCoreException {
      final Collection<? extends IRelationType> types =
         Arrays.asList(relationType1, relationType2, relationType3, relationType4);
      when(cache.getAll()).thenAnswer(new Answer<Collection<? extends IRelationType>>() {

         @Override
         public Collection<? extends IRelationType> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }

      });
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_B, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.MANY_TO_MANY);

      when(cache.isArtifactTypeAllowed(relationType2, RelationSide.SIDE_A, artifactType)).thenReturn(false);
      when(cache.getMultiplicity(relationType2)).thenReturn(RelationTypeMultiplicity.MANY_TO_ONE);

      when(cache.isArtifactTypeAllowed(relationType3, RelationSide.SIDE_A, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType3)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);

      when(cache.isArtifactTypeAllowed(relationType4, RelationSide.SIDE_A, artifactType)).thenReturn(false);
      when(cache.getMultiplicity(relationType4)).thenReturn(RelationTypeMultiplicity.ONE_TO_MANY);

      List<IRelationType> actual = validity.getValidRelationTypes(artifactType);

      assertEquals(2, actual.size());
      assertTrue(actual.contains(relationType1));
      assertFalse(actual.contains(relationType2));
      assertTrue(actual.contains(relationType3));
      assertFalse(actual.contains(relationType4));
   }

   @Test
   public void testGetRelationMultiplicityState() throws OseeCoreException {
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);

      MultiplicityState state = validity.getRelationMultiplicityState(typeSide1, 0);
      assertEquals(MultiplicityState.IS_VALID, state);

      state = validity.getRelationMultiplicityState(typeSide1, 1);
      assertEquals(MultiplicityState.IS_VALID, state);

      state = validity.getRelationMultiplicityState(typeSide1, 2);
      assertEquals(MultiplicityState.MAX_VIOLATION, state);
   }

   @Test
   public void testCheckRelationTypeMultiplicity() throws OseeCoreException {
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeStateException.class);
      thrown.expectMessage(String.format("Relation type [%s] exceeds max occurrence rule on [node message]", typeSide1,
         node.getExceptionString()));

      validity.checkRelationTypeMultiplicity(node, typeSide1, 2);
   }

   @Test
   public void testCheckRelationTypeMultiplicityNoException() throws OseeCoreException {
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);
      when(node.getExceptionString()).thenReturn("node message");

      ExpectedException.none();
      validity.checkRelationTypeMultiplicity(node, typeSide1, 0);
   }

   @Test
   public void testIsRelationTypeValid() throws OseeCoreException {
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_A, artifactType)).thenReturn(true);

      boolean actual = validity.isRelationTypeValid(artifactType, typeSide1);
      assertEquals(false, actual);

      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_B, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);

      actual = validity.isRelationTypeValid(artifactType, typeSide1);
      assertEquals(true, actual);
   }

   @Test
   public void testCheckRelationTypeValid() throws OseeCoreException {
      when(artifactType.toString()).thenReturn("artType1");
      when(artifactType2.toString()).thenReturn("artType2");

      when(relationType1.toString()).thenReturn("relationType1Name");
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_A, artifactType)).thenReturn(true);

      when(cache.getArtifactType(relationType1, RelationSide.SIDE_B)).thenReturn(artifactType2);

      when(node.getArtifactType()).thenReturn(artifactType);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Relation validity error for [node message] - ArtifactType [artType1] does not belong on side [SIDE_B] of relation [relationType1Name] - only items of type [artType2] are allowed");
      validity.checkRelationTypeValid(node, typeSide1);
   }

   @Test
   public void testCheckRelationTypeValidNoException() throws OseeCoreException {
      when(node.getArtifactType()).thenReturn(artifactType);
      when(cache.isArtifactTypeAllowed(relationType1, RelationSide.SIDE_B, artifactType)).thenReturn(true);
      when(cache.getMultiplicity(relationType1)).thenReturn(RelationTypeMultiplicity.ONE_TO_ONE);

      ExpectedException.none();
      validity.checkRelationTypeValid(node, typeSide1);
   }

}
