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
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.CorePreferences;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class CorePreferenceInitializer extends AbstractPreferenceInitializer {

   @Override
   public void initializeDefaultPreferences() {
      Preferences store = Activator.getInstance().getPluginPreferences();
      try {
         String defaultNetworkValue = Network.getValidIP().getHostAddress();
         store.setDefault(CorePreferences.INETADDRESS_KEY, defaultNetworkValue);
         String value = store.getString(CorePreferences.INETADDRESS_KEY);
         if (!Strings.isValid(value)) {
            store.setValue(CorePreferences.INETADDRESS_KEY, store.getDefaultString(CorePreferences.INETADDRESS_KEY));
         }
      } catch (UnknownHostException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error initializing default inet address key", ex);
      }
   }
}
