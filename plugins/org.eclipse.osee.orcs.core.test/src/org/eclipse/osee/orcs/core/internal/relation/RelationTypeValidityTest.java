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

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_ONE;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_ONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link RelationTypeValidity}
 *
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class RelationTypeValidityTest {

   private static final RelationTypeToken TYPE_1 = TokenFactory.createRelationType(123456789L, "TYPE_1");

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private RelationTypes relTypes;
   @Mock private Artifact node;
   @Mock private IArtifactType artifactType;
   @Mock private IArtifactType artifactType2;

   @Mock private IRelationType relationType1;
   @Mock private IRelationType relationType2;
   @Mock private IRelationType relationType3;
   @Mock private IRelationType relationType4;
   // @formatter:on

   private RelationTypeValidity validity;

   @Before
   public void init() {
      initMocks(this);
      validity = new RelationTypeValidity(relTypes);
      when(relTypes.exists(TYPE_1)).thenReturn(true);
   }

   @Test
   public void testMaximumRelationAllowedNullArtifactType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getMaximumRelationsAllowed(TYPE_1, null, SIDE_A);
   }

   @Test
   public void testMaximumRelationAllowedNullRelationType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("relationType cannot be null");
      validity.getMaximumRelationsAllowed(null, artifactType, SIDE_B);
   }

   @Test
   public void testMaximumRelationAllowedNullRelationSide() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("relationSide cannot be null");
      validity.getMaximumRelationsAllowed(TYPE_1, artifactType, null);
   }

   @Test
   public void testMaximumRelationAllowedTypeDoesNotExist() {
      when(relTypes.exists(TYPE_1)).thenReturn(false);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("relationType [%s] does not exist", TYPE_1));
      validity.getMaximumRelationsAllowed(TYPE_1, artifactType, SIDE_A);
   }

   @Test
   public void testValidRelationTypesNullArtifactType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getValidRelationTypes(null);
   }

   @Test
   public void testMaximumRelationAllowed1() {
      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_B, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(MANY_TO_MANY);

      int actual = validity.getMaximumRelationsAllowed(TYPE_1, artifactType, SIDE_B);
      assertEquals(Integer.MAX_VALUE, actual);
   }

   @Test
   public void testMaximumRelationAllowed2() {
      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_B, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(MANY_TO_ONE);

      int actual = validity.getMaximumRelationsAllowed(TYPE_1, artifactType, SIDE_B);
      assertEquals(1, actual);
   }

   @Test
   public void testMaximumRelationAllowed3() {
      when(relTypes.isArtifactTypeAllowed(relationType1, SIDE_A, artifactType)).thenReturn(true);

      int actual = validity.getMaximumRelationsAllowed(TYPE_1, artifactType, SIDE_B);
      assertEquals(0, actual);
   }

   @Test
   public void testValidRelationTypes() {
      final Collection<? extends IRelationType> types =
         Arrays.asList(relationType1, relationType2, relationType3, relationType4);
      when(relTypes.getAll()).thenAnswer(new Answer<Collection<? extends IRelationType>>() {

         @Override
         public Collection<? extends IRelationType> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }

      });
      when(relTypes.isArtifactTypeAllowed(relationType1, SIDE_B, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(relationType1)).thenReturn(MANY_TO_MANY);

      when(relTypes.isArtifactTypeAllowed(relationType2, SIDE_A, artifactType)).thenReturn(false);
      when(relTypes.getMultiplicity(relationType2)).thenReturn(MANY_TO_ONE);

      when(relTypes.isArtifactTypeAllowed(relationType3, SIDE_A, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(relationType3)).thenReturn(ONE_TO_ONE);

      when(relTypes.isArtifactTypeAllowed(relationType4, SIDE_A, artifactType)).thenReturn(false);
      when(relTypes.getMultiplicity(relationType4)).thenReturn(ONE_TO_MANY);

      List<RelationTypeId> actual = validity.getValidRelationTypes(artifactType);

      assertEquals(2, actual.size());
      assertTrue(actual.contains(relationType1));
      assertFalse(actual.contains(relationType2));
      assertTrue(actual.contains(relationType3));
      assertFalse(actual.contains(relationType4));
   }

   @Test
   public void testGetRelationMultiplicityState() {
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(ONE_TO_ONE);

      MultiplicityState state = validity.getRelationMultiplicityState(TYPE_1, SIDE_B, 0);
      assertEquals(MultiplicityState.IS_VALID, state);

      state = validity.getRelationMultiplicityState(TYPE_1, SIDE_B, 1);
      assertEquals(MultiplicityState.IS_VALID, state);

      state = validity.getRelationMultiplicityState(TYPE_1, SIDE_B, 2);
      assertEquals(MultiplicityState.MAX_VIOLATION, state);
   }

   @Test
   public void testCheckRelationTypeMultiplicity() {
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(ONE_TO_ONE);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeStateException.class);
      thrown.expectMessage(String.format("Relation type [%s] on [%s] exceeds max occurrence rule on [node message]",
         TYPE_1, SIDE_A, node.getExceptionString()));

      validity.checkRelationTypeMultiplicity(TYPE_1, node, SIDE_A, 2);
   }

   @Test
   public void testCheckRelationTypeMultiplicityNoException() {
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(ONE_TO_ONE);
      when(node.getExceptionString()).thenReturn("node message");

      ExpectedException.none();
      validity.checkRelationTypeMultiplicity(TYPE_1, node, SIDE_A, 0);
   }

   @Test
   public void testIsRelationTypeValid() {
      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_A, artifactType)).thenReturn(true);

      boolean actual = validity.isRelationTypeValid(TYPE_1, artifactType, SIDE_B);
      assertEquals(false, actual);

      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_B, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(ONE_TO_ONE);

      actual = validity.isRelationTypeValid(TYPE_1, artifactType, SIDE_B);
      assertEquals(true, actual);
   }

   @Test
   public void testCheckRelationTypeValid() {
      when(artifactType.toString()).thenReturn("artType1");
      when(artifactType2.toString()).thenReturn("artType2");

      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_A, artifactType)).thenReturn(true);
      when(relTypes.getArtifactType(TYPE_1, SIDE_B)).thenReturn(artifactType2);

      when(node.getArtifactTypeId()).thenReturn(artifactType);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(
         "Relation validity error for [node message] - ArtifactType [artType1] does not belong on side [SIDE_B] of relation [TYPE_1] - only items of type [artType2] are allowed");
      validity.checkRelationTypeValid(TYPE_1, node, SIDE_B);
   }

   @Test
   public void testCheckRelationTypeValidNoException() {
      when(node.getArtifactTypeId()).thenReturn(artifactType);
      when(relTypes.isArtifactTypeAllowed(TYPE_1, SIDE_B, artifactType)).thenReturn(true);
      when(relTypes.getMultiplicity(TYPE_1)).thenReturn(ONE_TO_ONE);

      ExpectedException.none();
      validity.checkRelationTypeValid(TYPE_1, node, SIDE_B);
   }

}
