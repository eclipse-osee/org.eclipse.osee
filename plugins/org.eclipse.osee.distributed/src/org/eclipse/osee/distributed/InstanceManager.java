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

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * @author Roberto E. Escobar
 */
public interface InstanceManager {

   AtomicNumber getAtomicNumber(String name);

   <E> BlockingQueue<E> getQueue(String name);

   <E> Set<E> getSet(String name);

   <E> List<E> getList(String name);

   <K, V> DistributedMap<K, V> getMap(String name);

   <K, V> DistributedMultiMap<K, V> getMultiMap(String name);

   DistributedId getIdGenerator(String name);

   DistributedLock getLock(Object key);

}
