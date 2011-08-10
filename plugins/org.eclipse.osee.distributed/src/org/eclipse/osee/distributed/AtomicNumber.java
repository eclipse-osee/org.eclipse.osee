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
package org.eclipse.osee.distributed;


/**
 * @author Roberto E. Escobar
 */
public interface AtomicNumber extends DistributedObject, HasName {

   long get();

   void set(long newValue);

   long decrementAndGet();

   long incrementAndGet();

   long getAndAdd(long delta);

   long addAndGet(long delta);

   long getAndSet(long newValue);

   void lazySet(long newValue);

   boolean compareAndSet(long expect, long update);

   boolean weakCompareAndSet(long expect, long update);
}