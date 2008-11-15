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

package org.eclipse.osee.framework.core.client.internal;

import java.net.UnknownHostException;
import java.util.logging.Level;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.CorePreferences;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CorePreferenceInitializer extends AbstractPreferenceInitializer {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
    */
   public void initializeDefaultPreferences() {
      Preferences store = CoreClientActivator.getInstance().getPluginPreferences();
      try {
         String defaultNetworkValue = Network.getValidIP().getHostAddress();
         store.setDefault(CorePreferences.INETADDRESS_KEY, defaultNetworkValue);
         String value = store.getString(CorePreferences.INETADDRESS_KEY);
         if (!Strings.isValid(value)) {
            store.setValue(CorePreferences.INETADDRESS_KEY, store.getDefaultString(CorePreferences.INETADDRESS_KEY));
         }
      } catch (UnknownHostException ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, "Error initializing default inet address key", ex);
      }
   }
}
