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
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.util.MultiplicityState;
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

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Artifact node;
   @Mock private ArtifactTypeToken artifactType;
   @Mock private ArtifactTypeToken artifactType2;
   @Mock private OrcsTokenService tokenService;

   @Mock private RelationTypeToken relationType1;
   @Mock private RelationTypeToken relationType2;
   @Mock private RelationTypeToken relationType3;
   @Mock private RelationTypeToken relationType4;
   // @formatter:on

   private RelationTypeValidity validity;

   @Before
   public void init() {
      initMocks(this);
      validity = new RelationTypeValidity(tokenService);
   }

   @Test
   public void testMaximumRelationAllowedNullArtifactType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getMaximumRelationsAllowed(CoreRelationTypes.Allocation_Requirement, null, SIDE_A);
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
      validity.getMaximumRelationsAllowed(CoreRelationTypes.Allocation_Requirement, artifactType, null);
   }

   @Test
   public void testValidRelationTypesNullArtifactType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("artifactType cannot be null");
      validity.getValidRelationTypes(null);
   }

   @Test
   public void testMaximumRelationAllowed() {
      int actual = validity.getMaximumRelationsAllowed(CoreRelationTypes.Allocation_Requirement,
         CoreArtifactTypes.Component, SIDE_B);
      assertEquals(Integer.MAX_VALUE, actual);

      actual = validity.getMaximumRelationsAllowed(CoreRelationTypes.Allocation_Requirement, CoreArtifactTypes.CodeUnit,
         SIDE_B);
      assertEquals(0, actual);
   }

   @Test
   public void testValidRelationTypes() {
      final Collection<? extends RelationTypeToken> types =
         Arrays.asList(relationType1, relationType2, relationType3, relationType4);
      when(tokenService.getRelationTypes()).thenAnswer(new Answer<Collection<? extends RelationTypeToken>>() {

         @Override
         public Collection<? extends RelationTypeToken> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }

      });
      when(relationType1.isArtifactTypeAllowed(SIDE_B, artifactType)).thenReturn(true);
      when(relationType1.getMultiplicity()).thenReturn(MANY_TO_MANY);

      when(relationType2.isArtifactTypeAllowed(SIDE_A, artifactType)).thenReturn(false);
      when(relationType2.getMultiplicity()).thenReturn(MANY_TO_ONE);

      when(relationType3.isArtifactTypeAllowed(SIDE_A, artifactType)).thenReturn(true);
      when(relationType3.getMultiplicity()).thenReturn(ONE_TO_ONE);

      when(relationType4.isArtifactTypeAllowed(SIDE_A, artifactType)).thenReturn(false);
      when(relationType4.getMultiplicity()).thenReturn(ONE_TO_MANY);

      List<RelationTypeToken> actual = validity.getValidRelationTypes(artifactType);

      assertEquals(2, actual.size());
      assertTrue(actual.contains(relationType1));
      assertFalse(actual.contains(relationType2));
      assertTrue(actual.contains(relationType3));
      assertFalse(actual.contains(relationType4));
   }

   @Test
   public void testGetRelationMultiplicityState() {
      MultiplicityState state =
         validity.getRelationMultiplicityState(CoreRelationTypes.Allocation_Requirement, SIDE_B, 0);
      assertEquals(MultiplicityState.IS_VALID, state);

      state = validity.getRelationMultiplicityState(CoreRelationTypes.Allocation_Requirement, SIDE_B, 1);
      assertEquals(MultiplicityState.IS_VALID, state);
   }

   @Test
   public void testCheckRelationTypeMultiplicity() {
      when(relationType1.getMultiplicity()).thenReturn(ONE_TO_ONE);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeStateException.class);
      thrown.expectMessage(String.format("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]",
         relationType1, SIDE_A, node.getExceptionString()));

      validity.checkRelationTypeMultiplicity(relationType1, node, SIDE_A, 3);
   }

   @Test
   public void testCheckRelationTypeMultiplicityNoException() {
      when(node.getExceptionString()).thenReturn("node message");

      ExpectedException.none();
      validity.checkRelationTypeMultiplicity(CoreRelationTypes.Allocation_Requirement, node, SIDE_A, 0);
   }

   @Test
   public void testIsRelationTypeValid() {
      boolean actual = validity.isRelationTypeValid(CoreRelationTypes.Allocation_Requirement,
         CoreArtifactTypes.EnumeratedArtifact, SIDE_B);
      assertEquals(false, actual);

      actual =
         validity.isRelationTypeValid(CoreRelationTypes.Allocation_Requirement, CoreArtifactTypes.Component, SIDE_B);
      assertEquals(true, actual);
   }

   @Test
   public void testCheckRelationTypeValid() {
      when(artifactType.toString()).thenReturn("artType1");
      when(artifactType2.toString()).thenReturn("artType2");

      when(node.getArtifactType()).thenReturn(artifactType);
      when(node.getExceptionString()).thenReturn("node message");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(
         "Relation validity error for [node message] - ArtifactType [artType1] does not belong on side [SIDE_B] of relation [Allocation] - only items of type [Component] are allowed");
      validity.checkRelationTypeValid(CoreRelationTypes.Allocation_Requirement, node, SIDE_B);
   }

   @Test
   public void testCheckRelationTypeValidNoException() {
      when(node.getArtifactType()).thenReturn(CoreArtifactTypes.Component);
      ExpectedException.none();
      validity.checkRelationTypeValid(CoreRelationTypes.Allocation_Requirement, node, SIDE_B);
   }

}
