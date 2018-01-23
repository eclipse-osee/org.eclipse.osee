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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public final class HashCollectionSet<K, V> extends HashCollectionGeneric<K, V, Set<V>> {
   public HashCollectionSet(boolean isSynchronized, int initialCapacity, float loadFactor, Supplier<Set<V>> collectionSupplier) {
      super(isSynchronized, initialCapacity, loadFactor);
      setCollectionSupplier(collectionSupplier);
   }

   public HashCollectionSet(boolean isSynchronized, int initialCapacity, Supplier<Set<V>> collectionSupplier) {
      this(isSynchronized, initialCapacity, 0.75f, collectionSupplier);
   }

   public HashCollectionSet(boolean isSynchronized, Supplier<Set<V>> collectionSupplier) {
      this(isSynchronized, 16, 0.75f, collectionSupplier);
   }

   public HashCollectionSet(Supplier<Set<V>> collectionSupplier) {
      this(false, 16, 0.75f, collectionSupplier);
   }
}