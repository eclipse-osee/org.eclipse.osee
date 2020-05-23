/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.dsl.integration.mocks;

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class MockArtifactDataProvider implements ArtifactDataProvider {

   private final boolean isApplicable;
   private final Object expectedObject;
   private final ArtifactProxy artifactProxy;
   private boolean wasIsApplicableCalled;
   private boolean wasAsCastedObjectCalled;

   public MockArtifactDataProvider(boolean isApplicable, Object expectedObject, ArtifactProxy artifactProxy) {
      this.isApplicable = isApplicable;
      this.expectedObject = expectedObject;
      this.artifactProxy = artifactProxy;
      reset();
   }

   public void reset() {
      wasIsApplicableCalled = false;
      wasAsCastedObjectCalled = false;
   }

   @Override
   public boolean isApplicable(Object object) {
      wasIsApplicableCalled = true;
      Assert.assertEquals(expectedObject, object);
      return isApplicable;
   }

   @Override
   public ArtifactProxy asCastedObject(Object object) {
      wasAsCastedObjectCalled = true;
      Assert.assertEquals(expectedObject, object);
      return artifactProxy;
   }

   public boolean wasIsApplicableCalled() {
      return wasIsApplicableCalled;
   }

   public boolean wasAsCastedObjectCalled() {
      return wasAsCastedObjectCalled;
   }
}