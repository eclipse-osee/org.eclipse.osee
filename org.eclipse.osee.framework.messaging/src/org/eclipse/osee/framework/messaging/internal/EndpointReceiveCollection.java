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
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.messaging.EndpointReceive;

/**
 * @author Andrew M. Finkbeiner
 */
public class EndpointReceiveCollection {

   private final List<EndpointReceive> receivers;

   public EndpointReceiveCollection() {
      receivers = new CopyOnWriteArrayList<EndpointReceive>();
   }

   public synchronized boolean add(EndpointReceive endpoint) {
      if (receivers.contains(endpoint)) {
         return false;
      } else {
         return receivers.add(endpoint);
      }
   }

   public synchronized boolean remove(EndpointReceive endpoint) {
      return receivers.remove(endpoint);
   }

   public synchronized Collection<EndpointReceive> getAll() {
      return receivers;
   }

   public synchronized void dispose() {
      receivers.clear();
   }

}
