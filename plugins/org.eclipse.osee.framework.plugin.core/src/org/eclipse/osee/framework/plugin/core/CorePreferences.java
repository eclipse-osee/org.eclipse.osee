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
