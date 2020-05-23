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

package org.eclipse.osee.framework.messaging.internal.old;

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
      receivers = new CopyOnWriteArrayList<>();
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
