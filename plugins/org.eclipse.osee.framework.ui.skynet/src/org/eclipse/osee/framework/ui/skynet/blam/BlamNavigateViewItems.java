/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class BlamNavigateViewItems implements XNavigateItemProvider {

   @Override
   public boolean isApplicable() {
      return true;
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      Collection<IUserGroupArtifactToken> userGroups =
         OsgiUtil.getService(getClass(), OseeClient.class).userService().getMyUserGroups();
      for (AbstractBlam blam : BlamService.getBlams()) {
         if (blam.isApplicable()) {
            Collection<IUserGroupArtifactToken> blamUserGroups = blam.getUserGroups();
            if (blamUserGroups.isEmpty() || !Collections.setIntersection(blamUserGroups, userGroups).isEmpty()) {
               XNavigateItemBlam blamItem = new XNavigateItemBlam(blam);
               items.add(blamItem);
            }
         }
      }
      return items;
   }

}