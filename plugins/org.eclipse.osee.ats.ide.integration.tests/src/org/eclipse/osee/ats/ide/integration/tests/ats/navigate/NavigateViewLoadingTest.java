/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.NavigateView;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.NavigateItemCollector;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProviders;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link NavigateView}
 *
 * @author Donald G. Dunne
 */
public class NavigateViewLoadingTest {

   @Test
   public void test() {

      XResultData rd1 = new XResultData();
      NavigateItemCollector collector1 = new NavigateItemCollector(XNavigateItemProviders.getProviders(), null, rd1);
      List<IUserGroupArtifactToken> myUserGroups = new ArrayList<>();
      myUserGroups.addAll(AtsApiService.get().userService().getMyUserGroups());
      List<XNavigateItem> navItems = collector1.getComputedNavItems(myUserGroups);
      Assert.assertTrue(rd1.toString(), rd1.isSuccess());
      Assert.assertTrue(navItems.size() > 22);
      List<XNavigateItem> items1 = collector1.getItems();
      Assert.assertTrue("Expected > 200, was " + items1.size(), items1.size() > 200);

      XResultData rd2 = new XResultData();
      NavigateItemCollector collector2 = new NavigateItemCollector(XNavigateItemProviders.getProviders(), null, rd2);
      // Adding admin groups should bump top and total items
      myUserGroups.add(CoreUserGroups.OseeAdmin);
      myUserGroups.add(AtsUserGroups.AtsAdmin);
      List<XNavigateItem> navItems2 = collector2.getComputedNavItems(myUserGroups);
      Assert.assertTrue(rd2.toString(), rd2.isSuccess());
      Assert.assertTrue(navItems2.size() > 24);

   }

}
