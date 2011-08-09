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
package org.eclipse.osee.cluster.admin.hazelcast.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.eclipse.osee.distributed.DistributedLock;

/**
 * @author Roberto E. Escobar
 */
public class DistributedLockProxy implements DistributedLock {

   private final com.hazelcast.core.ILock proxyObject;

   public DistributedLockProxy(com.hazelcast.core.ILock proxyObject) {
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
   public Object getObjectLocked() {
      return proxyObject.getLockObject();
   }

   @Override
   public void lock() {
      proxyObject.lock();
   }

   @Override
   public void lockInterruptibly() throws InterruptedException {
      proxyObject.lockInterruptibly();
   }

   @Override
   public boolean tryLock() {
      return proxyObject.tryLock();
   }

   @Override
   public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      return proxyObject.tryLock(time, unit);
   }

   @Override
   public void unlock() {
      proxyObject.unlock();
   }

   @Override
   public Condition newCondition() {
      return proxyObject.newCondition();
   }

}
