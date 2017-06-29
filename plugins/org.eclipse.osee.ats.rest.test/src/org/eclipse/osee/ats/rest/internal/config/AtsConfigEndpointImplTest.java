/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.rest.internal.notify.AtsServerService;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;

/**
 * Test unit for {@link AtsConfigEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsConfigEndpointImplTest {

   @org.junit.Test
   public void testAlive() throws OseeCoreException {
      AtsConfigEndpointApi configEp = AtsServerService.get().getConfigurationEndpoint();
      XResultData resultData = configEp.alive();
      Assert.assertEquals("Alive", resultData.getResults().iterator().next());
   }

}