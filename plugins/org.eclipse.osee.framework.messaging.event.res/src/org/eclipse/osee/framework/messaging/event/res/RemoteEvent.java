/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;

/**
 * @author Donald G. Dunne
 */
public abstract class RemoteEvent {

   public RemoteNetworkSender1 getNetworkSender() {
      return null;
   }

}
