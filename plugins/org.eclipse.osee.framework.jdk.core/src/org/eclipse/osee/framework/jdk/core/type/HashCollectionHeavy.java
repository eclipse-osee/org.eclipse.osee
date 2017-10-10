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

import java.util.Collection;

/**
 * A Map of keys to multiple values. Collections of values are stored in the Map. The type of Collection can be
 * specified at construction, if desired. All Collections returned by methods are backed by the HashCollection, so
 * changes to the HashCollection are reflected in the Collection, and vice-versa. However, modifications to the
 * Collection outside of this class are generally discouraged because removal of the last item would then not guarantee
 * removal of the key.
 * <p>
 * The implementation is tied to the HashCollectionPlus class, providing a null Object for the "plus" object.
 *
 * @author David Diepenbrock
 */
@SuppressWarnings("rawtypes")
public class HashCollectionHeavy<K, V> extends HashCollectionPlus<K, V, Object> {

   private static IPlusProvider<Object> provider = new IPlusProvider<Object>() {
      @Override
      public Object newObject() {
         return null;
      }
   };

   /********************************************************************************************************************
    * Constructors
    *******************************************************************************************************************/

   /**
    * @see HashCollectionPlus#HashCollectionPlus(boolean, Class, int, float, IPlusProvider)
    */
   public HashCollectionHeavy(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity, float loadFactor) {
      super(isSynchronized, collectionType, initialCapacity, loadFactor, provider);
   }

   /**
    * @see HashCollectionPlus#HashCollectionPlus(boolean, Class, int, IPlusProvider)
    */
   public HashCollectionHeavy(boolean isSynchronized, Class<? extends Collection> collectionType, int initialCapacity) {
      super(isSynchronized, collectionType, initialCapacity, provider);
   }

   /**
    * @see HashCollectionPlus#HashCollectionPlus(boolean, Class, IPlusProvider)
    */
   public HashCollectionHeavy(boolean isSynchronized, Class<? extends Collection> collectionType) {
      super(isSynchronized, collectionType, provider);
   }

   /**
    * Creates an unsynchronized HashCollection using a default Collection type
    *
    * @see HashCollectionHeavy#HashCollection(boolean, Class, int, float)
    * @see HashCollectionPlus#DEFAULT_COLLECTION_TYPE
    */
   public HashCollectionHeavy(int initialCapacity, float loadFactor) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity, loadFactor);
   }

   /**
    * Creates an unsynchronized HashCollection using a default Collection type
    *
    * @see HashCollectionHeavy#HashCollection(boolean, Class, int)
    * @see HashCollectionPlus#DEFAULT_COLLECTION_TYPE
    */
   public HashCollectionHeavy(int initialCapacity) {
      this(false, DEFAULT_COLLECTION_TYPE, initialCapacity);
   }

   /**
    * Creates an unsynchronized HashCollection using a default Collection type
    *
    * @see HashCollectionHeavy#HashCollection(boolean, Class, int)
    * @see HashCollectionPlus#DEFAULT_COLLECTION_TYPE
    */
   public HashCollectionHeavy(boolean isSynchronized) {
      this(isSynchronized, DEFAULT_COLLECTION_TYPE);
   }

   /**
    * Creates an unsynchronized HashCollection using a default Collection type
    *
    * @see HashCollectionHeavy#HashCollection(boolean, Class)
    * @see HashCollectionPlus#DEFAULT_COLLECTION_TYPE
    */
   public HashCollectionHeavy() {
      this(false, DEFAULT_COLLECTION_TYPE);
   }
}
