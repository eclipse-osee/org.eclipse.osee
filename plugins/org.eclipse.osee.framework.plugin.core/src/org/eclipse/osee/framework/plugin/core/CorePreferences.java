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

package org.eclipse.osee.framework.plugin.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.internal.Activator;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Roberto E. Escobar
 */
public class CorePreferences {

   private CorePreferences() {
   }

   public static final String INETADDRESS_KEY = Activator.PLUGIN_ID + ".preferences.InetAddressDefault";

   public static InetAddress getDefaultInetAddress() throws UnknownHostException {
      IEclipsePreferences prefs = ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID);
      String inetaddress = prefs.get(INETADDRESS_KEY, Network.getValidIP().getHostAddress());
      if (Strings.isValid(inetaddress)) {
         return InetAddress.getByName(inetaddress);
      }
      return Network.getValidIP();
   }

   /**
    * @param hostAddress
    * @throws UnknownHostException
    * @throws BackingStoreException
    */
   public static void setDefaultAddress(String hostAddress) {
      IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID);
      node.put(INETADDRESS_KEY, hostAddress);
      try {
         node.flush();
      } catch (BackingStoreException e) {
         throw new OseeCoreException("Error saving OSEE Preferences", e);
      }
   }
}
