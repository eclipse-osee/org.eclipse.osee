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
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class MockArtifactDataProvider implements ArtifactDataProvider {

   private final boolean isApplicable;
   private final Object expectedObject;
   private final ArtifactData artifactData;
   private boolean wasIsApplicableCalled;
   private boolean wasAsCastedObjectCalled;

   public MockArtifactDataProvider(boolean isApplicable, Object expectedObject, ArtifactData artifactData) {
      this.isApplicable = isApplicable;
      this.expectedObject = expectedObject;
      this.artifactData = artifactData;
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
   public ArtifactData asCastedObject(Object object) {
      wasAsCastedObjectCalled = true;
      Assert.assertEquals(expectedObject, object);
      return artifactData;
   }

   public boolean wasIsApplicableCalled() {
      return wasIsApplicableCalled;
   }

   public boolean wasAsCastedObjectCalled() {
      return wasAsCastedObjectCalled;
   }
}