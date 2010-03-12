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
package org.eclipse.osee.framework.jini.service.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostEntry extends FormmatedEntry {

   private static final long serialVersionUID = -2965321047363718068L;

   public String host;

   public HostEntry() {

      try {
         host = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException ex) {
         host = "Error Obtaining Host";
         ex.printStackTrace();
      }
   }

   public String getHost() {
      return host;
   }

   public String getFormmatedString() {
      return "Host : " + host;
   }
}
