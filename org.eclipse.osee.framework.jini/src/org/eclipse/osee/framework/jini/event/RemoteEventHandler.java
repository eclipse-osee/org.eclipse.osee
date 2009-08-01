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
package org.eclipse.osee.framework.jini.event;

import java.rmi.RemoteException;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.event.old.IOseeRemoteSubscriber;

/**
 * Singleton class which provides an interface for all OSEE Remote Events to the event service.
 * 
 * @author David Diepenbrock
 */
public class RemoteEventHandler implements IServiceLookupListener, IOseeRemoteSubscriber {

   public void serviceAdded(ServiceItem serviceItem) {
   }

   public void serviceChanged(ServiceItem serviceItem) {
   }

   public void serviceRemoved(ServiceItem serviceItem) {
   }

   public boolean receiveEventType(String event) throws RemoteException {
      return false;
   }

   public boolean receiveEventGuid(String event) throws RemoteException {
      return false;
   }
}
