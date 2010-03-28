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

/**
 * @author Donald G. Dunne
 */
public class NetworkMessageEventBase extends SkynetEventBase {

   private static final long serialVersionUID = 4199206432501390599L;
   private String message;

   public NetworkMessageEventBase(String message, NetworkSender networkSender) {
      super(networkSender);
      this.message = message;
   }

   protected String getMessage() {
      return message;
   }

   protected void setMessage(String message) {
      this.message = message;
   }
}
