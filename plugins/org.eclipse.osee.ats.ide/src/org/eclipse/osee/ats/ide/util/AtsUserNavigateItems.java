/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.CreateNewUsersByNameItem;
import org.eclipse.osee.ats.ide.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.ide.operation.PurgeUser;
import org.eclipse.osee.ats.ide.operation.ReAssignATSObjectsToUser;
import org.eclipse.osee.ats.ide.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.user.IUserNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class AtsUserNavigateItems implements IUserNavigateItem {

   @Override
   public List<XNavigateItem> getNavigateItems(XNavigateItem parentItem, boolean admin, boolean inUserGroup) {
      List<XNavigateItem> items = new ArrayList<>();
      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         items.add(new XNavigateItemBlam(parentItem, new PurgeUser(), FrameworkImage.X_RED));
         items.add(new XNavigateItemBlam(parentItem, new ReAssignATSObjectsToUser(), AtsImage.ACTION));
         items.add(new CreateNewUsersByNameItem(parentItem));
         try {
            new SearchNavigateItem(parentItem, new UserRelatedToAtsObjectSearch("Admin - Show User Related Objects",
               null, false, LoadView.WorldEditor));
            new SearchNavigateItem(parentItem,
               new UserRelatedToAtsObjectSearch("Show Active User Related Objects", null, true, LoadView.WorldEditor));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return items;
   }
}
