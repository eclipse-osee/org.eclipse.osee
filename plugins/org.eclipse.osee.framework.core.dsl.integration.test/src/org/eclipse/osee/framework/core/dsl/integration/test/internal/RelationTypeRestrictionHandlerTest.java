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
package org.eclipse.osee.framework.core.dsl.integration.test.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter.AccessDetailCollector;
import org.eclipse.osee.framework.core.dsl.integration.internal.RelationTypeRestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * Test Case for {@link RelationTypeRestrictionHandler}
 * 
 * @author Roberto E. Escobar
 */
public class RelationTypeRestrictionHandlerTest extends BaseRestrictionHandlerTest<RelationTypeRestriction> {

   public RelationTypeRestrictionHandlerTest() {
      super(new RelationTypeRestrictionHandler(), MockModel.createRelationTypeRestriction(),
         MockModel.createAttributeTypeRestriction());
   }

   @Test
   public void testProcessDataRelationTypeNoMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);

      // Artifact Data has no relation types therefore relation type will not match
      MockArtifactData artData = new MockArtifactData(GUID.create(), null);
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
   }

   @Test
   public void testProcessDataRelationTypeMatchSideANoMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);
      restriction.setRestrictedToSide(XRelationSideEnum.SIDE_A);

      RelationType testRelationType =
         getTestRelationType(relationType, CoreArtifactTypes.Artifact, CoreArtifactTypes.SoftwareRequirement);

      IArtifactType artTypeToken1 = CoreArtifactTypes.SoftwareRequirement;
      ArtifactType artArtType = new ArtifactType(artTypeToken1.getGuid(), artTypeToken1.getName(), false);

      MockArtifactData artData =
         new MockArtifactData(GUID.create(), artArtType, null, null, Collections.singleton(testRelationType));
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
   }

   @Test
   public void testProcessDataRelationTypeMatchSideAMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);
      restriction.setRestrictedToSide(XRelationSideEnum.SIDE_A);

      RelationType testRelationType =
         getTestRelationType(relationType, CoreArtifactTypes.Artifact, CoreArtifactTypes.SoftwareRequirement);

      IArtifactType artTypeToken1 = CoreArtifactTypes.SoftwareRequirement;
      ArtifactType artArtType = new ArtifactType(artTypeToken1.getGuid(), artTypeToken1.getName(), false);
      Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
      superTypes.add(new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false));
      artArtType.setSuperTypes(superTypes);

      MockArtifactData artData =
         new MockArtifactData(GUID.create(), artArtType, null, null, Collections.singleton(testRelationType));
      RelationTypeSide expectedObject = new RelationTypeSide(testRelationType, RelationSide.SIDE_A);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedObject, PermissionEnum.WRITE);
   }

   @Test
   public void testProcessDataRelationTypeMatchSideBNoMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);
      restriction.setRestrictedToSide(XRelationSideEnum.SIDE_B);

      RelationType testRelationType =
         getTestRelationType(relationType, CoreArtifactTypes.SoftwareRequirement, CoreArtifactTypes.Artifact);

      IArtifactType artTypeToken1 = CoreArtifactTypes.SoftwareRequirement;
      ArtifactType artArtType = new ArtifactType(artTypeToken1.getGuid(), artTypeToken1.getName(), false);

      MockArtifactData artData =
         new MockArtifactData(GUID.create(), artArtType, null, null, Collections.singleton(testRelationType));
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
   }

   @Test
   public void testProcessDataRelationTypeMatchSideBMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);
      restriction.setRestrictedToSide(XRelationSideEnum.SIDE_B);

      RelationType testRelationType =
         getTestRelationType(relationType, CoreArtifactTypes.SoftwareRequirement, CoreArtifactTypes.Artifact);

      IArtifactType artTypeToken1 = CoreArtifactTypes.SoftwareRequirement;
      ArtifactType artArtType = new ArtifactType(artTypeToken1.getGuid(), artTypeToken1.getName(), false);
      Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
      superTypes.add(new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false));
      artArtType.setSuperTypes(superTypes);

      MockArtifactData artData =
         new MockArtifactData(GUID.create(), artArtType, null, null, Collections.singleton(testRelationType));
      RelationTypeSide expectedObject = new RelationTypeSide(testRelationType, RelationSide.SIDE_B);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedObject, PermissionEnum.WRITE);
   }

   @Test
   public void testProcessDataRelationTypeMatchBothMatch() throws OseeCoreException {
      IRelationType relationType = CoreRelationTypes.Default_Hierarchical__Child;
      XRelationType relationTypeRef = MockModel.createXRelationType(relationType.getGuid(), relationType.getName());

      RelationTypeRestriction restriction = MockModel.createRelationTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setRelationTypeRef(relationTypeRef);
      restriction.setRestrictedToSide(XRelationSideEnum.BOTH);

      RelationType testRelationType =
         getTestRelationType(relationType, CoreArtifactTypes.SoftwareRequirement, CoreArtifactTypes.Artifact);

      IArtifactType artTypeToken1 = CoreArtifactTypes.SoftwareRequirement;
      ArtifactType artArtType = new ArtifactType(artTypeToken1.getGuid(), artTypeToken1.getName(), false);
      Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
      superTypes.add(new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false));
      artArtType.setSuperTypes(superTypes);

      MockArtifactData artData =
         new MockArtifactData(GUID.create(), artArtType, null, null, Collections.singleton(testRelationType));
      RelationTypeSide expectedObject1 = new RelationTypeSide(testRelationType, RelationSide.SIDE_A);
      RelationTypeSide expectedObject2 = new RelationTypeSide(testRelationType, RelationSide.SIDE_B);

      final List<AccessDetail<?>> actualAccesses = new ArrayList<AccessDetail<?>>();
      AccessDetailCollector collector = new AccessDetailCollector() {

         @Override
         public void collect(AccessDetail<?> accessDetail) {
            Assert.assertNotNull(accessDetail);
            actualAccesses.add(accessDetail);
         }
      };

      getRestrictionHandler().process(restriction, artData, collector);

      AccessDetail<?> actualAccess = actualAccesses.get(0);
      Assert.assertEquals(actualAccess.getPermission(), PermissionEnum.WRITE);
      Assert.assertEquals(expectedObject1, actualAccess.getAccessObject());

      actualAccess = actualAccesses.get(1);
      Assert.assertEquals(actualAccess.getPermission(), PermissionEnum.WRITE);
      Assert.assertEquals(expectedObject2, actualAccess.getAccessObject());
   }

   private static RelationType getTestRelationType(IRelationType relationType, IArtifactType aArtTypeToken, IArtifactType bArtTypeToken) {
      ArtifactType aArtArtType = new ArtifactType(aArtTypeToken.getGuid(), aArtTypeToken.getName(), false);
      ArtifactType bArtArtType = new ArtifactType(bArtTypeToken.getGuid(), bArtTypeToken.getName(), false);
      return new RelationType(relationType.getGuid(), relationType.getName(), "sideA_" + aArtArtType.getName(),
         "sideB_" + bArtArtType.getName(), aArtArtType, bArtArtType, RelationTypeMultiplicity.MANY_TO_MANY,
         RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid());
   }
}
