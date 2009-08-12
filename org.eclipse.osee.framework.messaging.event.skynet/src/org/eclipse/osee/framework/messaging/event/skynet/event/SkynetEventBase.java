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
package org.eclipse.osee.framework.messaging.event.skynet.event;

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;

/**
 * @author Robert A. Fisher
 */
public class SkynetEventBase implements ISkynetEvent {
   private static final long serialVersionUID = -5381855085551886510L;

   private final NetworkSender networkSender;

   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   public SkynetEventBase(NetworkSender networkSender) {
      this.networkSender = networkSender;
   }
}
