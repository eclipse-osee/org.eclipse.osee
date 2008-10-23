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

package org.eclipse.osee.framework.skynet.core.preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Roberto E. Escobar
 */
public class PreferenceConstants {

   private PreferenceConstants() {

   }
   public static final String INETADDRESS_KEY = "org.eclipse.osee.framework.skynet.core.preferences.InetAddressDefault";
   public static final String WORDWRAP_KEY = "org.eclipse.osee.framework.skynet.core.preferences.WordWrap";
   public static final String SESSION_SERVER = "org.eclipse.osee.framework.skynet.core.preferences.SessionServer";
   public static final String OSEE_REMOTE_HTTP_SERVER = "osee.remote.http.server";

   public static InetAddress getDefaultInetAddress() throws UnknownHostException {
      IPreferenceStore prefStore = SkynetActivator.getInstance().getPreferenceStore();
      String inetaddress = prefStore.getString(PreferenceConstants.INETADDRESS_KEY);
      if (Strings.isValid(inetaddress)) {
         return InetAddress.getByName(inetaddress);
      }
      return Network.getValidIP();
   }
}
