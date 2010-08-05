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

import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.access.internal.cm.CMAccessProvider;
import org.eclipse.osee.framework.core.services.ConfigurationManagementProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link CMAccessProvider}
 * 
 * @author Roberto E. Escobar
 */
public class CMAccessProviderTest {

   @Test
   public void testCMAccessProvider() {
      Assert.assertFalse(true);
      ConfigurationManagementProvider provider = null;
      IAccessProvider accessProvider = new CMAccessProvider(provider);
      //      accessProvider.computeAccess(userArtifact, objToCheck, accessData);
   }
}
