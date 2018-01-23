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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;

/**
 * @author Donald G. Dunne
 */
public final class HashCollection<K, V> extends HashCollectionGeneric<K, V, List<V>> {

   public HashCollection(boolean isSynchronized, int initialCapacity, float loadFactor, Supplier<List<V>> collectionSupplier) {
      super(isSynchronized, initialCapacity, loadFactor);
      setCollectionSupplier(collectionSupplier);
   }

   public HashCollection(boolean isSynchronized) {
      this(isSynchronized, 16);
   }

   public HashCollection(boolean isSynchronized, int initialCapacity) {
      this(isSynchronized, initialCapacity, 0.75f, isSynchronized ? Vector::new : ArrayList::new);
   }

   public HashCollection() {
      this(false);
   }

   public HashCollection(int initialCapacity) {
      this(false, initialCapacity, 0.75f, ArrayList::new);
   }

}