/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.access.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class MockConfigurationManagement implements CmAccessControl {

   private final ArtifactToken expectedUser;
   private final Object expectedObject;
   private final boolean isApplicable;
   private final Collection<AccessContextToken> contextIds;
   private boolean wasIsApplicableCalled;
   private boolean wasGetContextIdCalled;

   public MockConfigurationManagement(ArtifactToken expectedUser, Object expectedObject, boolean isApplicable, Collection<AccessContextToken> contextIds) {
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
   public Collection<AccessContextToken> getContextId(ArtifactToken user, Object object) {
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
