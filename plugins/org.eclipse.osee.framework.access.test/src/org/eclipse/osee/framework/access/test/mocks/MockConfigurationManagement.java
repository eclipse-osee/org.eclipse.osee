/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class MockConfigurationManagement implements CmAccessControl {

   private final ArtifactToken expectedUser;
   private final Object expectedObject;
   private final boolean isApplicable;
   private final Collection<IAccessContextId> contextIds;
   private boolean wasIsApplicableCalled;
   private boolean wasGetContextIdCalled;

   public MockConfigurationManagement(ArtifactToken expectedUser, Object expectedObject, boolean isApplicable, Collection<IAccessContextId> contextIds) {
      super();
      this.expectedUser = expectedUser;
      this.expectedObject = expectedObject;
      this.isApplicable = isApplicable;
      this.contextIds = contextIds;
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      wasIsApplicableCalled = true;
      Assert.assertEquals(expectedUser, user);
      Assert.assertEquals(expectedObject, object);
      return isApplicable;
   }

   @Override
   public Collection<IAccessContextId> getContextId(ArtifactToken user, Object object) {
      wasGetContextIdCalled = true;
      Assert.assertEquals(expectedUser, user);
      Assert.assertEquals(expectedObject, object);
      return contextIds;
   }

   public boolean wasIsApplicableCalled() {
      return wasIsApplicableCalled;
   }

   public boolean wasGetContextIdCalled() {
      return wasGetContextIdCalled;
   }

}
