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

package org.eclipse.osee.ats.ide.ev;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchWorkPackageSearchItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class EvNavigateItems {

   public static void createSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem evItems = new XNavigateItem(parent, "Earned Value", AtsImage.E_BOXED);
         new XNavigateItemAction(evItems, new OpenWorkPackageByIdAction(), AtsImage.WORK_PACKAGE);
         new WorkPackageConfigReport(evItems);
         new WorkPackageQBDReport(evItems);
         new SearchNavigateItem(evItems, new AtsSearchWorkPackageSearchItem());
         items.add(evItems);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Goals section");
      }
   }

}
