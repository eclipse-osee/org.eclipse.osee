/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.notify;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationNavigateItem extends XNavigateItemAction {

   /**
    * @param parent
    * @param name
    */
   public AtsNotificationNavigateItem(XNavigateItem parent) {
      this(parent, false);
   }

   public AtsNotificationNavigateItem(XNavigateItem parent, boolean sync) {
      super(parent, (sync ? "Sync - " : "") + "Process ATS Notifications", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      AtsNotificationCheckTreeDialog diag = new AtsNotificationCheckTreeDialog();
      if (diag.open() == 0) {
         if (diag.getSelectedAtsNotifications().size() == 0) {
            AWorkbench.popup("Error", "No Notifications Selected");
            return;
         }
         XResultData rd = new XResultData();
         if (diag.isSendNotifications()) {
            rd.addRaw(AHTML.bold("Notifications were sent"));
         } else {
            rd.addRaw("Report Only - Notifications were NOT sent");
         }
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Reason", "Description", "Id", "User(s)"}));
         for (IAtsNotification notify : diag.getSelectedAtsNotifications()) {
            for (OseeNotificationEvent event : notify.getNotificationEvents()) {
               rd.addRaw(AHTML.addRowMultiColumnTable(event.getType(), event.getDescription(), event.getId(),
                     Artifacts.semmicolonArts(event.getUsers())));
               if (diag.isSendNotifications()) {
                  OseeNotificationManager.addNotificationEvent(event);
               }
            }
         }
         rd.addRaw(AHTML.endMultiColumnTable());
         rd.report(getName());
         if (diag.isSendNotifications()) {
            OseeNotificationManager.sendNotifications();
            AWorkbench.popup("Complete", "Notifications Sent");
         }
      }
   }

}
