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

import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.internal.AttributeTypeRestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * Test Case for {@link AttributeTypeRestrictionHandler}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeTypeRestrictionHandlerTest extends BaseRestrictionHandlerTest<AttributeTypeRestriction> {

   public AttributeTypeRestrictionHandlerTest() {
      super(new AttributeTypeRestrictionHandler(), MockModel.createAttributeTypeRestriction(),
         MockModel.createArtifactTypeRestriction());
   }

   @Test
   public void testProcessDataAttributeTypeNotApplicable() throws OseeCoreException {
      IAttributeType attributeType = CoreAttributeTypes.Name;

      XAttributeType attributeTypeRef =
         MockModel.createXAttributeType(attributeType.getGuid(), attributeType.getName());

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactData artData = createArtifactData(null, attributeType, wasIsAttributeTypeValidCalled, false);
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicable() throws OseeCoreException {
      IAttributeType attributeType = CoreAttributeTypes.Name;

      XAttributeType attributeTypeRef =
         MockModel.createXAttributeType(attributeType.getGuid(), attributeType.getName());

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactData artData = createArtifactData(null, attributeType, wasIsAttributeTypeValidCalled, true);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, attributeType, PermissionEnum.WRITE);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeBoundedNoMatch() throws OseeCoreException {
      IAttributeType attributeType = CoreAttributeTypes.Name;

      XAttributeType attributeTypeRef =
         MockModel.createXAttributeType(attributeType.getGuid(), attributeType.getName());

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Artifact;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      IArtifactType artifactType2 = CoreArtifactTypes.Requirement;
      ArtifactType artArtifactType = new ArtifactType(artifactType2.getGuid(), artifactType2.getName(), false);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactData artData = createArtifactData(artArtifactType, attributeType, wasIsAttributeTypeValidCalled, true);
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeMatch() throws OseeCoreException {
      IAttributeType attributeType = CoreAttributeTypes.Name;

      XAttributeType attributeTypeRef =
         MockModel.createXAttributeType(attributeType.getGuid(), attributeType.getName());

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      ArtifactType artArtifactType = new ArtifactType(artifactType.getGuid(), artifactType.getName(), false);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactData artData = createArtifactData(artArtifactType, attributeType, wasIsAttributeTypeValidCalled, true);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, attributeType, PermissionEnum.WRITE);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   @Test
   public void testProcessDataAttributeTypeIsApplicableArtifactTypeMatchWithInheritance() throws OseeCoreException {
      IAttributeType attributeType = CoreAttributeTypes.Name;

      XAttributeType attributeTypeRef =
         MockModel.createXAttributeType(attributeType.getGuid(), attributeType.getName());

      AttributeTypeRestriction restriction = MockModel.createAttributeTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setAttributeTypeRef(attributeTypeRef);

      IArtifactType artifactType = CoreArtifactTypes.Artifact;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());
      restriction.setArtifactTypeRef(artifactTypeRef);

      IArtifactType artifactType2 = CoreArtifactTypes.Requirement;
      ArtifactType artArtifactType = new ArtifactType(artifactType2.getGuid(), artifactType2.getName(), false);

      // Make expectedAccessObject inherit from ArtifactType
      Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
      superTypes.add(new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false));
      artArtifactType.setSuperTypes(superTypes);

      final MutableBoolean wasIsAttributeTypeValidCalled = new MutableBoolean(false);
      ArtifactData artData = createArtifactData(artArtifactType, attributeType, wasIsAttributeTypeValidCalled, true);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, attributeType, PermissionEnum.WRITE);
      Assert.assertTrue(wasIsAttributeTypeValidCalled.getValue());
   }

   private static ArtifactData createArtifactData(ArtifactType artifactType, final IAttributeType expectedAttributeType, final MutableBoolean wasIsAttributeTypeValidCalled, final boolean isTypeValid) {
      MockArtifactData artData = new MockArtifactData(GUID.create(), artifactType) {

         @Override
         public boolean isAttributeTypeValid(IAttributeType attributeType) {
            wasIsAttributeTypeValidCalled.setValue(true);
            Assert.assertEquals(expectedAttributeType, attributeType);
            return isTypeValid;
         }
      };
      return artData;
   }
}
