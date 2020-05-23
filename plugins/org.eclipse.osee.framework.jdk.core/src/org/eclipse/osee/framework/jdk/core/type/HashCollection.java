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