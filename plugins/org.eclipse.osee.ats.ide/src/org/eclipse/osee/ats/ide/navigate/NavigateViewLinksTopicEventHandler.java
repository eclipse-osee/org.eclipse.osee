/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.links.LinksNavigateViewItem;
import org.eclipse.osee.framework.ui.skynet.util.FrameworkEvents;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Handler for {@link FrameworkEvents.PERSONAL_WEB_PREFERENCES}</br>
 * Handler for {@link FrameworkEvents.GLOBAL_WEB_PREFERENCES}
 *
 * @author Donald G. Dunne
 */
public class NavigateViewLinksTopicEventHandler implements EventHandler {

   public static final long TOP_LINK_ID = 4561556789L;

   @Override
   public void handleEvent(Event event) {
      try {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               if (NavigateView.getNavigateView() != null && NavigateView.isAccessible()) {
                  LinksNavigateViewItem.clearAndReload();
               }
            }
         });
      } catch (Exception ex) {
         OseeLog.log(NavigateViewLinksTopicEventHandler.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String toString() {
      return String.format("%s for %s / %s", getClass(), FrameworkEvents.GLOBAL_WEB_PREFERENCES,
         FrameworkEvents.PERSONAL_WEB_PREFERENCES);
   }
}
