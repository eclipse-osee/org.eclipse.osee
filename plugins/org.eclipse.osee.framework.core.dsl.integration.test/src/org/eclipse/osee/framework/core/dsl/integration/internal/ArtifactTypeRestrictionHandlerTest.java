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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSpecRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Requirement;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.dsl.integration.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.Scope;
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
      ArtifactTypeToken artifactType = Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getId(), artifactType.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      MockArtifactProxy artData = new MockArtifactProxy(CoreArtifactTypes.Folder);
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData, expectedScope);
   }

   @Test
   public void testProcessCreateAccessDetail() {
      ArtifactTypeToken artifactType = Requirement;
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(artifactType.getId(), artifactType.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      MockArtifactProxy artData = new MockArtifactProxy(artifactType);

      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, artifactType, PermissionEnum.WRITE,
         expectedScope);
   }

   @Test
   public void testProcessArtifactTypeInheritance() {
      XArtifactType artifactTypeRef = MockModel.createXArtifactType(Requirement.getId(), Requirement.getName());

      ArtifactTypeRestriction restriction = MockModel.createArtifactTypeRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactTypeRef(artifactTypeRef);

      MockArtifactProxy artData = new MockArtifactProxy(Artifact);
      Scope expectedScope = new Scope();
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData, expectedScope);

      MockArtifactProxy reqArtData = new MockArtifactProxy(AbstractSpecRequirement);
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, reqArtData, AbstractSpecRequirement,
         PermissionEnum.WRITE, expectedScope);
   }
}