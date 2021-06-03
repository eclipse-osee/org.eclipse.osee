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

package org.eclipse.osee.framework.plugin.core.internal;

import java.net.UnknownHostException;
import java.util.logging.Level;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.CorePreferences;

/**
 * @author Roberto E. Escobar
 */
public class CorePreferenceInitializer extends AbstractPreferenceInitializer {

   @Override
   public void initializeDefaultPreferences() {
      IEclipsePreferences prefs = ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID);
      try {
         String currentValue = prefs.get(CorePreferences.INETADDRESS_KEY, "");
         if (Strings.isInValid(currentValue)) {
            String defaultNetworkValue = Network.getValidIP().getHostAddress();
            prefs.put(CorePreferences.INETADDRESS_KEY, defaultNetworkValue);
         }
      } catch (UnknownHostException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error initializing default inet address key", ex);
      }

   }
}
