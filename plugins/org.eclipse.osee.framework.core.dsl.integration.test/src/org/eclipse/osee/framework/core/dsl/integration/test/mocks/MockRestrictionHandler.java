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

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class MockRestrictionHandler implements RestrictionHandler<Object> {

   private final ObjectRestriction expectedObjectRestriction;
   private final ArtifactData expectedArtifactData;
   private final AccessDetailCollector expectedCollector;
   private boolean wasProcessCalled;

   public MockRestrictionHandler(ObjectRestriction expectedObjectRestriction, ArtifactData expectedArtifactData, AccessDetailCollector expectedCollector) {
      super();
      this.expectedObjectRestriction = expectedObjectRestriction;
      this.expectedArtifactData = expectedArtifactData;
      this.expectedCollector = expectedCollector;
      reset();
   }

   @Override
   public Object asCastedObject(ObjectRestriction objectRestriction) {
      return null;
   }

   public void reset() {
      wasProcessCalled = false;
   }

   public boolean wasProcessCalled() {
      return wasProcessCalled;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactData artifactData, AccessDetailCollector collector) {
      wasProcessCalled = true;
      Assert.assertEquals(expectedObjectRestriction, objectRestriction);
      Assert.assertEquals(expectedArtifactData, artifactData);
      Assert.assertEquals(expectedCollector, collector);
   }
}