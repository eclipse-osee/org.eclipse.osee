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

import org.eclipse.osee.distributed.AtomicNumber;

/**
 * @author Roberto E. Escobar
 */
public class AtomicNumberProxy implements AtomicNumber {

   private final com.hazelcast.core.AtomicNumber proxyObject;

   public AtomicNumberProxy(com.hazelcast.core.AtomicNumber proxyObject) {
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
   public long get() {
      return proxyObject.get();
   }

   @Override
   public void set(long newValue) {
      proxyObject.set(newValue);
   }

   @Override
   public long decrementAndGet() {
      return proxyObject.decrementAndGet();
   }

   @Override
   public long incrementAndGet() {
      return proxyObject.incrementAndGet();
   }

   @Override
   public long getAndAdd(long delta) {
      return proxyObject.getAndAdd(delta);
   }

   @Override
   public long addAndGet(long delta) {
      return proxyObject.addAndGet(delta);
   }

   @Override
   public long getAndSet(long newValue) {
      return proxyObject.getAndSet(newValue);
   }

   @Override
   public void lazySet(long newValue) {
      proxyObject.lazySet(newValue);
   }

   @Override
   public boolean compareAndSet(long expect, long update) {
      return proxyObject.compareAndSet(expect, update);
   }

   @Override
   public boolean weakCompareAndSet(long expect, long update) {
      return proxyObject.weakCompareAndSet(expect, update);
   }

}
