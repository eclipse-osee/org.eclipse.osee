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

import junit.framework.Assert;
import org.eclipse.osee.framework.core.dsl.integration.internal.ArtifactInstanceRestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * Test Case for {@link ArtifactInstanceRestrictionHandler}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactInstanceRestrictionHandlerTest extends BaseRestrictionHandlerTest<ArtifactInstanceRestriction> {

   public ArtifactInstanceRestrictionHandlerTest() {
      super(new ArtifactInstanceRestrictionHandler(), MockModel.createArtifactInstanceRestriction(),
         MockModel.createAttributeTypeRestriction());
   }

   @Test
   public void testProcessDataNotMatchesRestriction() throws OseeCoreException {

      String guid1 = GUID.create();

      XArtifactRef artifactRef = MockModel.createXArtifactRef(guid1, "TestArtifact");

      ArtifactInstanceRestriction restriction = MockModel.createArtifactInstanceRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactRef(artifactRef);

      String guid2 = GUID.create();

      Assert.assertFalse(guid1.equals(guid2));
      MockArtifactData artData = new MockArtifactData(guid2, null);

      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData);
   }

   @Test
   public void testProcessCreateAccessDetail() throws OseeCoreException {
      String guid1 = GUID.create();

      XArtifactRef artifactRef = MockModel.createXArtifactRef(guid1, "TestArtifact");

      ArtifactInstanceRestriction restriction = MockModel.createArtifactInstanceRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactRef(artifactRef);

      DefaultBasicArtifact expectedAccessObject = new DefaultBasicArtifact(1, guid1, "Another Artifact");

      MockArtifactData artData = new MockArtifactData(expectedAccessObject);

      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedAccessObject,
         PermissionEnum.WRITE);
   }

}
