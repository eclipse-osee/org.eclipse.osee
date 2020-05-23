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

package org.eclipse.osee.framework.jdk.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Roberto E. Escobar
 */
public class Network {

   public static InetAddress getValidIP() throws UnknownHostException {
      InetAddress[] addrs;
      // Cannot just grab the localhost address because the user might have more than one NIC.
      addrs = getLocalNetworkAdapters();

      for (int j = 0; j < addrs.length; j++) {
         if (!addrs[j].getHostAddress().matches("192.*")) {
            return addrs[j];
         }
      }
      return addrs[0];
   }

   public static InetAddress[] getLocalNetworkAdapters() throws UnknownHostException {
      return InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
   }
}
