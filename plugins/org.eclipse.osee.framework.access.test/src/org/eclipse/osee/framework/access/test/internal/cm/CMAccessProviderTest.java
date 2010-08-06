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
package org.eclipse.osee.framework.access.test.internal.cm;

import java.util.Collection;
import org.eclipse.osee.framework.access.internal.cm.CMAccessProvider;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.junit.Test;

/**
 * Test Case for {@link CMAccessProvider}
 * 
 * @author Roberto E. Escobar
 */
public class CMAccessProviderTest {

   @Test
   public void testCMAccessProvider() {
      IBasicArtifact<?> expectedUser = null;
      Object expectedObject = null;
      //
      //      AccessData expectedAccessData = new AccessData();
      //
      //      AccessModel accessModel = new MockAccessModel();
      //      ConfigurationManagement cm =
      //         new MockCMWithAccessModel(accessModel, expectedUser, expectedObject, true,
      //            Collections.singleton((AccessContextId) CoreAccessContextIds.DEFAULT_SYSTEM_CONTEXT_ID));
      //      ConfigurationManagementProvider provider =
      //         new MockConfigurationManagementProvider(expectedUser, expectedObject, cm);
      //
      //      IAccessProvider accessProvider = new CMAccessProvider(provider);
      //      accessProvider.computeAccess(expectedUser, expectedObject, expectedAccessData);
   }

   private final static class MockAccessModel implements AccessModel {

      @Override
      public void computeAccess(AccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData) {
      }
   }

}
