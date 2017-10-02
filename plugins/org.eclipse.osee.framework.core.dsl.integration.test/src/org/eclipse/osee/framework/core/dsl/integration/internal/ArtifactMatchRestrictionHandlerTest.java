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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ArtifactMatchRestrictionHandler}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactMatchRestrictionHandlerTest extends BaseRestrictionHandlerTest<ArtifactMatchRestriction> {

   private static final MockArtifactMatchInterpreter matcher = new MockArtifactMatchInterpreter();

   public ArtifactMatchRestrictionHandlerTest() {
      super(new ArtifactMatchRestrictionHandler(matcher), MockModel.createArtifactMatchRestriction(),
         MockModel.createAttributeTypeRestriction());
   }

   @Test
   public void testProcessDataNotMatchesRestriction() {
      XArtifactMatcher artifactRef = MockModel.createXArtifactMatcherRef("TestArtifact");

      ArtifactMatchRestriction restriction = MockModel.createArtifactMatchRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactMatcherRef(artifactRef);

      matcher.setMatchesResult(false);

      MockArtifactProxy artData = new MockArtifactProxy(GUID.create(), null);
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(getRestrictionHandler(), restriction, artData, expectedScope);

      Assert.assertEquals(artifactRef, matcher.getMatcher());
      Assert.assertEquals(artData, matcher.getProxy());
   }

   @Test
   public void testProcessCreateAccessDetail() {
      XArtifactMatcher artifactRef = MockModel.createXArtifactMatcherRef("TestArtifact");

      ArtifactMatchRestriction restriction = MockModel.createArtifactMatchRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      restriction.setArtifactMatcherRef(artifactRef);

      matcher.setMatchesResult(true);
      ArtifactToken expectedAccessObject = ArtifactToken.valueOf(1, "Another Artifact", BranchId.SENTINEL);
      MockArtifactProxy artData = new MockArtifactProxy(expectedAccessObject);

      Scope expectedScope = new Scope();
      DslAsserts.assertAccessDetail(getRestrictionHandler(), restriction, artData, expectedAccessObject,
         PermissionEnum.WRITE, expectedScope);

      Assert.assertEquals(artifactRef, matcher.getMatcher());
      Assert.assertEquals(artData, matcher.getProxy());
   }

   private static final class MockArtifactMatchInterpreter extends ArtifactMatchInterpreter {

      private boolean matchesResult;
      private ArtifactProxy proxy;
      private XArtifactMatcher matcher;

      @Override
      public boolean matches(XArtifactMatcher matcher, ArtifactProxy proxy) {
         this.matcher = matcher;
         this.proxy = proxy;
         return matchesResult;
      }

      public ArtifactProxy getProxy() {
         return proxy;
      }

      public XArtifactMatcher getMatcher() {
         return matcher;
      }

      public void setMatchesResult(boolean matchesResult) {
         this.matchesResult = matchesResult;
      }
   }

}
