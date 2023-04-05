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
      XResultData rd = new XResultData();
      NavigateItemCollector collector = new NavigateItemCollector(XNavigateItemProviders.getProviders(), null, rd);
      List<IUserGroupArtifactToken> myUserGroups = new ArrayList<>();
      myUserGroups.addAll(AtsApiService.get().userService().getMyUserGroups());
      List<XNavigateItem> navItems = collector.getComputedNavItems(myUserGroups);
      Assert.assertTrue(rd.toString(), rd.isSuccess());
      Assert.assertTrue(navItems.size() > 15);
      Assert.assertTrue(collector.getItems().size() > 215);

      rd = new XResultData();
      collector = new NavigateItemCollector(XNavigateItemProviders.getProviders(), null, rd);
      myUserGroups.add(CoreUserGroups.OseeAdmin);
      myUserGroups.add(AtsUserGroups.AtsAdmin);
      List<XNavigateItem> navItems2 = collector.getComputedNavItems(myUserGroups);
      Assert.assertTrue(rd.toString(), rd.isSuccess());
      Assert.assertTrue(navItems2.size() > 15);
      Assert.assertTrue(collector.getItems().size() > 220);

   }

}
