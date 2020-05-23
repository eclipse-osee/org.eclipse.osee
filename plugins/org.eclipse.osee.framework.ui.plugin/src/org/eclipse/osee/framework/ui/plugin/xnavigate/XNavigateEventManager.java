/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.swt.Displays;

public class XNavigateEventManager {

   private static final Set<IXNavigateEventListener> listeners = new HashSet<>();

   public static void register(IXNavigateEventListener listener) {
      listeners.add(listener);
   }

   public static void itemRefreshed(final XNavigateItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IXNavigateEventListener listener : listeners) {
               try {
                  listener.refresh(item);
               } catch (Exception ex) {
                  OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }
}
