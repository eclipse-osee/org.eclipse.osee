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
package org.eclipse.osee.cluster.hazelcast.internal;

import org.eclipse.osee.distributed.DistributedId;

/**
 * @author Roberto E. Escobar
 */
public class DistributedIdProxy implements DistributedId {

   private final com.hazelcast.core.IdGenerator proxyObject;

   public DistributedIdProxy(com.hazelcast.core.IdGenerator proxyObject) {
      super();
      this.proxyObject = proxyObject;
   }

   @Override
   public Object getId() {
      return proxyObject.getId();
   }

   @Override
   public void dispose() {
      proxyObject.destroy();
   }

   @Override
   public String getName() {
      return proxyObject.getName();
   }

   @Override
   public long newId() {
      return proxyObject.newId();
   }

}