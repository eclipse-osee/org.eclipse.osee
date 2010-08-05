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

import org.eclipse.osee.framework.access.internal.cm.ConfigurationManagementProviderImpl;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.ConfigurationManagement;
import org.eclipse.osee.framework.core.services.ConfigurationManagementProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ConfigurationManagementProvider}{@link ConfigurationManagementProviderImpl}
 * 
 * @author Roberto E. Escobar
 */
public class ConfigurationManagementProviderTest {

   @Test
   public void testX() {
      Assert.assertTrue(false);
      //      ConfigurationManagementProvider provider = new ConfigurationManagementProviderImpl(ConfigurationManagement defaultCM, Collection<ConfigurationManagement> cmServices);
      //      ConfigurationManagement getCmService(IBasicArtifact<?> user, Object object)
   }

   private static final class MockConfigurationManagement implements ConfigurationManagement {

      @Override
      public boolean isApplicable(IBasicArtifact<?> userArtifact, Object object) {
         return true;
      }

      @Override
      public AccessContextId getContextId(IBasicArtifact<?> userArtifact, Object itemToCheck) throws OseeCoreException {
         return null;
      }
   }
}
