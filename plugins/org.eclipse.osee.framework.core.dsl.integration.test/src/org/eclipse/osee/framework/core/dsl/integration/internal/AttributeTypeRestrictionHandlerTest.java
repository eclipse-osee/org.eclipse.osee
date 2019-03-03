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
package org.eclipse.osee.framework.core.dsl.integration.internal;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AttributeTypeRestrictionHandler}
 *
 * @author Roberto E. Escobar
 */
public class AttributeTypeRestrictionHandlerTest extends BaseRestrictionHandlerTest<AttributeTypeRestriction> {
   private static final XAttributeType attributeTypeRef = MockModel.createXAttributeType(Name, Name.getName());

   public AttributeTypeRestrictionHandlerTest() {
      super(new AttributeTypeRestrictionHandler(), MockModel.createAttributeTypeRestriction(),
         MockModel.createArtifactTypeRestriction());
   }

   @Test
   public void testProcessDataAttributeTypeNotApplicable() {
      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactProxy artifactProxy = createArtifactProxy(null, Name, wasIsAttributeTypeValidCalled, false);
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artifactProxy, expectedScope);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicable() {
      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactProxy artifactProxy = createArtifactProxy(null, Name, wasIsAttributeTypeValidCalled, true);
      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artifactProxy, Name, PermissionEnum.WRITE,
         expectedScope);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeBoundedNoMatch() {
      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Artifact;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getId(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      IArtifactType artifactType2 = CoreArtifactTypes.Requirement;
      ArtifactType artArtifactType = new ArtifactType(artifactType2.getId(), artifactType2.getName(), false);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactProxy artifactProxy = createArtifactProxy(artArtifactType, Name, wasIsAttributeTypeValidCalled, true);
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artifactProxy, expectedScope);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeMatch() {

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getId(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      ArtifactType artArtifactType = new ArtifactType(artifactType.getId(), artifactType.getName(), false);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactProxy artifactProxy = createArtifactProxy(artArtifactType, Name, wasIsAttributeTypeValidCalled, true);
      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artifactProxy, Name, PermissionEnum.WRITE,
         expectedScope);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeMatchWithInheritance() {
      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Artifact;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getId(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      IArtifactType artifactType2 = CoreArtifactTypes.Requirement;
      ArtifactType artArtifactType = new ArtifactType(artifactType2.getId(), artifactType2.getName(), false);

      // Make expectedAccessObject inherit from ArtifactType
      Set<ArtifactType> superTypes = new HashSet<>();
      superTypes.add(
         new ArtifactType(CoreArtifactTypes.Artifact.getId(), CoreArtifactTypes.Artifact.getName(), false));
      artArtifactType.setSuperTypes(superTypes);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactProxy artifactProxy = createArtifactProxy(artArtifactType, Name, wasIsAttributeTypeValidCalled, true);
      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artifactProxy, Name, PermissionEnum.WRITE,
         expectedScope);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   private static ArtifactProxy createArtifactProxy(ArtifactType artifactType, final AttributeTypeId expectedAttributeType, final MutableBoolean wasIsAttributeTypeValidCalled, final boolean isTypeValid) {
      MockArtifactProxy artData = new MockArtifactProxy(artifactType) {

         @Override
         public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
            wasIsAttributeTypeValidCalled.setValue(true);
            Assert.assertEquals(expectedAttributeType, attributeType);
            return isTypeValid;
         }
      };
      return artData;
   }
}
