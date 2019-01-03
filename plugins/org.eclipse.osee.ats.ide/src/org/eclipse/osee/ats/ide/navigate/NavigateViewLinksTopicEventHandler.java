/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.navigate;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.links.LinksNavigateViewItems;
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
               if (NavigateView.getNavigateView() != null) {
                  XNavigateItem linkItem = NavigateView.getNavigateView().getItem(TOP_LINK_ID, true);
                  LinksNavigateViewItems.reloadLinks(linkItem);
                  NavigateView.getNavigateView().refresh(linkItem);
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
