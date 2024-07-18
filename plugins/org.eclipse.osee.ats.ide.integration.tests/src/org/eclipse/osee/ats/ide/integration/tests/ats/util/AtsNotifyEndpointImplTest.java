/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.notify.TestEmail;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
      AtsNotifyEndpointApi notifyEp = AtsApiService.get().getServerEndpoints().getNotifyEndpoint();
      Assert.assertNotNull(notifyEp);

      TestEmail email = new TestEmail();
      email.setSubject("My Subject");
      email.setEmail("d@d.com");
      XResultData rd = notifyEp.sendTestEmail(email);
      Assert.assertTrue(rd.toString().contains(OseeEmail.DEFAULT_MAIL_SERVER_NOT_CONFIGURED));
   }

}
