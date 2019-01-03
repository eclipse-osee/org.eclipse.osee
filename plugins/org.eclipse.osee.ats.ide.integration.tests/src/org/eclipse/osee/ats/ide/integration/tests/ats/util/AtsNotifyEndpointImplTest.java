/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AtsNotifyEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsNotifyEndpointImplTest {

   @Test
   public void testAtsNotify() {
      AtsNotifyEndpointApi notifyEndpoint = AtsClientService.getNotifyEndpoint();
      Assert.assertNotNull(notifyEndpoint);
   }

}
