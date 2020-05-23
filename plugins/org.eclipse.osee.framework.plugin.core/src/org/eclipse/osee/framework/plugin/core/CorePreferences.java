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
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class CorePreferences {

   private CorePreferences() {
   }

   public static final String INETADDRESS_KEY = Activator.PLUGIN_ID + ".preferences.InetAddressDefault";

   @SuppressWarnings("deprecation")
   public static InetAddress getDefaultInetAddress() throws UnknownHostException {
      String inetaddress = Activator.getInstance().getPluginPreferences().getString(CorePreferences.INETADDRESS_KEY);
      if (Strings.isValid(inetaddress)) {
         return InetAddress.getByName(inetaddress);
      }
      return Network.getValidIP();
   }
}
