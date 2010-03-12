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
package org.eclipse.osee.ote.service.core;

import java.util.Properties;
import org.eclipse.osee.framework.messaging.EndpointReceive;
import org.eclipse.osee.framework.messaging.Message;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OteClientEndpointReceive extends EndpointReceive {

   @Override
   public void dispose() {
   }

   @Override
   public void start(Properties properties) {
   }

   public void receivedMessage(Message message) {
      onReceive(message);
   }
   
}
