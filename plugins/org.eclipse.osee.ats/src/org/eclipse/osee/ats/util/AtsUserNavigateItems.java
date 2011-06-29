/*
 * Created on Jun 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.navigate.CreateNewUsersByNameItem;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.operation.PurgeUser;
import org.eclipse.osee.ats.operation.ReAssignATSObjectsToUser;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.user.perspective.IUserNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

public class AtsUserNavigateItems implements IUserNavigateItem {

   @Override
   public List<XNavigateItem> getNavigateItems(XNavigateItem parentItem) {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      if (AtsUtilCore.isAtsAdmin()) {
         items.add(new XNavigateItemBlam(parentItem, new PurgeUser(), FrameworkImage.X_RED));
         items.add(new XNavigateItemBlam(parentItem, new ReAssignATSObjectsToUser(), AtsImage.ACTION));
         items.add(new CreateNewUsersByNameItem(parentItem));
         try {
            new SearchNavigateItem(parentItem, new UserRelatedToAtsObjectSearch("Admin - Show User Related Objects",
               null, false, LoadView.WorldEditor));
            new SearchNavigateItem(parentItem, new UserRelatedToAtsObjectSearch("Show Active User Related Objects",
               null, true, LoadView.WorldEditor));
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return items;
   }
}
