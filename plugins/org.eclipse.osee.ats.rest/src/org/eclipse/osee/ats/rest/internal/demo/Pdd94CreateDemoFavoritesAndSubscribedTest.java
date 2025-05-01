/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd94CreateDemoFavoritesAndSubscribedTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd94CreateDemoFavoritesAndSubscribedTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      assertEquals(3, atsApi.getQueryService().getFavorites(DemoUsers.Joe_Smith).size());
      assertEquals(0, atsApi.getQueryService().getFavorites(DemoUsers.Kay_Jones).size());

      assertEquals(1, atsApi.getQueryService().getSubscribed(DemoUsers.Joe_Smith).size());
      assertEquals(0, atsApi.getQueryService().getSubscribed(DemoUsers.Kay_Jones).size());

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
