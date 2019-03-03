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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.dsl.integration.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.junit.Test;

/**
 * Test Case for {@link ArtifactTypeRestrictionHandler}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactTypeRestrictionHandlerTest extends BaseRestrictionHandlerTest<ArtifactTypeRestriction> {

   public ArtifactTypeRestrictionHandlerTest() {
      super(new ArtifactTypeRestrictionHandler(), MockModel.createArtifactTypeRestriction(),
         MockModel.createAttributeTypeRestriction());
   }

   @Test
   public void testProcessDataNotMatchesRestriction() {
      IArtifactType artifactType = CoreArtifactTypes.Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      ArtifactType artifactType2 = new ArtifactType(0L, "Some Artifact Type", false);
      MockArtifactProxy artData = new MockArtifactProxy(artifactType2);
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData, expectedScope);
   }

   @Test
   public void testProcessCreateAccessDetail() {
      IArtifactType artifactType = CoreArtifactTypes.Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      ArtifactType expectedAccessObject = new ArtifactType(artifactType.getGuid(), artifactType.getName(), false);
      MockArtifactProxy artData = new MockArtifactProxy(expectedAccessObject);

      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedAccessObject,
         PermissionEnum.WRITE, expectedScope);
   }

   @Test
   public void testProcessArtifactTypeInheritance() {
      IArtifactType artifactType = CoreArtifactTypes.Artifact;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getGuid(), artifactType.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      ArtifactType expectedAccessObject =
         new ArtifactType(CoreArtifactTypes.Requirement.getGuid(), CoreArtifactTypes.Requirement.getName(), false);

      MockArtifactProxy artData = new MockArtifactProxy(expectedAccessObject);
      Scope expectedScope = new Scope();
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData, expectedScope);

      // Make expectedAccessObject inherit from ArtifactType
      Set<ArtifactType> superTypes = new HashSet<>();
      superTypes.add(
         new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false));
      expectedAccessObject.setSuperTypes(superTypes);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedAccessObject,
         PermissionEnum.WRITE, expectedScope);
   }
}